#!/usr/bin/bash

# Variabelen
DB_CONTAINER_NAME="pg_backup"
DB_NAME="voornameninliedjes"
DB_USER="postgres"
DB_PASSWORD="secret"
ROLES_BACKUP_FILE=$2
DB_BACKUP_FILE=$1

if [ -z "$DB_BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file> <roles_file>"
    exit 1
fi

if [ -z "$DB_BACKUP_FILE" ] || [ -z "$ROLES_BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file> <roles_file>"
    exit 1
fi

# Start de PostgreSQL-container
echo "Starting PostgreSQL container..."
docker run -d --name $DB_CONTAINER_NAME \
  -e POSTGRES_USER=$DB_USER \
  -e POSTGRES_PASSWORD=$DB_PASSWORD \
  -e POSTGRES_DB=$DB_NAME \
  -p 5432:5432 \
  postgres:17.5-bookworm

# Wacht even totdat de database volledig gestart is
echo "Waiting for PostgreSQL to start..."
sleep 10  # Verhoog indien nodig de wachttijd

# Importeer de rollen en gebruikers vanuit het SQL-bestand
echo "Importing roles and users..."
echo "$ROLES_BACKUP_FILE"
docker cp $ROLES_BACKUP_FILE $DB_CONTAINER_NAME:/
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -f $ROLES_BACKUP_FILE

# Importeer de database backup in de database
echo "Restoring the database from backup..."
docker cp $DB_BACKUP_FILE $DB_CONTAINER_NAME:/
docker exec -i $DB_CONTAINER_NAME pg_restore -U $DB_USER -d $DB_NAME $DB_BACKUP_FILE

# Resetting passwords locally
echo "Resetting passwords for accounts"
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "ALTER ROLE vil_admin WITH PASSWORD 'secret'"
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "ALTER ROLE vil_app WITH PASSWORD 'secret'"

echo "Started local database with data."

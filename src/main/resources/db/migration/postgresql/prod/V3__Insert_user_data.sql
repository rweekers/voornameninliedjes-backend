CREATE EXTENSION pgcrypto;

insert into users(username, password)
values ('temp', crypt('secret', gen_salt('bf', 10)));

insert into USER_ROLES(name, user_id)
VALUES ('ADMIN', (select id from users where username = 'temp'));
insert into USER_ROLES(name, user_id)
VALUES ('OWNER', (select id from users where username = 'temp'));

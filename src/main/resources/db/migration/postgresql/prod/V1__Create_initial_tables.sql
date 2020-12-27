create type SONG_STATUS AS ENUM ('SHOW', 'IN_PROGRESS', 'TO_BE_DELETED');

create table ARTISTS
(
    ID   bigserial not null primary key,
    NAME text      not null unique
);

create table SONGS
(
    ID           bigserial not null primary key,
    ARTIST_ID    bigint    not null references ARTISTS (id),
    TITLE        text      not null,
    NAME         text      not null,
    ARTIST_IMAGE text,
    BACKGROUND   text,
    YOUTUBE      text,
    SPOTIFY      text,
    STATUS       song_status,
    MONGO_ID     text, -- for migration purposes
    unique (artist_id, title),
    check (strpos(title, name) > 0)
);

-- GRANT SELECT ON ALL TABLES IN SCHEMA vil TO vil;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA vil TO vil;

GRANT USAGE ON SCHEMA vil TO vil_app;

-- Set default rights for new objects
ALTER DEFAULT PRIVILEGES IN SCHEMA vil GRANT USAGE ON SEQUENCES TO vil_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA vil GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO vil_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA vil GRANT EXECUTE ON FUNCTIONS TO vil_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA vil GRANT SELECT ON TABLES TO vil_app;

-- Grant rights on current objects
GRANT USAGE ON ALL SEQUENCES IN SCHEMA vil TO vil_app;
GRANT SELECT,INSERT,DELETE,UPDATE ON ALL TABLES IN SCHEMA vil TO vil_app;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA vil TO vil_app;
GRANT SELECT ON ALL TABLES IN SCHEMA vil TO vil_app;
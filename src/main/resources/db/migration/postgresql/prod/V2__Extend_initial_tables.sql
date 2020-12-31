create table ARTIST_WIKIMEDIA_PHOTOS
(
    ID          bigserial not null primary key,
    URL         text      not null,
    ATTRIBUTION text      not null,
    ARTIST_ID   bigint    not null references ARTISTS (id),
    unique (URL, ARTIST_ID)
);

create table ARTIST_FLICKR_PHOTOS
(
    ID        bigserial not null primary key,
    FLICKR_ID text      not null,
    ARTIST_ID bigint    not null references ARTISTS (id),
    unique (FLICKR_ID, ARTIST_ID)
);

create table SONG_SOURCES
(
    ID       bigserial not null primary key,
    URL      text      not null,
    NAME     text      not null,
    SONG_ID  bigint references SONGS (id),
    SONG_KEY bigint,
    unique (URL, SONG_ID)
);

create table ARTIST_SOURCES
(
    ID        bigserial not null primary key,
    URL       text      not null,
    NAME      text      not null,
    ARTIST_ID bigint references ARTISTS (id),
    unique (URL, ARTIST_ID)
);

create table USERS
(
    ID       bigserial not null primary key,
    USERNAME text      not null unique,
    PASSWORD text,
    HUMAN    boolean   not null default true,
    MONGO_ID text -- for migration purposes,
        check ((human AND PASSWORD is not null) OR (
            not human AND PASSWORD is null
            ))
);

create table ROLES
(
    ID      bigserial not null primary key,
    NAME    text      not null,
    USER_ID bigint    not null references USERS (id),
    unique (NAME, USER_ID)
);

create table SONG_LOG_ENTRIES
(
    ID       bigserial                not null primary key,
    DATE     timestamp with time zone not null,
    USERNAME text                     not null,
    SONG_ID  bigint references SONGS (id),
    SONG_KEY bigint
);

create table ARTIST_LOG_ENTRIES
(
    ID         bigserial                not null primary key,
    DATE       timestamp with time zone not null,
    USERNAME   text                     not null,
    ARTIST_ID  bigint references ARTISTS (id),
    ARTIST_KEY bigint
);

alter table ARTISTS
    ADD COLUMN background text;

alter table SONGS
    alter COLUMN status SET NOT NULL;

create index on songs (lower(name));

-- Add implicit cast for postgres enum
CREATE CAST (varchar AS SONG_STATUS) WITH INOUT AS IMPLICIT;
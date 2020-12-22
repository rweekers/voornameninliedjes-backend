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
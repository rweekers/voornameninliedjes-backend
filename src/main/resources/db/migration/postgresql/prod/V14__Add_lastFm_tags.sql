create table SONG_LAST_FM_TAGS
(
    ID       bigserial                not null primary key,
    NAME     text                     not null,
    URL      text                     not null,
    SONG_ID  bigint references SONGS (id),
    SONG_KEY bigint
);
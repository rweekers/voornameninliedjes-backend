create table SONG_WIKIMEDIA_PHOTOS
(
    ID          bigserial not null primary key,
    URL         text      not null,
    ATTRIBUTION text      not null,
    SONG_ID   bigint    not null references SONGS (id),
    unique (URL, SONG_ID)
);
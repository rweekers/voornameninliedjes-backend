CREATE TABLE SONGS_ARTISTS
(
    SONG            bigint  not null references SONGS (id),
    ARTIST          bigint  not null references ARTISTS (id),
    ORIGINAL_ARTIST boolean not null,
    primary key (SONG, ARTIST)
);

ALTER TABLE SONGS
    DROP COLUMN ARTIST_ID;
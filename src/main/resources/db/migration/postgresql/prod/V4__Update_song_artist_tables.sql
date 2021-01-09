CREATE TABLE SONGS_ARTISTS
(
    SONG            bigint,
    ARTIST          bigint,
    ORIGINAL_ARTIST boolean default true,
    primary key (SONG, ARTIST)
);

ALTER TABLE SONGS
    DROP COLUMN ARTIST_ID;
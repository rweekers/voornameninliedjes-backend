ALTER TABLE SONGS
    ADD COLUMN ARTIST_ID    bigint  references ARTISTS (id);

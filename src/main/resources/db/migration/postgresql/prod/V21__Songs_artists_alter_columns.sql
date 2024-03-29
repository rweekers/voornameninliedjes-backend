ALTER TABLE SONGS_ARTISTS
    ADD COLUMN SONGS    bigint  references SONGS (id),
    ADD COLUMN ARTISTS    bigint  references ARTISTS (id);

UPDATE SONGS_ARTISTS set SONGS = SONG;
UPDATE SONGS_ARTISTS set ARTISTS = ARTIST;

ALTER TABLE SONGS_ARTISTS DROP CONSTRAINT songs_artists_pkey;

ALTER TABLE SONGS_ARTISTS ADD CONSTRAINT songs_artists_pkey PRIMARY KEY (songs, artists);

ALTER TABLE SONGS_ARTISTS DROP CONSTRAINT songs_artists_song_fkey;
ALTER TABLE SONGS_ARTISTS DROP CONSTRAINT songs_artists_artist_fkey;

ALTER TABLE SONGS_ARTISTS ALTER COLUMN SONG DROP NOT NULL;
ALTER TABLE SONGS_ARTISTS ALTER COLUMN ARTIST DROP NOT NULL;

ALTER TABLE SONGS_ARTISTS ALTER COLUMN SONGS SET NOT NULL;
ALTER TABLE SONGS_ARTISTS ALTER COLUMN ARTISTS SET NOT NULL;
update songs s
set artist_id = (select sa.artists
                 from songs s2
                          inner join songs_artists sa on s2.id = sa.songs
                 where s.id = s2.id);

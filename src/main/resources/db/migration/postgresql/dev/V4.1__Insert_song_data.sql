insert into artists(name)
values ('Michael Jackson');
insert into songs(title, name, status, background, youtube, spotify)
values ('Ben', 'Ben', 'SHOW', 'Background on Ben', 's0u8iA6bJTo', '3vZMkLS1jP7NdNhzqGfUSW');
insert into songs(title, name, status, background, youtube, spotify)
values ('Dirty Diana', 'Diana', 'SHOW', 'Background on Dirty Diana', 'yUi_S6YWjZw', '6JZYMxvcoeLD4IifJPvDux');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Ben'), (select id from artists where name = 'Michael Jackson'), true);
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Dirty Diana'), (select id from artists where name = 'Michael Jackson'), true);
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/3/31/Michael_Jackson_in_1988.jpg', 'Zoran Veselinovic, CC BY-SA 2.0 <https://creativecommons.org/licenses/by-sa/2.0>, via Wikimedia Commons', (select id from artists where name = 'Michael Jackson'));
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/0/04/Michael_Jackson_1984.jpg', 'White House Photo Office, Public domain, via Wikimedia Commons', (select id from artists where name = 'Michael Jackson'));

insert into artists(name)
values('Rolling Stones');
insert into songs(title, name, status, background, youtube, spotify)
values ('Angie', 'Angie', 'SHOW', 'Background on Angie', 'RcZn2-bGXqQ', '1GcVa4jFySlun4jLSuMhiq');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Angie'), (select id from artists where name = 'Rolling Stones'), true);
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/a/af/Stones_members_montage2.jpg', 'Larry Rogers (1972 Jagger photo from File:Jagger-early Stones.jpg); User:Machocarioca (1995 Keith Richards photo from File:KeithR2.JPG); Catharine Anderson (October 1981 photo from File:Ron-Wood in CA.jpg); Patrick Baumbach (2006 photo from File:Charlie Watts Hannover 19-07-2006.jpg), CC BY 3.0 <https://creativecommons.org/licenses/by/3.0>, via Wikimedia Commons', (select id from artists where name = 'Rolling Stones'));
insert into song_sources(url, name, song_id, song_key)
values ('https://nl.wikipedia.org/wiki/Angie_(nummer)', 'Wikipedia over ''Angie''', (select id from songs where title = 'Angie'), 0);
insert into song_log_entries(date, username, song_id, song_key)
values ('2021-11-13 15:40:00+01', 'Parser', (select id from songs where title = 'Angie'), 1);
insert into song_log_entries(date, username, song_id, song_key)
values ('2021-11-13 16:43:00+01', 'Temp', (select id from songs where title = 'Angie'), 0);

insert into artists(name)
values ('Paul Simon');
insert into songs(title, name, status, background, youtube, spotify)
values ('You can call me Al', 'Al', 'SHOW', 'Background on You Can Call Me All', 'uq-gYOrU8bA', '0qxYx4F3vm1AOnfux6dDxP');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'You can call me Al'), (select id from artists where name = 'Paul Simon'), true);
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/2/2d/Paul_Simon_in_1982.jpg', 'Nationaal Archief, Den Haag, Rijksfotoarchief: Fotocollectie Algemeen Nederlands Fotopersbureau (ANEFO), 1945-1989 - negatiefstroken zwart/wit, nummer toegang 2.24.01.05, bestanddeelnummer 932-2092, CC0, via Wikimedia Commons', (select id from artists where name = 'Paul Simon'));
insert into artist_flickr_photos(flickr_id, artist_id)
values ('5919550669', (select id from artists where name = 'Paul Simon'));
insert into song_sources(url, name, song_id, song_key)
values ('https://nl.wikipedia.org/wiki/You_Can_Call_Me_Al', 'Wikipedia over ''You Can Call Me Al''', (select id from songs where title = 'You can call me Al'), 0);
insert into song_log_entries(date, username, song_id, song_key)
values ('2020-12-15 10:05:13+01', 'Parser', (select id from songs where title = 'You can call me Al'), 1);
insert into song_log_entries(date, username, song_id, song_key)
values ('2020-12-30 19:12:20+01', 'Temp', (select id from songs where title = 'You can call me Al'), 0);

insert into artists(name)
values ('The Police');
insert into songs(title, name, status, background, youtube, spotify)
values ('Roxanne', 'Roxanne', 'SHOW', 'Background on Roxanne', '3T1c7GkzRQQ', '3EYOJ48Et32uATr9ZmLnAo');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Roxanne'), (select id from artists where name = 'The Police'), true);
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/f/ff/Sting2.jpg', 'Rita Molnár, CC BY-SA 2.5 <https://creativecommons.org/licenses/by-sa/2.5>, via Wikimedia Commons', (select id from artists where name = 'The Police'));

insert into artists(name)
values ('Iggy Pop & Kate Pierson');
insert into songs(title, name, status, background, youtube, spotify)
values ('Candy', 'Candy', 'SHOW', 'Background on Candy', '6bLOjmY--TA', '6sFpmdsk4UDMcDWdy4T1Kc');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Candy'), (select id from artists where name = 'Iggy Pop & Kate Pierson'), true);
insert into artist_wikimedia_photos(url, attribution, artist_id)
values ('https://upload.wikimedia.org/wikipedia/commons/9/9f/Iggy_%26_The_Stooges_%40_Bsf_2012_%287855862360%29.jpg', 'Eddy BERTHIER from Brussels, Belgium, CC BY 2.0 <https://creativecommons.org/licenses/by/2.0>, via Wikimedia Commons', (select id from artists where name = 'Iggy Pop & Kate Pierson'));

insert into artists(name)
values ('Neil Diamond');
insert into songs(title, name, status, background, youtube, spotify)
values ('Sweet Caroline', 'Caroline', 'SHOW', 'Background on Sweet Caroline', '1vhFnTjia_I', '62AuGbAkt8Ox2IrFFb8GKV');
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Sweet Caroline'), (select id from artists where name = 'Neil Diamond'), true);
insert into song_sources(url, name, song_id, song_key)
values ('https://nl.wikipedia.org/wiki/Sweet_Caroline', 'Wikipedia over ''Sweet Caroline''', (select id from songs where title = 'Sweet Caroline'), 0);
insert into song_sources(url, name, song_id, song_key)
values ('https://www.nporadio2.nl/song/21689/sweet-caroline', 'NPO Radio2 over Sweet Caroline', (select id from songs where title = 'Sweet Caroline'), 1);
insert into artist_flickr_photos(flickr_id, artist_id)
values ('2623883171', (select id from artists where name = 'Neil Diamond'));
insert into artist_flickr_photos(flickr_id, artist_id)
values ('49108851478', (select id from artists where name = 'Neil Diamond'));
insert into song_log_entries(date, username, song_id, song_key)
values ('2021-01-15 12:12:20+01', 'Temp', (select id from songs where title = 'Sweet Caroline'), 0);
insert into artist_log_entries(date, username, artist_id, artist_key)
values ('2021-01-10 09:59:10+01', 'Temp', (select id from artists where name = 'Neil Diamond'), 0);

insert into artists(name)
values ('Dolly Parton');
insert into songs(title, name, status, background, youtube, spotify)
values ('Jolene', 'Jolene', 'IN_PROGRESS', null, null, null);
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Jolene'), (select id from artists where name = 'Dolly Parton'), true);

insert into artists(name)
values ('The Kinks');
insert into songs(title, name, status, background, youtube, spotify)
values ('Lola', 'Lola', 'IN_PROGRESS', null, null, null);
insert into songs_artists(song, artist, original_artist)
values ((select id from songs where title = 'Lola'), (select id from artists where name = 'The Kinks'), true);
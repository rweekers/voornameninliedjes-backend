insert into artists(name)
values ('Paul Simon');

insert into songs(title, name, status)
values ('You can call me Al', 'Al', 'SHOW');

insert into songs_artists(song, artist)
values ((select id from songs where title = 'You can call me Al'), (select id from artists where name = 'Paul Simon'));
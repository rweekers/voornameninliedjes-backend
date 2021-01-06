insert into artists(name)
values ('Paul Simon');

insert into songs(artist_id, title, name, status)
values ((select id from artists where name = 'Paul Simon'), 'You can call me Al', 'Al', 'SHOW');
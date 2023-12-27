insert into users(username, password)
values ('test', crypt('secret', gen_salt('bf', 10)));
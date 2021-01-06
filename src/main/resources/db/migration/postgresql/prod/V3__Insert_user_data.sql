CREATE EXTENSION pgcrypto;

insert into users(username, password)
values ('remco', crypt('secret', gen_salt('bf', 10)));

insert into users(username, password)
values ('nadja', crypt('secret', gen_salt('bf', 10)));

insert into USER_ROLES(name, user_id)
VALUES ('ADMIN', (select id from users where username = 'remco'));
insert into USER_ROLES(name, user_id)
VALUES ('OWNER', (select id from users where username = 'remco'));
insert into USER_ROLES(name, user_id)
VALUES ('ADMIN', (select id from users where username = 'nadja'));

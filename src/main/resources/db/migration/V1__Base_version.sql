create table users
(
  userid serial not null
    constraint users_pkey
    primary key,
  username varchar(128) not null,
  password varchar(128) not null,
  enabled boolean default true,
  role varchar(64) not null
)
;

create unique index users_userid_uindex
  on users (userid)
;

create table daily_task
(
  id serial not null
    constraint daily_task_pkey
    primary key,
  userid integer not null
    constraint daily_task_userid_fk
    references users
    on update cascade on delete cascade,
  name varchar(255) not null
)
;

create unique index daily_task_id_uindex
  on daily_task (id)
;

INSERT INTO public.users (userid, username, password, enabled, role) VALUES (1, 'arthan', 'qazqaz', true, 'ADMIN');
INSERT INTO public.users (userid, username, password, enabled, role) VALUES (4, 'python', 'python', true, 'USER');
INSERT INTO public.daily_task (id, userid, name) VALUES (1, 1, 'Be cool');
INSERT INTO public.daily_task (id, userid, name) VALUES (2, 1, 'Look awesome');
INSERT INTO public.daily_task (id, userid, name) VALUES (3, 1, 'Don''t break a promise');
INSERT INTO public.daily_task (id, userid, name) VALUES (4, 4, 'Go to Japan');
INSERT INTO public.daily_task (id, userid, name) VALUES (5, 4, 'Return home');

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

create unique index users_username_uindex
  on users (username)
;

INSERT INTO public.users (username, password, enabled, role) VALUES ('arthan', 'arthan', true, 'ADMIN');
INSERT INTO public.users (username, password, enabled, role) VALUES ('python', 'python', true, 'USER');

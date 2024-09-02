create schema users;

create table users.users
(
    id         serial not null,
    username   text   not null,
    password   text   not null,
    first_name text,
    last_name  text,
    biography  text,
    birthday   date,
    gender     text,
    city       text
);

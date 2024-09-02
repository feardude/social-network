create schema users;

create table users.users
(
    id         serial not null,
    username   text   not null,
    password   text   not null,
    first_name text,
    last_name  text,
    birthday   date,
    gender     text,
    city       text
);

create table users.interests
(
    id    serial not null,
    title text   not null
);

create table users.user_interests
(
    interest_id int not null,
    user_id     int not null
);

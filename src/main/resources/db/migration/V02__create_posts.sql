alter table users.users
add constraint user_pk primary key (id);

create table posts
(
    id             uuid not null,
    text           text not null,
    author_user_id int references users.users (id)
);

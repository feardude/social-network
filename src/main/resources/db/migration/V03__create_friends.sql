create table friends
(
    user_id    int not null,
    friend_id  int not null,
    created_at timestamp default current_timestamp,
    primary key (user_id, friend_id),
    foreign key (user_id) references users.users (id),
    foreign key (friend_id) references users.users (id)
);

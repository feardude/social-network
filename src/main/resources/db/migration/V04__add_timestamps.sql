alter table users.users add column last_active_at timestamp;
update users.users set last_active_at = timestamp '2024-06-01' + (random() * (now() - '2024-06-01'))::interval;

alter table posts add column created_at timestamp;
update posts set created_at = timestamp '2024-12-01' + (random() * (now() - '2024-12-01'))::interval;

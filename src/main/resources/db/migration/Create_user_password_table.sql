-- For H2 Database
create table user_password (
  id bigserial not null primary key,
  userid varchar(8) not null,
  password varchar(512) not null,
  hash_version int not null,
  disabled boolean not null,
  created_at timestamp not null,
  updated_at timestamp not null
)

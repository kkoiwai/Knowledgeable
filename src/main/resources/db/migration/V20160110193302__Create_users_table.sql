-- For H2 Database
create table users (
  id bigserial not null primary key,
  userid varchar(8) not null,
  name varchar(512) not null,
  email varchar(512) not null,
  authority int not null,
  disabled boolean not null,
  created_at timestamp not null,
  updated_at timestamp not null
)

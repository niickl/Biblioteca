--create  extension "uuid-ossp";

create table if not exists usuario (
   id uuid default gen_random_uuid() primary key,
   nome varchar (255) not null,
   login varchar (255) not null unique,
   senha varchar (255) not null,
   criado_em timestamptz default now(),
   excluido_em timestamptz default null)
;

--DROP TABLE IF EXISTS usuario CASCADE ;

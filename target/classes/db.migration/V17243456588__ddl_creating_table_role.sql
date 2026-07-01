--create type role_ as enum ('admin', 'user');


create table if not exists role(
    id bigserial primary key,
    status   role_ not null default 'user',
    criado_em timestamptz default now(),
    excluido_em timestamptz default null
);

INSERT INTO role (status, criado_em) VALUES ('admin', NOW());
INSERT INTO role (status, criado_em) VALUES ('user', NOW());
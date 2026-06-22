create table if not exists autor (
    id bigserial primary key,
    nome varchar,
    idade integer,
    biografia text,
    criado_em timestamptz default now(),
    excluido_em timestamptz default null
);
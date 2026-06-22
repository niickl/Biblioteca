create table if not exists editora(
    id bigserial primary key,
    nome varchar(255),
    cnpj varchar(255),
    criado_em timestamptz default now(),
    excluido_em timestamptz default null
);

-- ALTER TABLE autor ADD
--     criado_em timestamp default now()
--     excluido_em timestamp default null
--     ;

--ALTER TABLE editora drop column lido;
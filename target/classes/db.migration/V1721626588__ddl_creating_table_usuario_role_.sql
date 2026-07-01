create table if not exists usuario_role_ (
    id bigserial primary key,
    usuario_id uuid not null,
    role_id bigint not null,
    criado_em timestamptz default now(),
    excluido_em timestamptz default null,
    constraint fk_usuario foreign key (usuario_id) references usuario(id),
    constraint fk_role foreign key (role_id) references role(id)
);
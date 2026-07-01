create table emprestimo
(
    id              bigserial primary key,
    usuario_id      uuid not null,
    livro_id        bigint not null,
    data_emprestimo date not null ,
    data_devolucao_prevista  date not null,
    devolvido         boolean default false not null ,
    criado_em       timestamptz default now(),
    excluido_em     timestamptz default null,
    constraint fk_usuario foreign key (usuario_id) references usuario(id),
    constraint fk_livro foreign key (livro_id) references livro(id)
);
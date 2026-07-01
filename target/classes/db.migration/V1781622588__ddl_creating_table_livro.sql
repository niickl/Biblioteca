create table if not exists livro (
    id bigserial primary key,
    nome varchar(255),
    descricao text,


    autor_id bigint,
    editora_id bigint,

    foreign key (autor_id) references autor(id) on delete cascade ,
    foreign key (editora_id) references editora(id) on delete cascade
);

--alter table AUTOR alter column id bigint AUTO_INCREMENT;
--alter table EDITORA alter column id bigint AUTO_INCREMENT;
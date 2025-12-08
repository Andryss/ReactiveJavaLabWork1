--liquibase formatted sql

--changeset andryss:create-repairmen-table
create table repairmen (
    id bigserial primary key,
    name varchar(255) not null,
    position varchar(255) not null
);

comment on table repairmen is 'Repairmen';

comment on column repairmen.id is 'Repairman identifier';
comment on column repairmen.name is 'Repairman name';
comment on column repairmen.position is 'Repairman position';


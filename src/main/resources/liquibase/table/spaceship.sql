--liquibase formatted sql

--changeset andryss:create-spaceship-table
create table spaceship (
    id bigserial primary key,
    serial bigint not null unique,
    manufacturer varchar(255) not null,
    manufacture_date timestamp not null,
    name varchar(255) not null,
    type varchar(50) not null,
    dimensions jsonb,
    engine jsonb,
    crew jsonb,
    max_speed integer not null
);

comment on table spaceship is 'Space ships';

comment on column spaceship.id is 'Primary key identifier';
comment on column spaceship.serial is 'Serial number, unique for each ship';
comment on column spaceship.manufacturer is 'Manufacturer company name';
comment on column spaceship.manufacture_date is 'Manufacture date and time';
comment on column spaceship.name is 'Ship name';
comment on column spaceship.type is 'Ship type';
comment on column spaceship.dimensions is 'Dimensions (JSONB)';
comment on column spaceship.engine is 'Engine (JSONB)';
comment on column spaceship.crew is 'Crew members (JSONB)';
comment on column spaceship.max_speed is 'Maximum speed';


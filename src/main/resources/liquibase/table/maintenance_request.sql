--liquibase formatted sql

--changeset andryss:create-maintenance-request-table
create table maintenance_request (
    id bigserial primary key,
    spaceship_serial bigint not null,
    comment text not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    assignee bigint,
    status varchar(50) not null
);

comment on table maintenance_request is 'Maintenance requests';

comment on column maintenance_request.id is 'Maintenance request identifier';
comment on column maintenance_request.spaceship_serial is 'Spaceship serial number';
comment on column maintenance_request.comment is 'Request comment';
comment on column maintenance_request.created_at is 'Creation timestamp';
comment on column maintenance_request.updated_at is 'Last update timestamp';
comment on column maintenance_request.assignee is 'Assigned repairman identifier';
comment on column maintenance_request.status is 'Request status';


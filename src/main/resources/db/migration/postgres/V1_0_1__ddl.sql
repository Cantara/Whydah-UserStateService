 create table DELETED_USER (
        id varchar(64) not null,
        username varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        email varchar(255),
        cell_phone varchar(255),
        person_ref varchar(64),
        last_login_time timestamp,
		registration_time timestamp,
        creation_time timestamp,
        primary key (id)
);

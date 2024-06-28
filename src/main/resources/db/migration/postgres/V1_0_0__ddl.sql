 create table LOGIN_USER_STATUS (
        id varchar(64) not null,
        username varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        email varchar(255),
        cell_phone varchar(255),
        person_ref varchar(64),
        last_login_time timestamp,
	creation_time timestamp,
	old_logon_detected BOOLEAN DEFAULT FALSE,	      
        primary key (id)
);

CREATE INDEX idx_last_login_time_login_user_status ON LOGIN_USER_STATUS(last_login_time);

 create table APP_STATE (
        id varchar(64) not null,
        importuser_page_index INTEGER DEFAULT 1,
        stats_total_users_imported INTEGER DEFAULT 0,
        stats_number_of_old_users_detected INTEGER DEFAULT 0,
        stats_number_of_mails_sent INTEGER DEFAULT 0,
        stats_number_of_old_users_removed INTEGER DEFAULT 0,
        stats_number_of_old_users_comming_back INTEGER DEFAULT 0,
        primary key (id)
);

 create table OLD_USER (
        id varchar(64) not null,
        username varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        email varchar(255),
        cell_phone varchar(255),
        person_ref varchar(64),
        last_login_time timestamp,
        notified BOOLEAN DEFAULT FALSE,      
        primary key (id)
);

CREATE INDEX idx_last_login_time_old_user ON OLD_USER(last_login_time);
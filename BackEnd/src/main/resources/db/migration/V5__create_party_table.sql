
CREATE TABLE parties (
    id BIGSERIAL PRIMARY KEY ,
    party_name varchar(255) NOT NULL ,
    party_address varchar(1023) NOT NULL ,
    party_type varchar(1023) NOT NULL
);


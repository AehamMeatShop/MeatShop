CREATE TYPE party_types AS ENUM( 'SUPPLIER','CUSTOMER','COMPLEX','EMPLOYEE');

CREATE TABLE parties (
    id BIGSERIAL PRIMARY KEY ,
    party_name varchar(255) NOT NULL ,
    party_address varchar(1023) NOT NULL ,
    party_type party_types NOT NULL
);


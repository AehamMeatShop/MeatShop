CREATE TABLE party_contacts (

   id BIGSERIAL PRIMARY KEY ,
   method varchar(255) NOT NULL ,
   identifier varchar(255) NOT NULL ,
   party_id BIGINT NOT NULL ,
   CONSTRAINT fk_party_contact FOREIGN KEY (party_id) REFERENCES parties(id) ON DELETE CASCADE,
   CONSTRAINT  unique_method_identifier UNIQUE (method , identifier)
);

CREATE INDEX idx_method ON party_contacts(method) ;
CREATE INDEX idx_party_id ON party_contacts(party_id) ;
CREATE INDEX idx_identifier ON party_contacts(identifier)



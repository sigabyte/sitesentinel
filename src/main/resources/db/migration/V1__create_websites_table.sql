CREATE TABLE websites (
                          id UUID PRIMARY KEY,
                          name VARCHAR(180) NOT NULL,
                          domain VARCHAR(255) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          CONSTRAINT uk_websites_domain UNIQUE (domain)
);
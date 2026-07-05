CREATE TABLE monitoring_runs (
                                 id UUID PRIMARY KEY,
                                 website_id UUID NOT NULL,
                                 status VARCHAR(30) NOT NULL,
                                 started_at TIMESTAMP WITH TIME ZONE,
                                 completed_at TIMESTAMP WITH TIME ZONE,
                                 failure_reason TEXT,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 CONSTRAINT fk_monitoring_runs_website
                                     FOREIGN KEY (website_id)
                                         REFERENCES websites(id)
                                         ON DELETE CASCADE
);

CREATE INDEX idx_monitoring_runs_website_id
    ON monitoring_runs(website_id);

CREATE INDEX idx_monitoring_runs_status
    ON monitoring_runs(status);
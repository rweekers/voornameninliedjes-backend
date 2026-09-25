ALTER TABLE artist_log_entries
    ADD COLUMN user_id VARCHAR(255),
    ADD COLUMN http_method VARCHAR(10),
    ADD COLUMN request JSONB;
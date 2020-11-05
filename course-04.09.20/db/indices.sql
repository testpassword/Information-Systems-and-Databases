CREATE INDEX mission_period ON mission USING btree(start_date_and_time, end_date_and_time);

CREATE INDEX pos_rank ON position USING btree(rank);
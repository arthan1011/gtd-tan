ALTER TABLE daily_task
    ADD COLUMN type VARCHAR(32)
;
UPDATE daily_task
SET type = 'INSTANT'
;
ALTER TABLE daily_task
    ALTER COLUMN type SET NOT NULL
;
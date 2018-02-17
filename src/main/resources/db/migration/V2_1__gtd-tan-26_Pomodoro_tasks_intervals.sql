CREATE TABLE public.pomodoro_task_intervals
(
    id SERIAL PRIMARY KEY NOT NULL,
    intervals INT DEFAULT 1,
    CONSTRAINT pomodoro_task_intervals_daily_task_id_fk FOREIGN KEY (id) REFERENCES daily_task (id) ON DELETE CASCADE ON UPDATE CASCADE
);
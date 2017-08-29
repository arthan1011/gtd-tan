CREATE TABLE public.task_state
(
  id SERIAL PRIMARY KEY NOT NULL,
  task_id BIGINT NOT NULL,
  date DATE NOT NULL,
  state VARCHAR(32) NOT NULL,
  CONSTRAINT task_state_daily_task_id_fk FOREIGN KEY (task_id) REFERENCES daily_task (id)
);
CREATE UNIQUE INDEX task_state_id_uindex ON public.task_state (id);
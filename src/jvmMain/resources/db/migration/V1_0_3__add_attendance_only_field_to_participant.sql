ALTER TABLE public.participant ADD COLUMN is_attendance_only boolean;

UPDATE public.participant SET is_attendance_only = false;

ALTER TABLE public.participant ALTER COLUMN is_attendance_only SET NOT NULL;
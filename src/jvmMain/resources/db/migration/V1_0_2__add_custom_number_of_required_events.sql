ALTER TABLE public.season ADD COLUMN mangekjemper_required_events smallint;

UPDATE public.season SET mangekjemper_required_events = 8;

ALTER TABLE public.season ALTER COLUMN mangekjemper_required_events SET NOT NULL;
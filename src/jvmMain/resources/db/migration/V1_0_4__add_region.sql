ALTER TABLE public.season ADD COLUMN region integer;

UPDATE public.season SET region = 0;

ALTER TABLE public.season ALTER COLUMN region SET NOT NULL;
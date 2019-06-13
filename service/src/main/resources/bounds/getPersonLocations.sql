-- TODO: add filtering by date range
SELECT
  l.latitude,
  l.longitude,
  lh.start_date,
  lh.end_date
FROM public.location_history lh
	JOIN public.location l
	  ON lh.location_id = l.location_id
WHERE lh.domain_id = 'PERSON' AND lh.entity_id = @personId
-- TODO: add filtering by date range
SELECT
  l.latitude,
  l.longitude,
  lh.start_date,
  lh.end_date
FROM @cdmSchema.location_history lh
	JOIN @cdmSchema.location l
	  ON lh.location_id = l.location_id
WHERE lh.domain_id = 'PERSON' AND lh.entity_id = @personId
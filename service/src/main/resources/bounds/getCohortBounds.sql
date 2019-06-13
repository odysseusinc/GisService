-- TODO: need proper handling of latitudes and longitudes in both hemispheres (latitude) and Europe/US (longitude)
SELECT
  MIN(latitude) min_latitude,
  MAX(latitude) max_latitude,
  MIN(longitude) min_longitude,
  MAX(longitude) max_longitude
FROM results.cohort c
	JOIN public.location_history lh
	  ON c.subject_id = lh.entity_id AND lh.domain_id = 'PERSON'
	    AND lh.start_date <= c.cohort_start_date
	    AND c.cohort_end_date <= DATEFROMPARTS(2099, 12, 31)
	JOIN public.location l
	  ON lh.location_id = l.location_id
WHERE c.cohort_definition_id = @cohortId
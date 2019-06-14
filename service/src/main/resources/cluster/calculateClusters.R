library(DatabaseConnector)
library(geojson) # required for "as.geojson"
library(sp)
library(SqlRender)

dbms <- Sys.getenv("DBMS_TYPE")
connectionString <- Sys.getenv("CONNECTION_STRING")
user <- Sys.getenv("DBMS_USERNAME")
pwd <- Sys.getenv("DBMS_PASSWORD")
cdmSchema <- Sys.getenv("DBMS_SCHEMA")
resultSchema <- Sys.getenv("RESULT_SCHEMA")

connectionDetails <- DatabaseConnector::createConnectionDetails(
  dbms=dbms, 
  connectionString=connectionString,
  user=user,
  password=pwd
)

# TODO: the same SQL is in Density script, move out
sql <- "
    SELECT longitude, latitude
    FROM @resultSchema.cohort c
        JOIN @cdmSchema.location_history lh
          ON c.subject_id = lh.entity_id AND lh.domain_id = 'PERSON'
            AND c.cohort_start_date <= isNull(lh.end_date, DATEFROMPARTS(2099, 12, 31))
            AND c.cohort_end_date >= lh.start_date
        JOIN @cdmSchema.location l
          ON lh.location_id = l.location_id
    WHERE c.cohort_definition_id = {{cohortId}}
    AND {{westLongitude}} <= longitude AND longitude <= {{eastLongitude}} AND {{northLatitude}} >= latitude AND latitude >= {{southLatitude}}"

sql <- SqlRender::render(sql, resultSchema = resultSchema, cdmSchema = cdmSchema)
sql <- SqlRender::translate(sql, connectionDetails$dbms)

con <- DatabaseConnector::connect(connectionDetails)
res <- DatabaseConnector::lowLevelQuerySql(con, sql)
disconnect(con)

clusters <- kmeans(res, 10)

coords <- clusters$centers
size <- clusters$size

sp <- SpatialPointsDataFrame(coords, as.data.frame(size))

cat(as.geojson(sp), file = "clusters.json")
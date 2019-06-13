library(DatabaseConnector)
library(geojson) # required for "as.geojson"
library(sp)

dbms <- Sys.getenv("DBMS_TYPE")
connectionString <- Sys.getenv("CONNECTION_STRING")
user <- Sys.getenv("DBMS_USERNAME")
pwd <- Sys.getenv("DBMS_PASSWORD")

connectionDetails <- DatabaseConnector::createConnectionDetails(
  dbms=dbms, 
  connectionString=connectionString,
  user=user,
  password=pwd
)
con <- DatabaseConnector::connect(connectionDetails)
res <- DatabaseConnector::lowLevelQuerySql(
    con,
    "SELECT * FROM results.test_geo_p2 WHERE {{westLongitude}} <= lon AND lon <= {{eastLongitude}} AND {{northLatitude}} >= lat AND lat >= {{southLatitude}}"
)
disconnect(con)

clusters <- kmeans(res, 10)

coords <- clusters$centers
size <- clusters$size

sp <- SpatialPointsDataFrame(coords, as.data.frame(size))

cat(as.geojson(sp), file = "clusters.json")
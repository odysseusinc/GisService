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
driversPath <- (function(path) if (path == "") NULL else path)( Sys.getenv("JDBC_DRIVER_PATH") )

connectionDetails <- DatabaseConnector::createConnectionDetails(
  dbms=dbms, 
  connectionString=connectionString,
  user=user,
  password=pwd,
  pathToDriver = driversPath
)

sql <- SqlRender::readSql("getLocation.sql")
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
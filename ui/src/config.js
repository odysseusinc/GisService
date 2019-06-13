// TODO: need to do smth with "appConfig". Shouldn't be sticked to Atlas directory
define(['optional!appConfig'], (appConfig) => {

	// require.toUrl('appConfig')

	return {
		gisServiceUrl: appConfig.gisServiceUrl || (new URL(appConfig.api.url).origin + '/gis-service/api/v1'),
		tilesServerUrl: appConfig.tilesServerUrl || 'https://{s}.tile.openstreetmap.org',
	}
});
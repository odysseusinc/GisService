define([
	'../Utils.js',
	'../../node_modules/numeral/min/numeral.min.js',
], ({ httpQuery, addQueryParams }, numeral) => {
	return class CohortMap {

		constructor({
			gisServiceUrl,
			tilesServerUrl,
			mapContainerEl,
		}) {
			this.gisServiceUrl = gisServiceUrl;
			this.tilesServerUrl = tilesServerUrl;
			this.mapContainerEl = mapContainerEl;
		}

		setParams(cohortId, sourceKey) {
			this.cohortId = cohortId;
			this.sourceKey = sourceKey;
		}

		loadCohortBounds() {
			// TODO after test data is available
		}

		refresh() {
			const center = [40.71, -74.0]; // TODO: determine based on cohortId & sourceKey
			const zoom = 11; // TODO: determine based on cohortId & sourceKey
			if (this.mapInitiated) {
				this.map.setView(center, zoom);
			} else {
				this.initiateMap(this.mapContainerEl, center, zoom);
			}
		}

		getDensityUrl(cohortId, sourceKey, bounds) {
			return addQueryParams(this.gisServiceUrl + `/cohort/${cohortId}/density/${sourceKey}`, bounds);
		}

		getClustersUrl(cohortId, sourceKey, bounds) {
			return addQueryParams(this.gisServiceUrl + `/cohort/${cohortId}/clusters/${sourceKey}`, bounds);
		}

		initiateMap(containerEl, center, zoom) {
			this.map = L.map(containerEl);

			this.osmLayer = L.tileLayer(this.tilesServerUrl + '/{z}/{x}/{y}.png', {
				id: 'osm_tiles',
				maxZoom: 18,
			});

			this.map.setView(center, zoom);
			this.map.addLayer(this.osmLayer);

			this.map.on('moveend', () => {
				this.clearLayers();
			});

			this.mapInitiated = true;
		}

		getMapBounds() {
			const bounds = this.map.getBounds();
			const neBounds = bounds.getNorthEast();
			const swBounds = bounds.getSouthWest()
			return {
				northLatitude: neBounds.lat,
				westLongitude: swBounds.lng,
				southLatitude: swBounds.lat,
				eastLongitude: neBounds.lng
			};
		}

		clearLayers() {
			this.map.eachLayer(layer => layer !== this.osmLayer && this.map.removeLayer(layer));
		}

		getDensityColor(d) {
			return d >= 10000 ? '#800026' :
				d >= 1000 ? '#BD0026' :
					d >= 100 ? '#E31A1C' :
						d >= 10 ? '#FC4E2A' :
							d >= 1 ? '#FD8D3C' :
								d >= 0.1 ? '#FEB24C' :
									d >= parseFloat("1e-08") ? '#FED976' :
										d > 0 ? '#fff7d4' :
											'rgba(0,0,0,0)';
		}

		getDensityStyle(feature) {
			return {
				fill: true,
				fillColor: this.getDensityColor(parseFloat(feature.properties.level)),
				weight: 2,
				opacity: 1,
				color: 'white',
				dashArray: '3',
				fillOpacity: 0.5,

				fillRule: 'nonzero'
			};
		}

		loadDensityMap() {
			const url = this.getDensityUrl(this.cohortId, this.sourceKey, this.getMapBounds());
			return httpQuery(url);
		}

		loadClusters() {
			const url = this.getClustersUrl(this.cohortId, this.sourceKey, this.getMapBounds());
			return httpQuery(url);
		}

		async updateClusterMap() {
			const geoJson = await this.loadClusters();
			this.clearLayers();
			const clusters = L.geoJSON(geoJson, {
				pointToLayer: (feature, latlng) => {
					return new L.Marker(latlng, {
						icon: new L.DivIcon({
							iconSize: [35, 35],
							className: 'cluster-icon',
							html: '<span class="cluster-label">' + numeral(feature.properties.size).format('0a') + '</span>'
						})
					}).on('click', (e) => {
						this.map.setView(e.latlng, this.map.getZoom() + 1);
						this.loadClusters().then(dm => this.updateClusterMap(dm));
					});
				}
			});
			clusters.addTo(this.map);
		}

		async updateDensityMap() {
			const geoJson = await this.loadDensityMap();
			this.clearLayers();
			const geojsonLayer = L.geoJSON(geoJson, {
				style: this.getDensityStyle.bind(this),
				onEachFeature: (feature, layer) => {
					layer.bindPopup('<p>' + feature.properties.level + '</p>');
				}
			});
			geojsonLayer.addTo(this.map);
		}
	}
});
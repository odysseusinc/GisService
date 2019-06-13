define([
	'../config.js',
	'../BaseMapWidget.js',
	'./CohortMap.js',
	'text!./template.html',
	'text!./styles.css',
	'text!../../node_modules/leaflet/dist/leaflet.css',
	'../../node_modules/leaflet/dist/leaflet.js',
], (
	config,
	BaseMapWidget,
	CohortMap,
	componentTemplate,
	componentStyles,
	leafletStyles
) => {
	const TYPE = 'atlas-cohort-report';
	const NAME = `${TYPE}-geospatial`;
	let CohortGeospatialReport;

	if (!customElements.get(NAME)) {

		const COHORT_ID_ATTR = 'data-cohortid';
		const MAP_CONTAINER_ID = '#map';
		const LOAD_DENSITY_BTN_ID = '#loadDensity';
		const LOAD_CLUSTERS_BTN_ID = '#loadClusters';
		const LOADING_PANEL_ID = '#loadingPanel';

		CohortGeospatialReport = class extends BaseMapWidget {
			static TYPE = TYPE;
			static TITLE = 'Geospatial';

			constructor() {
				super();
			}

			connectedCallback() {
				super.connectedCallback();
				this.render();
				this.initMap();
			}

			static get observedAttributes() {
				return [super.observedAttributes, COHORT_ID_ATTR];
			}

			attributeChangedCallback(name, oldValue, newValue) {
				if (this.cohortMap && this.cohortId && this.sourceKey) {
					this.setLoading(true);
					this.cohortMap.setParams(this.cohortId, this.sourceKey);
					this.cohortMap.refresh();
					this.setLoading(false);
				}
			}

			get cohortId() {
				return this.getAttribute(COHORT_ID_ATTR);
			}

			setLoading(state) {
				this.getEl(LOADING_PANEL_ID).style.display = state ? 'block' : 'none';
				this.getEl(LOAD_DENSITY_BTN_ID).toggleAttribute('disabled', state);
				this.getEl(LOAD_CLUSTERS_BTN_ID).toggleAttribute('disabled', state);
			}

			initMap() {
				this.cohortMap = new CohortMap({
					gisServiceUrl: config.gisServiceUrl,
					tilesServerUrl: config.tilesServerUrl,
					mapContainerEl: this.getEl(MAP_CONTAINER_ID)
				});

				this.root.querySelector(LOAD_DENSITY_BTN_ID).addEventListener('click', async () => {
					this.setLoading(true);
					try {
						await this.cohortMap.updateDensityMap();
					} catch (e) {
						alert('Cannot retrieve density map');
						console.log(e);
					}
					this.setLoading(false);
				});
				this.root.querySelector(LOAD_CLUSTERS_BTN_ID).addEventListener('click', async () => {
					this.setLoading(true);
					try {
						await this.cohortMap.updateClusterMap();
					} catch (e) {
						alert('Cannot retrieve clusters');
						console.log(e);
					}

					this.setLoading(false);
				});
			}

			render() {
				this.root.innerHTML = componentTemplate;
				this.attachStyles(componentStyles);
				this.attachStyles(leafletStyles);
			}
		}

		customElements.define(NAME, CohortGeospatialReport);
	}

	return {
		name: NAME,
		element: CohortGeospatialReport,
	};
});
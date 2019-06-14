define([
	'../config.js',
	'../BaseMapWidget.js',
	'../Utils.js',
	'text!./template.html',
	'text!./styles.css',
], (
	config,
	BaseMapWidget,
	{ httpQuery },
	componentTemplate,
	componentStyles,
) => {

	const TYPE = 'atlas-profile-widget';
	const NAME = `${TYPE}-map`;
	let PersonMapWidget;

	const markerBase64 = "iVBORw0KGgoAAAANSUhEUgAAABkAAAApCAYAAADAk4LOAAAFgUlEQVR4Aa1XA5BjWRTN2oW17d3YaZtr2962HUzbDNpjszW24mRt28p47v7zq/bXZtrp/lWnXr337j3nPCe85NcypgSFdugCpW5YoDAMRaIMqRi6aKq5E3YqDQO3qAwjVWrD8Ncq/RBpykd8oZUb/kaJutow8r1aP9II0WmLKLIsJyv1w/kqw9Ch2MYdB++12Onxee/QMwvf4/Dk/Lfp/i4nxTXtOoQ4pW5Aj7wpici1A9erdAN2OH64x8OSP9j3Ft3b7aWkTg/Fm91siTra0f9on5sQr9INejH6CUUUpavjFNq1B+Oadhxmnfa8RfEmN8VNAsQhPqF55xHkMzz3jSmChWU6f7/XZKNH+9+hBLOHYozuKQPxyMPUKkrX/K0uWnfFaJGS1QPRtZsOPtr3NsW0uyh6NNCOkU3Yz+bXbT3I8G3xE5EXLXtCXbbqwCO9zPQYPRTZ5vIDXD7U+w7rFDEoUUf7ibHIR4y6bLVPXrz8JVZEql13trxwue/uDivd3fkWRbS6/IA2bID4uk0UpF1N8qLlbBlXs4Ee7HLTfV1j54APvODnSfOWBqtKVvjgLKzF5YdEk5ewRkGlK0i33Eofffc7HT56jD7/6U+qH3Cx7SBLNntH5YIPvODnyfIXZYRVDPqgHtLs5ABHD3YzLuespb7t79FY34DjMwrVrcTuwlT55YMPvOBnRrJ4VXTdNnYug5ucHLBjEpt30701A3Ts+HEa73u6dT3FNWwflY86eMHPk+Yu+i6pzUpRrW7SNDg5JHR4KapmM5Wv2E8Tfcb1HoqqHMHU+uWDD7zg54mz5/2BSnizi9T1Dg4QQXLToGNCkb6tb1NU+QAlGr1++eADrzhn/u8Q2YZhQVlZ5+CAOtqfbhmaUCS1ezNFVm2imDbPmPng5wmz+gwh+oHDce0eUtQ6OGDIyR0uUhUsoO3vfDmmgOezH0mZN59x7MBi++WDL1g/eEiU3avlidO671bkLfwbw5XV2P8Pzo0ydy4t2/0eu33xYSOMOD8hTf4CrBtGMSoXfPLchX+J0ruSePw3LZeK0juPJbYzrhkH0io7B3k164hiGvawhOKMLkrQLyVpZg8rHFW7E2uHOL888IBPlNZ1FPzstSJM694fWr6RwpvcJK60+0HCILTBzZLFNdtAzJaohze60T8qBzyh5ZuOg5e7uwQppofEmf2++DYvmySqGBuKaicF1blQjhuHdvCIMvp8whTTfZzI7RldpwtSzL+F1+wkdZ2TBOW2gIF88PBTzD/gpeREAMEbxnJcaJHNHrpzji0gQCS6hdkEeYt9DF/2qPcEC8RM28Hwmr3sdNyht00byAut2k3gufWNtgtOEOFGUwcXWNDbdNbpgBGxEvKkOQsxivJx33iow0Vw5S6SVTrpVq11ysA2Rp7gTfPfktc6zhtXBBC+adRLshf6sG2RfHPZ5EAc4sVZ83yCN00Fk/4kggu40ZTvIEm5g24qtU4KjBrx/BTTH8ifVASAG7gKrnWxJDcU7x8X6Ecczhm3o6YicvsLXWfh3Ch1W0k8x0nXF+0fFxgt4phz8QvypiwCCFKMqXCnqXExjq10beH+UUA7+nG6mdG/Pu0f3LgFcGrl2s0kNNjpmoJ9o4B29CMO8dMT4Q5ox8uitF6fqsrJOr8qnwNbRzv6hSnG5wP+64C7h9lp30hKNtKdWjtdkbuPA19nJ7Tz3zR/ibgARbhb4AlhavcBebmTHcFl2fvYEnW0ox9xMxKBS8btJ+KiEbq9zA4RthQXDhPa0T9TEe69gWupwc6uBUphquXgf+/FrIjweHQS4/pduMe5ERUMHUd9xv8ZR98CxkS4F2n3EUrUZ10EYNw7BWm9x1GiPssi3GgiGRDKWRYZfXlON+dfNbM+GgIwYdwAAAAASUVORK5CYII=";
	const markerShadowBase64 = "iVBORw0KGgoAAAANSUhEUgAAACkAAAApCAQAAAACach9AAACMUlEQVR4Ae3ShY7jQBAE0Aoz/f9/HTMzhg1zrdKUrJbdx+Kd2nD8VNudfsL/Th///dyQN2TH6f3y/BGpC379rV+S+qqetBOxImNQXL8JCAr2V4iMQXHGNJxeCfZXhSRBcQMfvkOWUdtfzlLgAENmZDcmo2TVmt8OSM2eXxBp3DjHSMFutqS7SbmemzBiR+xpKCNUIRkdkkYxhAkyGoBvyQFEJEefwSmmvBfJuJ6aKqKWnAkvGZOaZXTUgFqYULWNSHUckZuR1HIIimUExutRxwzOLROIG4vKmCKQt364mIlhSyzAf1m9lHZHJZrlAOMMztRRiKimp/rpdJDc9Awry5xTZCte7FHtuS8wJgeYGrex28xNTd086Dik7vUMscQOa8y4DoGtCCSkAKlNwpgNtphjrC6MIHUkR6YWxxs6Sc5xqn222mmCRFzIt8lEdKx+ikCtg91qS2WpwVfBelJCiQJwvzixfI9cxZQWgiSJelKnwBElKYtDOb2MFbhmUigbReQBV0Cg4+qMXSxXSyGUn4UbF8l+7qdSGnTC0XLCmahIgUHLhLOhpVCtw4CzYXvLQWQbJNmxoCsOKAxSgBJno75avolkRw8iIAFcsdc02e9iyCd8tHwmeSSoKTowIgvscSGZUOA7PuCN5b2BX9mQM7S0wYhMNU74zgsPBj3HU7wguAfnxxjFQGBE6pwN+GjME9zHY7zGp8wVxMShYX9NXvEWD3HbwJf4giO4CFIQxXScH1/TM+04kkBiAAAAAElFTkSuQmCC";

	// Note:
	// Issues with relative path for marker icon (https://github.com/Leaflet/Leaflet/issues/4968#issuecomment-264311098)
	// +
	// text plugin loads data as text (and we need 'arraybuffer' / 'blob') to assemble base64 manually
	// =
	// hardcode icons base64

	let DefaultIcon = L.icon({
		iconUrl: "data:image/png;base64," + markerBase64,
		shadowUrl: "data:image/png;base64," + markerShadowBase64,
		iconSize: [25, 41],
		iconAnchor: [12.5, 41],
		popupAnchor: [0, -40]
	});

	if (!customElements.get(NAME)) {

		const PERSON_ID_ATTR = 'data-personid';
		const MAP_CONTAINER_ID = '#map';

		PersonMapWidget = class extends BaseMapWidget {
			static TYPE = TYPE;
			static TITLE = 'Map';

			constructor() {
				super();
			}

			connectedCallback() {
				super.connectedCallback();
				this.render();
			}

			static get observedAttributes() {
				return [super.observedAttributes, PERSON_ID_ATTR];
			}

			attributeChangedCallback(name, oldValue, newValue) {
				if (this.personId && this.sourceKey) {
					!this.mapInitiated && this.initMap();
					this.loadLocationHistory();
				}
			}

			get personId() {
				return this.getAttribute(PERSON_ID_ATTR);
			}

			get componentTemplate() {
				return componentTemplate;
			}

			get componentStyles() {
				return componentStyles;
			}

			initMap() {
				this.map = L.map(this.getEl(MAP_CONTAINER_ID));
				// Since the map takes full width and user's mouse most possible will be over it during scroll - disable zoom on scroll
				this.map.scrollWheelZoom.disable();
				this.osmLayer = L.tileLayer(config.tilesServerUrl + '/{z}/{x}/{y}.png', {
					id: 'osm_tiles',
					maxZoom: 18,
				});
				this.map.addLayer(this.osmLayer);
				this.mapInitiated = true;
			}

			async loadLocationHistory() {
				this.setLoading(true);

				this.clearLayers();

				const locationHistory = await httpQuery(config.gisServiceUrl + `/person/${this.personId}/bounds/${this.sourceKey}`);

				this.map.fitBounds(
					[
						// <LatLng> southWest, <LatLng> northEast
						[locationHistory.bbox.southLatitude, locationHistory.bbox.westLongitude],
						[locationHistory.bbox.northLatitude, locationHistory.bbox.eastLongitude],
					],
					{
						padding: [50, 50]
					}
				);

				locationHistory.locations.forEach(loc => {
					L
						.marker([loc.latitude, loc.longitude], {icon: DefaultIcon})
						.bindPopup(`Dates: ${loc.startDate} - ${loc.endDate || 'current'}`)
						.addTo(this.map);
				});

				this.setLoading(false);
			}

			clearLayers() {
				this.map.eachLayer(layer => layer !== this.osmLayer && this.map.removeLayer(layer));
			}

			render() {
				super.render();
				window.onpopstate = event => this.loadLocationHistory();
			}
		}

		customElements.define(NAME, PersonMapWidget);
	}

	return {
		name: NAME,
		element: PersonMapWidget,
	}
});
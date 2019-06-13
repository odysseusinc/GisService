'use strict';

const currentDocument = document.currentScript.ownerDocument;

define([], () => {
	const SOURCE_KEY_ATTR = 'data-sourcekey';

	class BaseMapWidget extends HTMLElement {

		connectedCallback() {
			this.root = this.attachShadow({mode: 'open'});
		}

		static get observedAttributes() {
			return [SOURCE_KEY_ATTR];
		}

		get sourceKey() {
			return this.getAttribute(SOURCE_KEY_ATTR);
		}

		getEl(selector) {
			return this.root.querySelector(selector);
		}

		attachStyles(styles) {
			const mapStyles = currentDocument.createElement('style');
			mapStyles.innerHTML = styles;
			this.root.appendChild(mapStyles);
		}
	}

	return BaseMapWidget;
});
const babel = require('@babel/core');
const r = require('requirejs');

const bundleName = 'dist/bundle.js';

const settings = {
	baseUrl: '.',
	normalizeDirDefines: 'skip',
	name: 'src/index',
	out: bundleName,
	paths: {
		'optional': 'node_modules/@ohdsi/ui-toolbox/lib/es/extensions/requirejs-loaders/optional',
		'text': 'node_modules/@ohdsi/ui-toolbox/lib/es/extensions/requirejs-loaders/text',

	},
	onBuildRead(moduleName, path, content) {
		return babel.transform(content, {
				plugins: ['@babel/plugin-proposal-object-rest-spread', '@babel/plugin-proposal-class-properties'],
				presets: ['@babel/preset-env'],
			}
		).code;
	},
	findNestedDependencies: true,
	generateSourceMaps: false,
	optimize: 'none',
};

r.optimize(settings);
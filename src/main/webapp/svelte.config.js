import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://svelte.dev/docs/kit/integrations
	// for more information about preprocessors
	preprocess: vitePreprocess(
		
	),

	kit: {
		// adapter-auto only supports some environments, see https://svelte.dev/docs/kit/adapter-auto for a list.
		// If your environment is not supported, or you settled on a specific environment, switch out the adapter.
		// See https://svelte.dev/docs/kit/adapters for more information about adapters.
		adapter: adapter({
			pages:'../../../target/svelte-build',
			assets:'../../../target/svelte-build',
			fallback: 'index.html',
		}),
		prerender: {
			entries: [
				'/',
				'/rule/java/GCI1',
			],
			handleHttpError: ({ path, referrer, message }) => {
				// ignore deliberate link to shiny 404 page
				if (path === '/not-found') {
					return;
				}

				// otherwise fail the build
				throw new Error(message);
			}
		}
	}
};

export default config;

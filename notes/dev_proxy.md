In vite.config.js, the key part is that only requests passing through /api are proxied to the backend. 

In "prod", a request to a route which does _not_ get prefixed by API, will return the single page app UI... browser caches it so it would be fast. 

A request which does rquest from the "/api/*", will be intercepted by the backend and served as such. 

This imposes the constraint, that all smithy routes are prefixed with "api" by convention. 




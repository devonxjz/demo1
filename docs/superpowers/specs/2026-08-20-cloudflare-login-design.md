# Cloudflare Login Form Design

## Goal

Provide a minimal web form that accepts a username and email address, then displays a greeting containing both values.

## Architecture

Replace the unused Java/Maven starter with one Cloudflare Worker. The Worker serves the form on `GET /` and processes submissions on `POST /login`. No database, session, password, or external authentication service is required.

## Request Flow

1. `GET /` returns an accessible HTML form with required username and email fields.
2. The browser posts form-encoded values to `/login`.
3. The Worker trims and validates both values, including the email format.
4. Valid input returns `Xin chào <username>, email: <email>` with HTML escaping.
5. Invalid input returns HTTP 400 with a short Vietnamese error and a link back to the form.

## Files

- `src/index.js`: Worker request handler and HTML responses.
- `wrangler.jsonc`: Cloudflare Worker configuration.
- `package.json`: local check and deployment commands.
- `test/worker.test.mjs`: one runnable check for the form and submission flow.

The existing `pom.xml` and `src/main/java/dev/Main.java` will be removed because Cloudflare Workers does not execute JSP or Java Servlet code.

## Deployment

The project will be committed and pushed to a user-provided GitHub or GitLab repository. Cloudflare Workers Builds can then connect to that repository and deploy the production branch with `npx wrangler deploy` after every push.

## Acceptance Criteria

- The root page renders both required fields.
- A valid submission displays the supplied username and email.
- Invalid or unsafe input is rejected or escaped.
- The local check passes.
- Wrangler validates the Worker configuration.

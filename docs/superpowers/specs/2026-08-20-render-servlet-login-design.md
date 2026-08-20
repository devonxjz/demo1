# Render Servlet Login Design

## Goal

Provide a minimal login form with username and email fields. Submitting the form displays `Xin chào <username>, email: <email>`.

## Architecture

Tomcat serves a static HTML/CSS form and a Jakarta Servlet handles `POST /login`. Maven packages the application as a WAR. Render builds and runs the WAR with the repository's Dockerfile and redeploys on pushes to the linked branch.

## Request Flow

1. `GET /` serves `index.html` and `style.css` without JavaScript.
2. The browser posts form-encoded username and email values to `/login`.
3. `LoginServlet` trims and validates the values.
4. Invalid input returns HTTP 400.
5. Valid input is HTML-escaped and returned in the greeting.

## Constraints

- No JavaScript, JSP, database, session, password, or authentication provider.
- Java 21, Jakarta Servlet 6.1, Tomcat 11, and Maven WAR packaging.
- One focused unit test file covers validation and HTML escaping.
- Render deployment uses Docker and the free web-service plan.

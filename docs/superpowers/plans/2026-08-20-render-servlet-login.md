# Render Servlet Login Implementation Plan

**Goal:** Build and deploy a Java Servlet login form on Render without JavaScript.

**Spec:** `docs/superpowers/specs/2026-08-20-render-servlet-login-design.md`

## Task 1: Servlet behavior

- Create Maven WAR configuration with Jakarta Servlet and JUnit.
- Write failing tests for input normalization, email validation, and HTML escaping.
- Implement the minimum `LoginServlet` needed to pass the tests.
- Run `mvn test`.

## Task 2: Static form

- Create `src/main/webapp/index.html` with username and email fields.
- Create `src/main/webapp/style.css` with a small responsive layout.
- Keep submission native with `method="post"` and `action="/login"`.
- Run `mvn package` and inspect the WAR contents.

## Task 3: Render deployment

- Add a multi-stage Dockerfile that builds the WAR and runs it on Tomcat 11.
- Add `render.yaml` for a Docker web service on port 8080.
- Verify tests and WAR packaging locally.
- Push the branch to the user-provided Git remote and connect the Render Blueprint.

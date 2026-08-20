# Cloudflare Login Form Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build and validate a minimal Cloudflare Worker that accepts a username and email, then returns a safe greeting.

**Architecture:** One ES-module Worker serves the HTML form and handles its form-encoded submission. Node's built-in test runner exercises the real Worker handler directly, and Wrangler owns local development and deployment.

**Tech Stack:** JavaScript ES modules, Cloudflare Workers runtime, Wrangler 4, Node.js built-in test runner

**Spec:** `docs/superpowers/specs/2026-08-20-cloudflare-login-design.md`

## Global Constraints

- No database, session, password, external authentication service, frontend framework, or CSS dependency.
- `GET /` serves the form and `POST /login` processes it.
- Successful output is `Xin chào <username>, email: <email>`.
- Username and email are required, bounded, validated, and escaped before HTML rendering.
- Use `compatibility_date` `2026-08-20` and Wrangler 4.

---

### Task 1: Form page

**Files:**
- Create: `package.json`
- Create: `src/index.js`
- Create: `test/worker.test.mjs`

**Interfaces:**
- Produces: default Worker export with `fetch(request: Request): Promise<Response>`.
- Produces: `GET /` returning an HTML form with `username` and `email` controls.

- [ ] **Step 1: Add the minimal project and Worker skeleton**

Create `package.json`:

```json
{
  "name": "demo1-login",
  "private": true,
  "type": "module",
  "scripts": {
    "test": "node --test",
    "dev": "wrangler dev",
    "check": "npm test && wrangler deploy --dry-run",
    "deploy": "wrangler deploy"
  }
}
```

Create `src/index.js` with a compileable skeleton only:

```js
export default {};
```

- [ ] **Step 2: Write the failing form-page test**

Create `test/worker.test.mjs`:

```js
import test from "node:test";
import assert from "node:assert/strict";
import worker from "../src/index.js";

test("GET / returns the login form", async () => {
  assert.equal(typeof worker.fetch, "function");

  const response = await worker.fetch(new Request("https://example.com/"));
  const html = await response.text();

  assert.equal(response.status, 200);
  assert.match(response.headers.get("content-type"), /^text\/html/);
  assert.match(html, /name="username"/);
  assert.match(html, /name="email"/);
  assert.match(html, /type="email"/);
  assert.match(html, /required/);
});
```

- [ ] **Step 3: Run the test and verify RED**

Run: `npm test`

Expected: FAIL at `typeof worker.fetch` because the handler is not implemented.

- [ ] **Step 4: Implement the form response**

Replace `src/index.js` with:

```js
const HTML_HEADERS = {
  "content-type": "text/html; charset=UTF-8",
  "content-security-policy": "default-src 'none'; style-src 'unsafe-inline'; form-action 'self'; base-uri 'none'; frame-ancestors 'none'",
  "referrer-policy": "no-referrer",
  "x-content-type-options": "nosniff"
};

function page(content, title = "Đăng nhập") {
  return `<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>${title}</title>
  <style>
    *{box-sizing:border-box}body{margin:0;min-height:100vh;display:grid;place-items:center;background:#f4f1ea;color:#20201e;font:16px/1.5 system-ui,sans-serif}.card{width:min(92vw,420px);padding:32px;background:#fff;border:1px solid #ddd8ce;border-radius:16px;box-shadow:0 16px 40px #29271f14}h1{margin:0 0 8px;font-size:28px}p{margin:0 0 24px;color:#66625a}label{display:block;margin:16px 0 6px;font-weight:650}input{width:100%;padding:12px;border:1px solid #aaa49a;border-radius:9px;font:inherit}input:focus{outline:3px solid #b9d5ff;border-color:#245fa8}button,.link{display:inline-block;margin-top:22px;padding:12px 18px;border:0;border-radius:9px;background:#20201e;color:#fff;font:inherit;font-weight:700;text-decoration:none;cursor:pointer}button{width:100%}
  </style>
</head>
<body><main class="card">${content}</main></body>
</html>`;
}

function html(content, status = 200) {
  return new Response(page(content), { status, headers: HTML_HEADERS });
}

function formPage() {
  return html(`<h1>Đăng nhập</h1>
<p>Nhập thông tin để tiếp tục.</p>
<form method="post" action="/login">
  <label for="username">Tên đăng nhập</label>
  <input id="username" name="username" autocomplete="username" maxlength="50" required>
  <label for="email">Email</label>
  <input id="email" name="email" type="email" autocomplete="email" maxlength="254" required>
  <button type="submit">Đăng nhập</button>
</form>`);
}

export default {
  async fetch(request) {
    const url = new URL(request.url);
    if (request.method === "GET" && url.pathname === "/") return formPage();
    return new Response("Không tìm thấy", { status: 404 });
  }
};
```

- [ ] **Step 5: Run the test and verify GREEN**

Run: `npm test`

Expected: PASS with one test.

- [ ] **Step 6: Commit the form page**

```bash
git add package.json src/index.js test/worker.test.mjs
git commit -m "feat(login): add Worker login form"
```

---

### Task 2: Submission, validation, and safe output

**Files:**
- Modify: `src/index.js`
- Modify: `test/worker.test.mjs`

**Interfaces:**
- Consumes: default Worker export from Task 1.
- Produces: `POST /login` accepting `application/x-www-form-urlencoded` values.
- Produces: HTTP 200 greeting for valid input and HTTP 400 for invalid input.

- [ ] **Step 1: Add failing submission tests**

Append to `test/worker.test.mjs`:

```js
function loginRequest(username, email) {
  return new Request("https://example.com/login", {
    method: "POST",
    body: new URLSearchParams({ username, email })
  });
}

test("POST /login returns the submitted greeting", async () => {
  const response = await worker.fetch(loginRequest("An", "an@example.com"));
  const html = await response.text();

  assert.equal(response.status, 200);
  assert.match(html, /Xin chào An, email: an@example\.com/);
});

test("POST /login rejects invalid values", async () => {
  const response = await worker.fetch(loginRequest("", "not-an-email"));

  assert.equal(response.status, 400);
  assert.match(await response.text(), /Thông tin không hợp lệ/);
});

test("POST /login escapes submitted HTML", async () => {
  const response = await worker.fetch(loginRequest("<script>alert(1)</script>", "an@example.com"));
  const html = await response.text();

  assert.equal(response.status, 200);
  assert.doesNotMatch(html, /<script>alert\(1\)<\/script>/);
  assert.match(html, /&lt;script&gt;alert\(1\)&lt;\/script&gt;/);
});
```

- [ ] **Step 2: Run the tests and verify RED**

Run: `npm test`

Expected: the three new tests FAIL because `POST /login` still returns 404.

- [ ] **Step 3: Implement the minimum submission flow**

Add below `HTML_HEADERS` in `src/index.js`:

```js
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const ESCAPES = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" };

function escapeHtml(value) {
  return value.replace(/[&<>"']/g, character => ESCAPES[character]);
}

async function credentials(request) {
  let values;
  try {
    values = await request.formData();
  } catch {
    return null;
  }

  const username = values.get("username");
  const email = values.get("email");
  if (typeof username !== "string" || typeof email !== "string") return null;

  const normalized = { username: username.trim(), email: email.trim() };
  if (!normalized.username || normalized.username.length > 50) return null;
  if (normalized.email.length > 254 || !EMAIL_PATTERN.test(normalized.email)) return null;
  return normalized;
}
```

Add below `formPage()`:

```js
async function loginPage(request) {
  const input = await credentials(request);
  if (!input) {
    return html(`<h1>Thông tin không hợp lệ</h1><a class="link" href="/">Thử lại</a>`, 400);
  }

  return html(`<h1>Xin chào ${escapeHtml(input.username)}, email: ${escapeHtml(input.email)}</h1>`);
}
```

Add the POST route before the 404 response:

```js
if (request.method === "POST" && url.pathname === "/login") return loginPage(request);
```

- [ ] **Step 4: Run the tests and verify GREEN**

Run: `npm test`

Expected: PASS with four tests.

- [ ] **Step 5: Commit the submission flow**

```bash
git add src/index.js test/worker.test.mjs
git commit -m "feat(login): handle form submission"
```

---

### Task 3: Cloudflare configuration and Java cleanup

**Files:**
- Create: `wrangler.jsonc`
- Create: `package-lock.json`
- Modify: `.gitignore`
- Delete: `pom.xml`
- Delete: `src/main/java/dev/Main.java`

**Interfaces:**
- Consumes: Worker entry point at `src/index.js`.
- Produces: `npm run deploy` and a Workers Builds-compatible repository.

- [ ] **Step 1: Install the deployment CLI**

Run: `npm install -D wrangler@latest`

Expected: `wrangler` 4.x is added to `devDependencies` and `package-lock.json` is created.

- [ ] **Step 2: Add the validated Worker configuration**

Create `wrangler.jsonc`:

```jsonc
{
  "$schema": "./node_modules/wrangler/config-schema.json",
  "name": "demo1-login",
  "main": "src/index.js",
  "compatibility_date": "2026-08-20",
  "compatibility_flags": ["nodejs_compat"],
  "observability": {
    "enabled": true,
    "head_sampling_rate": 1
  }
}
```

- [ ] **Step 3: Remove obsolete Java files and ignore Node output**

Delete `pom.xml` and `src/main/java/dev/Main.java`. Append to `.gitignore`:

```gitignore
node_modules/
.wrangler/
.dev.vars
```

- [ ] **Step 4: Run full verification**

Run:

```bash
npm test
npx wrangler deploy --dry-run
npx wrangler check startup
```

Expected: all tests pass, Wrangler builds without configuration errors, and startup profiling completes.

- [ ] **Step 5: Commit the deployable project**

```bash
git add package.json package-lock.json wrangler.jsonc .gitignore
git add -u -- pom.xml src/main/java/dev/Main.java
git commit -m "chore(cloudflare): configure Worker deployment"
```

---

### Task 4: Git push and automatic deployment

**Files:** None.

**Interfaces:**
- Consumes: user-provided GitHub or GitLab repository URL and authenticated Cloudflare account.
- Produces: remote Git branch and Cloudflare Workers Builds connection.

- [ ] **Step 1: Add the repository remote and push**

Set the task-specific `DEMO1_REPOSITORY_URL` variable to the exact repository URL supplied by the user, then run:

```bash
git remote add origin "$DEMO1_REPOSITORY_URL"
git branch -M main
git push -u origin main
```

Expected: the `main` branch exists on GitHub or GitLab.

- [ ] **Step 2: Connect Workers Builds**

In Cloudflare: **Workers & Pages → Create application → Import a repository**. Choose the pushed repository, production branch `main`, and deploy command `npx wrangler deploy`.

Expected: Cloudflare reports a successful build and provides a `workers.dev` URL. Future pushes to `main` automatically deploy.

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

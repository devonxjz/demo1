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

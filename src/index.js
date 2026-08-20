const HTML_HEADERS = {
  "content-type": "text/html; charset=UTF-8",
  "content-security-policy": "default-src 'none'; style-src 'unsafe-inline'; form-action 'self'; base-uri 'none'; frame-ancestors 'none'",
  "referrer-policy": "no-referrer",
  "x-content-type-options": "nosniff"
};

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

async function loginPage(request) {
  const input = await credentials(request);
  if (!input) {
    return html(`<h1>Thông tin không hợp lệ</h1><a class="link" href="/">Thử lại</a>`, 400);
  }

  return html(`<h1>Xin chào ${escapeHtml(input.username)}, email: ${escapeHtml(input.email)}</h1>`);
}

export default {
  async fetch(request) {
    const url = new URL(request.url);
    if (request.method === "GET" && url.pathname === "/") return formPage();
    if (request.method === "POST" && url.pathname === "/login") return loginPage(request);
    return new Response("Không tìm thấy", { status: 404 });
  }
};

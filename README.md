## Run Locally

### Cách 1: Chạy trực tiếp bằng Maven Jetty Plugin
```bash
mvn jetty:run
```
Truy cập: `http://localhost:8088/` hoặc `http://localhost:8088/index.html`

### Cách 2: Chạy qua Docker (Tomcat 11)
```bash
docker build -t demo1 .
docker run --rm -p 8081:8080 demo1
```
Truy cập: `http://localhost:8081/`
# URL Shortener + Analytics Service

A backend URL shortener built with Spring Boot, MySQL, and Redis — similar in spirit to bit.ly.

## Features
- Shorten any long URL into a compact Base62 code
- Fast redirects via a Redis cache in front of MySQL
- Click analytics: total click count per short link
- Clean REST API, ready for a frontend or CLI client

## Tech Stack
- Java 17, Spring Boot 3.3
- Spring Data JPA + MySQL
- Spring Data Redis (caching layer)
- Maven

## API

| Method | Endpoint              | Description                          |
|--------|-----------------------|---------------------------------------|
| POST   | `/api/shorten`        | Body: `{"originalUrl": "..."}` → returns short URL |
| GET    | `/{shortCode}`        | Redirects to the original URL (302) and logs a click |
| GET    | `/api/stats/{shortCode}` | Returns total click count for that code |

### Example

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.google.com"}'

# => {"shortUrl":"http://localhost:8080/1"}

curl -i http://localhost:8080/1
# => 302 redirect to https://www.google.com

curl http://localhost:8080/api/stats/1
# => {"shortCode":"1","totalClicks":1}
```

## Running locally

1. Create a MySQL database named `urlshortener`.
2. Run a local Redis instance (or use Docker: `docker run -p 6379:6379 redis`).
3. Set env vars (or edit `application.properties` defaults):
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
   - `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
4. `mvn spring-boot:run`

## Deploying (free tier)

- **Backend:** [Render](https://render.com) — connect the GitHub repo, set build command `mvn clean package`, start command `java -jar target/url-shortener-1.0.0.jar`.
- **MySQL:** [Railway](https://railway.app) or [Aiven](https://aiven.io) free tier.
- **Redis:** [Upstash](https://upstash.com) free tier.
- Set the env vars listed above (`DB_URL`, `REDIS_HOST`, etc.) plus `APP_BASE_URL` to your live Render URL, in the Render dashboard.

## Design notes

- Short codes are a Base62 encoding of the row's auto-increment ID — collision-free by construction, no retry loop needed.
- Redis is checked before MySQL on every redirect, with a 24-hour TTL, to keep hot links fast under load.
- Click events are stored as their own table so analytics can grow (by day, by referrer, etc.) without touching the core mapping table.

## Author
Sachin Rathod

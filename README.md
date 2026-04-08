# Fake-Rest

Fake-Rest is a service for mocking REST endpoints for testing your projects.

## Getting Started

**Requirements:**
- Java 17+

**Build:**
```
./gradlew :ui:shadowJar
```

**Start:**
```
java -jar ui/build/libs/ui-2.0.0.jar
```

By default:
- UI is available at `http://localhost:8080`
- Mock server listens on `http://localhost:8081`

## Configuration

Configuration is stored in `config.json` in the working directory. It is created automatically on first run.

```json
{
  "mockPort": 8081,
  "uiPort": 8080,
  "handlers": []
}
```

You can also place additional config files in the `import/` directory next to the jar. On startup, handlers from those files are merged into `config.json` and the imported files are moved to `import/processed/`.

### Handler common fields

Every handler has these fields:

| Field    | Description                                                      |
|----------|------------------------------------------------------------------|
| `id`     | Unique identifier (auto-generated UUID if not set)               |
| `path`   | URL path, can include path variables like `/users/{id}`          |
| `method` | HTTP method: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD`, `OPTIONS` |
| `type`   | Handler type: `STATIC`, `GROOVY`, or `ROUTER`                    |

---

### STATIC handler

Returns a fixed response for every matching request.

**Extra fields:**

| Field          | Description                        |
|----------------|------------------------------------|
| `responseBody` | Body to return (string)            |
| `responseCode` | HTTP status code to return (int)   |

**Example:**
```json
{
  "type": "STATIC",
  "method": "GET",
  "path": "/hello",
  "responseCode": 200,
  "responseBody": "{\"message\": \"hello\"}"
}
```

---

### GROOVY handler

Executes a Groovy script and returns its result as the response. Useful for dynamic behavior.

**Extra fields:**

| Field        | Description              |
|--------------|--------------------------|
| `groovyCode` | Groovy script (string)   |

**Available variables in the script:**

| Variable         | Type                      | Description                                              |
|------------------|---------------------------|----------------------------------------------------------|
| `request`        | `HttpRequest`             | Incoming request. Has `body` (String) and `variables` (Map<String, String> of query params) |
| `dataRepository` | `HttpHandlerDataRepository` | Key-value store shared across all Groovy handlers       |
| `jsonMapper`     | `JsonMapper`              | Jackson JSON mapper for parsing/building JSON            |

The script must return an `HttpResponse`. The following classes are auto-imported — no `import` statement needed:
`HttpResponse`, `HttpRequest`, `HttpHandlerDataRepository`, `ObjectNode`, `ArrayNode`, `JsonMapper`.

**Example:**
```json
{
  "type": "GROOVY",
  "method": "POST",
  "path": "/echo",
  "groovyCode": "return HttpResponse.builder().code(200).body(request.getBody()).build();"
}
```

---

### ROUTER handler

Forwards the incoming request to another registered handler.

**Extra fields:**

| Field        | Description                                         |
|--------------|-----------------------------------------------------|
| `routerPath` | Path of another handler to route the request to     |

The router matches the target handler by `method` + `routerPath` in the registry.

**Example:**
```json
{
  "type": "ROUTER",
  "method": "GET",
  "path": "/alias",
  "routerPath": "/hello"
}
```

This routes `GET /alias` to whatever handler is registered at `GET /hello`.

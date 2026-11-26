# REST API Design & Contract Standards

## 1. Architectural Philosophy

All HTTP REST APIs must adhere to standard REST architectural constraints, predictable URI structures, explicit HTTP verbs, standardized status codes, and uniform response envelopes across all microservices.

---

## 2. Resource URI Conventions

1. **Plural Nouns:** Use lowercase, hyphen-separated (kebab-case) plural nouns for collection resources:
   - `/api/v1/orders`
   - `/api/v1/customer-profiles`
   - `/api/v1/files`
2. **Hierarchical Sub-Resources:** Represent nested relationships clearly:
   - `/api/v1/orders/{orderId}/items`
   - `/api/v1/users/{userId}/addresses`
3. **No Verbs in URIs for Standard CRUD:** Use HTTP methods to define the action:
   - **CORRECT:** `POST /api/v1/orders`
   - **INCORRECT:** `POST /api/v1/orders/createOrder`
4. **Action / Controller Endpoints (Non-CRUD Operations):** For operations that do not map directly to CRUD entities, use a sub-resource verb:
   - `POST /api/v1/orders/{id}/cancel`
   - `POST /api/v1/files/{id}/presigned-upload`
   - `POST /api/v1/auth/refresh-token`
5. **API Versioning:** Version all public APIs in the URL path (`/api/v1/...`). Never release breaking changes without incrementing the major version.

---

## 3. Standard HTTP Methods & Status Codes

| HTTP Method | Purpose | Success Status Code | Idempotent |
| :--- | :--- | :--- | :--- |
| **`GET`** | Retrieve resource(s) | `200 OK` | Yes |
| **`POST`** | Create new resource | `201 Created` (with `Location` header or payload) | No |
| **`POST`** | Execute command or search | `200 OK` | No (requires `Idempotency-Key`) |
| **`PUT`** | Full resource replacement | `200 OK` or `204 No Content` | Yes |
| **`PATCH`** | Partial resource update | `200 OK` | No (requires `Idempotency-Key`) |
| **`DELETE`**| Remove resource | `204 No Content` or `200 OK` | Yes |

### Error Status Codes
- **`400 Bad Request`:** Syntactic validation error, missing required header/parameter, or malformed JSON payload.
- **`401 Unauthorized`:** Missing or invalid authentication token.
- **`403 Forbidden`:** Authenticated caller lacks permissions to access the specified resource.
- **`404 Not Found`:** Requested resource ID does not exist.
- **`409 Conflict`:** Concurrency collision (`OPTIMISTIC_LOCK_CONFLICT`), distributed lock timeout (`LOCK_ACQUISITION_FAILED`), or duplicate in-flight request (`IDEMPOTENT_OPERATION_IN_PROGRESS`).
- **`422 Unprocessable Entity`:** Syntactically valid payload violating domain business rules.
- **`500 Internal Server Error`:** Unhandled server error or unexpected infrastructure failure.

---

## 4. Standardized Error Response Envelope

All error responses across all microservices must follow a uniform JSON schema (or RFC 7807/9457 `ProblemDetails`):

```json
{
  "errorCode": "ORDER_NOT_FOUND",
  "errorMessage": "Order with ID '3fa85f64-5717-4562-b3fc-2c963f66afa6' was not found",
  "timestamp": "2026-11-19T10:00:00Z",
  "path": "/api/v1/orders/3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "errorDetails": {
    "quantity": "Must be greater than 0"
  }
}
```

- Handled centrally in `GlobalApiExceptionHandler`.
- `errorDetails` is populated when `@Valid` constraint violations occur (`MethodArgumentNotValidException`).

---

## 5. Pagination, Sorting & Filtering Standards

### 5.1 Pagination Request Query Parameters
Standardize query parameters across all paged search endpoints:
- `page`: Page index (1-based indexing for external clients, e.g. `page=1`).
- `pageSize`: Number of items per page (default `20`, maximum `100`).
- `sort`: Comma-separated field and direction, e.g. `sort=createdAt,desc`.

### 5.2 Pagination Response Envelope (`PagingResponse<T>`)
Never return unpaged arrays for unbounded collections. Always wrap list responses in a paginated envelope:
```json
{
  "items": [
    { "id": "uuid-1", "title": "Sample Item" }
  ],
  "page": 1,
  "pageSize": 20,
  "totalItems": 145,
  "totalPages": 8,
  "hasPrevious": false,
  "hasNext": true
}
```

---

## 6. HTTP Idempotency Protocol

- Every state-mutating request (POST, PUT, PATCH) must accept the **`Idempotency-Key`** header:
  ```http
  Idempotency-Key: 9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d
  ```
- **In-flight concurrent requests** with the same key return `409 Conflict` (`IDEMPOTENT_OPERATION_IN_PROGRESS`).
- **Completed requests** replay the cached status code, headers, and body with header:
  ```http
  Idempotency-Replay: true
  ```

---

## 7. OpenAPI 3.0 Documentation Standards

Every controller endpoint must be documented using SpringDoc OpenAPI annotations:
```java
@Operation(
    summary = "Create a new order",
    description = "Initiates order processing and reserves inventory under idempotency control."
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Order created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(responseCode = "409", description = "Concurrent conflict or idempotency collision", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
})
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
    @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
    @Valid @RequestBody CreateOrderRequest request
)
```

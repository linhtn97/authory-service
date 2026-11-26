# Application Security & Vulnerability Defense Standards

## 1. Secure-by-Design Principles

All backend microservices must adhere strictly to the principle of defense-in-depth against OWASP Top 10 vulnerabilities, unauthorized data exposure, and malicious payloads.

---

## 2. Input Sanitization & Injection Defense

### 2.1 SQL Injection Prevention
- Always use Spring Data JPA repository methods or parameterized JPQL queries.
- Never concatenate raw user input into native SQL strings:
  ```java
  // CORRECT: Parameterized query
  @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.customerId = :customerId")
  List<OrderEntity> findByStatusAndCustomer(@Param("status") OrderStatus status, @Param("customerId") UUID customerId);
  ```

### 2.2 Path Traversal & Filesystem Manipulation
If the service handles filesystem operations or object storage keys:
1. **Never use raw user-supplied filenames on disk.** Generate surrogate UUIDs for internal storage keys.
2. **Canonical Path Normalization & Root Containment:** Always normalize and verify the path remains within the configured root boundary:
   ```java
   Path rootPath = Paths.get(configuredRoot).toAbsolutePath().normalize();
   Path resolvedPath = rootPath.resolve(userKey).normalize();

   if (!resolvedPath.startsWith(rootPath)) {
       log.error("Path traversal attack detected! Key: {}", userKey);
       throw new SecurityException("Invalid path traversal sequence");
   }
   ```
3. **Filename Sanitization:** Strip directory separators (`/`, `\`), control characters, and null bytes from display names.

---

## 3. Payload & File Upload Verification

1. **Do NOT Trust Client-Reported Content-Type Blindly:** The `Content-Type` header can be spoofed by attackers.
2. **Extension Allowlisting:** Validate file extensions against an approved allowlist. Strictly reject executable or dangerous extensions (`.exe`, `.bat`, `.cmd`, `.sh`, `.jsp`, `.php`, `.dll`, `.vbs`, `.js`).
3. **Payload Size Enforcement:** Enforce maximum request and upload size limits in Spring Boot:
   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 50MB
         max-request-size: 50MB
   ```

---

## 4. Presigned URLs & Temporary Credential Security

If the service generates presigned URLs (e.g. AWS S3, Google Cloud Storage, MinIO):
1. **Short Expiration Windows:** Default expiration must be 15 minutes (900 seconds); maximum expiration must not exceed 1 hour.
2. **HTTP Method Binding:** Presigned upload URLs must enforce `HTTP PUT`; download URLs must enforce `HTTP GET`.
3. **Signed Headers Binding:** Bind critical headers (such as `Content-Type`) into the signature to prevent tampering.

---

## 5. Server-Side Request Forgery (SSRF) Defense

- External endpoint URLs (webhooks, storage endpoints, payment gateways) must be loaded from verified configuration or environment variables.
- Never construct outbound HTTP connection targets directly from unvalidated client-supplied headers or query parameters.
- Restrict outbound requests to private RFC-1918 IP addresses (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `127.0.0.1`) unless explicitly configured.

---

## 6. Authentication, Authorization & Secret Management

1. **Zero Hardcoded Secrets:** Never commit passwords, private keys, API secrets, or credentials to git repositories.
2. **Environment Variable Injection:** Use environment variable interpolation with local development defaults in `application.yaml`:
   ```yaml
   app:
     security:
       api-key: ${API_KEY:}
       jwt-secret: ${JWT_SECRET:}
   ```
3. **Sensitive Logging Masking:** Never log passwords, tokens, full credit cards, `Authorization` headers, or API keys. Exclude sensitive fields using Lombok `@ToString(exclude = "...")`.
4. **Security Headers:** Enforce defensive HTTP headers (HSTS, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`).

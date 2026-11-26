# Testing & Quality Assurance Standards

## 1. Testing Pyramid & Philosophy

The project prioritizes fast, deterministic, isolated unit tests over slow integration tests. Testing must provide high regression confidence while keeping local and CI build times under 60 seconds.

```
          / \
         /   \        Integration / E2E Tests (SpringBootTest + Testcontainers)
        /     \       - End-to-end database persistence, message broker delivery
       /-------\
      /         \     Controller / WebMvc Tests (MockMvc)
     /           \    - HTTP contracts, validation, status codes, JSON serialization
    /-------------\
   /               \  Unit Tests (Pure Java + Mockito)
  /                 \ - Domain models, value objects, use case orchestration
 /-------------------\
```

---

## 2. Unit Testing Standards (Domain & Use Cases)

### 2.1 Pure Unit Tests (Zero Spring Context)
- **Rule:** Never use `@SpringBootTest` to test a Use Case, Domain Model, or Mapper.
- Booting the Spring application context takes seconds; pure unit tests must execute in milliseconds.
- Annotate with `@ExtendWith(MockitoExtension.class)`.

### 2.2 Mocking Outbound Ports
```java
@ExtendWith(MockitoExtension.class)
class OrderGetServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CacheService cacheService;

    @InjectMocks private OrderGetService service;

    @Test
    @DisplayName("Should return OrderGetResult when order exists")
    void shouldReturnOrderWhenFound() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .title("Sample Order")
                .status(OrderStatus.CONFIRMED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        OrderGetResult result = service.execute(orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        verify(orderRepository).findById(orderId);
    }
}
```

### 2.3 Mockito Strictness & Lenient Stubbing
- Modern Mockito strictly flags unused stubs (`UnnecessaryStubbingException`).
- If a stub defined in `@BeforeEach` is not consumed by every test in the class, mark it as `lenient()`:
  ```java
  @BeforeEach
  void setUp() {
      lenient().when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/orders/test");
      lenient().when(i18nHelper.getMessage(anyString()))
              .thenAnswer(invocation -> invocation.getArgument(0));
  }
  ```

---

## 3. Controller & REST Layer Testing

Test REST controllers using `MockMvc` (standalone setup or `@WebMvcTest`):
- Verify HTTP response status codes (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`, `409 Conflict`).
- Verify JSON response attributes using JsonPath (`jsonPath("$.id")`).
- Verify `@Valid` constraints reject invalid request payloads with `400 Bad Request`.

```java
@Test
@DisplayName("POST /api/v1/orders - Should return 200 and created order")
void shouldCreateOrder() throws Exception {
    UUID id = UUID.randomUUID();
    CreateOrderRequest request = new CreateOrderRequest();
    request.setTitle("New Order");

    OrderCreateResult result = OrderCreateResult.builder()
            .id(id)
            .title("New Order")
            .status(OrderStatus.PENDING)
            .build();

    when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(result);

    mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.status").value("PENDING"));
}
```

---

## 4. Test Quality & Reliability Guardrails

1. **No `Thread.sleep()`:** Never introduce fixed sleep pauses to wait for asynchronous operations. Use `Awaitility` or mock schedulers.
2. **Deterministic Clocks:** When testing expiration logic, timestamps, or scheduled tasks, pass an explicit `Instant` or mockable `Clock`.
3. **AssertJ Assertions:** Standardize on AssertJ fluent assertions (`assertThat(...)`, `assertThatThrownBy(...)`).
4. **Mandatory Spotless Verification:** Format tests using the same code style rules as production code.

---

## 5. Execution Commands

```bash
# Gradle (Unix / macOS):
./gradlew test && ./gradlew check

# Gradle (Windows):
cmd.exe /c "gradlew.bat test && gradlew.bat check"

# Maven (Unix / macOS):
./mvnw test && ./mvnw verify

# Maven (Windows):
cmd.exe /c "mvnw.cmd test && mvnw.cmd verify"
```

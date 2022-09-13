# Test-Driven-Development
Sample project for TDD. 

- Unit Testing
- Integration Testing
- Testing External Services
- Mocking with Mockito
- Test Driven Development (TDD)
- Behavior Driven Development (BDD)

# Notes
## @Mock
We can do this either by using the **@RunWith(MockitoJUnitRunner.class)** to run the test, or by calling the **MockitoAnnotations.openMocks()** method explicitly.

## @Mock vs @MockBean
**@Mock** annotation mock the class only for the specified test class.

We can use the **@MockBean** to add mock objects to the Spring application context. The mock will replace any existing bean of the same type in the application context.

If no bean of the same type is defined, a new one will be added. This annotation is useful in integration tests where a particular bean, like an external service, needs to be mocked.

To use this annotation, we have to use **SpringRunner** to run the test:

```
@RunWith(SpringRunner.class)
public class MockBeanAnnotationIntegrationTest {
    
    @MockBean
    UserRepository mockRepository;
    
    @Autowired
    ApplicationContext context;
    
    @Test
    public void givenCountMethodMocked_WhenCountInvoked_ThenMockValueReturned() {
        Mockito.when(mockRepository.count()).thenReturn(123L);

        UserRepository userRepoFromContext = context.getBean(UserRepository.class);
        long userCount = userRepoFromContext.count();

        Assert.assertEquals(123L, userCount);
        Mockito.verify(mockRepository).count();
    }
}
```

## @Mock vs @Spy
**@Mock:** Nullified object of the class. All methods and fields are nullified. (objects equals null, primitives equals default values)

**@Spy:** Regular object of the class.  

Both can be used to mock methods or fields. The difference is that in mock, you are creating a complete mock or fake object while in spy, there is the real object and you just spying or stubbing specific methods of it.

While in spy objects, of course, since it is a real method, when you are not stubbing the method, then it will call the real method behavior. If you want to change and mock the method, then you need to stub it.

## MockMvc class
Use this class to perform HTTP requests. First, the Spring boot app started and test methods are called. 
```
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private MockMvc mockMvc;
...
```

## @Transactional
You can encounter a "not found exception" if the test has multiple **mockMvc** requests because the transaction does not commit when the test running. In this case, you should use **@Transactional** annotation to perform the transaction commit. 

## @SpringBootTest
Spring-Boot provides an **@SpringBootTest** annotation which provides spring-boot features over and above of the spring-test module. This annotation works by creating the ApplicationContext used in our tests through SpringApplication. It starts the embedded server, creates a web environment and then enables **@Test** methods to do integration testing.

By default, **@SpringBootTest**  does not start a server. We need to add attribute **webEnvironment** to further refine how your tests run. It has several options:

- **MOCK(Default):** Loads a web ApplicationContext and provides a mock web environment

- **RANDOM_PORT:** Loads a WebServerApplicationContext and provides a real web environment. The embedded server is started and listen on a random port. This is the one should be used for the integration test

- **DEFINED_PORT:** Loads a WebServerApplicationContext and provides a real web environment.

- **NONE:** Loads an ApplicationContext by using SpringApplication but does not provide any web environment

## MockMvc vs WebTestClient vs TestRestTemplate
All three tools help us invoke and test our Spring Boot application's endpoint. The main difference lies in whether we can perform requests against a mocked servlet environment and/or our servlet container runtime. Furthermore, not all tools are designed to work with both **Spring Web MVC (blocking)** and **Spring WebFlux (non-blocking)**.

In short, these three technologies serve the following purpose:

**MockMvc:** Fluent API to interact with a mocked servlet environment. No real HTTP communication. The perfect solution to verify blocking Spring WebMVC controller endpoints. We either bootstrap the MockMvc instance on our own, use @WebMvcTest or @SpringBootTest (without a port configuration). Includes API support for verifying the model or view name of a server-side rendered view endpoint.

**WebTestClient:** Originally the testing tool for invoking and verifying Spring WebFlux endpoints. However, we can also use it to write tests for a running servlet container or MockMvc. Fluent API that allows chaining the request and verification. There's no API support for verifying the model or view name of a server-side rendered view endpoint.

**TestRestTemplate:** Test and verify controller endpoints for a running servlet container over HTTP. Less fluent API. If our team is still familiar with the RestTemplate and hasn't made the transition to the WebTestClient (yet), we may favor this for our integration tests. We can't use the TestRestTemplate to interact with mocked servlet environment or Spring WebFlux endpoints. There's no API support for verifying the model or view name of a server-side rendered view endpoint.

![different  methods](https://user-images.githubusercontent.com/3144356/189852106-049d4ce1-190c-4314-b5ee-9aab54fabb5e.png)

# References:
- https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testing
- https://docs.spring.io/spring-boot/docs/2.7.3/reference/html/features.html#features.testing
- https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
- https://dzone.com/articles/integration-testing-in-spring-boot-1
- https://www.baeldung.com/java-spring-mockito-mock-mockbean
- https://github.com/amigoscode/software-testing

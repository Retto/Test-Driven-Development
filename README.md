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


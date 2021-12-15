# mock-sut

유닛테스트 코드 작성시 반복적으로 작성되는 `mocking 객체`를 간편하게 사용 할 수 있도록 해줍니다.  
annotation-processing 방식으로 mock 객체를 자동 생성해, 유닛테스트 타겟 클래스를 간편하게 선언 할 수 있습니다.

### Getting started

```gradle
repositories {
    ...
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.taes-k:mock-sut:$release-version")
    annotationProcessor("com.github.taes-k:mock-sut:$release-version")
    ...
}
```

### Examples

As-Is

```java
class SomethingServiceTest {

    @Mock
    SampleService1 sampleService1;

    @Mock
    SampleService1 sampleService2;

    @Mock
    SampleService1 sampleService3;

    @Mock
    SampleService1 sampleService4;

    @Mock
    SampleService1 sampleService5;

    @InjectMock
    SomethinigService sut;

    @BeforeEach
    void beforeEach() {
        Mockito.reset(sampleService1);
        Mockito.reset(sampleService2);
        Mockito.reset(sampleService3);
        Mockito.reset(sampleService4);
        Mockito.reset(sampleService5);
    }

    @Test
    void doSomething() {
        // given
        Mockito.when(sampleService1().getSample()).thenReturn(...);
        Mockito.when(sampleService2().getSample()).thenReturn(...);
        Mockito.when(sampleService3().getSample()).thenReturn(...);
        Mockito.when(sampleService4().getSample()).thenReturn(...);
        Mockito.when(sampleService5().getSample()).thenReturn(...);

        // when
        var result = sut.doSomething(...);

        // then
        then()...
    }
}
```

To-Be

```java
class SomethingServiceTest {
    private final SomethingServiceMockSutFactory sutFactory = new SomethingServiceMockSutFactory();
    private final SomethingService sut = sutFactory.getSut();

    @BeforeEach
    void beforeEach() {
        sutFactory.reset();
    }

    @Test
    void joinAllSamples() {
        // given
        Mockito.when(sutFactory.getSampleService1().getSample()).thenReturn(...);
        Mockito.when(sutFactory.getSampleService2().getSample()).thenReturn(...);
        Mockito.when(sutFactory.getSampleService3().getSample()).thenReturn(...);
        Mockito.when(sutFactory.getSampleService4().getSample()).thenReturn(...);
        Mockito.when(sutFactory.getSampleService5().getSample()).thenReturn(...);

        // when
        var result = sut.doSomething(...);

        // then
        then()...
    }
}
```

---

### Features

- `@MockSut`
- `**MockSutFactory`
  - `get**(): propertyField`
  - `getSut(): targetField`
  - `set**(field)`
  - `withSpy**(field)`
  - `reset()`

# mock-sut

유닛테스트 코드 작성시 반복적으로 작성되는 `mock`객체 지정 보일러플레이트 코드들을 자동화해줍니다.  
annotation-processing 방식으로 mock 객체를 자동 생성해, 유닛테스트의 대상이 되는 클래스 (`SUT`)를 간편하게 테스트 대상객체로 선언 할 수 있습니다.

### How to use

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

### Example

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


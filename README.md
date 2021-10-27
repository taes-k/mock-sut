# mock-sut

### Abstract
유닛테스트 코드 작성시 소모적으로 반복 작성되는 코드들을 자동화해줍니다.  
annotation-processing 방식으로 mock 객체를 자동 생성해, 유닛테스트의 대상이 되는 클래스 (`SUT`)를 간편하게 테스트 대상객체로 선언 할 수 있습니다.

### How to use

- `@MockSut`
- `*SutFactory`
### Example
As-Is

```
class SomethingServiceTest{

    @Mock
    SampleService1 SampleService1;

    @Mock
    SampleService1 SampleService2;

    @Mock
    SampleService1 SampleService3;

    @Mock
    SampleService1 SampleService4;

    @InjectMock
    SomethinigService sut;

    @Test
    void doSomething(){

        // given
        Mockito.when(SampleService1.doSample(...)).then(...);

        // when
        var result = sut.doSomething(...);

        // then
        then()...
    }
}
```


To-Be
```
@MockSut
public class SomethingService(){
    private final SampleService1;
    private final SampleService2;
    private final SampleService3;
    private final SampleService4;

    ...
}
```
```
class SomethingServiceTest{
    SomethingServiceSutFactory sutFactory = new SomethingServiceSutFactory();
    SomethingService sut = sutFactory.getSut();

    @Test
    void doSomething(){

        // given
        Mockito.when(sutFactory.getSampleService1.doSample(...)).then(...);

        // when
        var result = sut.doSomething(...);

        // then
        then()...
    }
}
```

---


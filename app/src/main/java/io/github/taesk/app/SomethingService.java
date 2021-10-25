package io.github.taesk.app;

import io.github.taesk.MockSut;

@MockSut
public class SomethingService {
    private final SampleService1 sampleService1;
    private final SampleService2 sampleService2;
    private final SampleService3 sampleService3;

    public SomethingService(SampleService1 sampleService1, SampleService2 sampleService2, SampleService3 sampleService3) {
        this.sampleService1 = sampleService1;
        this.sampleService2 = sampleService2;
        this.sampleService3 = sampleService3;
    }

    public void doSomething() {
        System.out.println(sampleService1.getSample());
    }
}

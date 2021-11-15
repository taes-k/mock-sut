package io.github.taesk.app;

import io.github.taesk.MockSut;
import lombok.RequiredArgsConstructor;

@MockSut
@RequiredArgsConstructor
public class LombokSomethingService {
    private final SampleService1 sampleService1;
    private final SampleService2 sampleService2;
    private final SampleService3 sampleService3;
    private final SampleService4 sampleService4;
    private final SampleService5 sampleService5;

    public String joinAllSamples() {
        String sample1 = sampleService1.getSample();
        String sample2 = sampleService2.getSample();
        String sample3 = sampleService3.getSample();
        String sample4 = sampleService4.getSample();
        String sample5 = sampleService5.getSample();

        return String.join(sample1, sample2, sample3, sample4, sample5);
    }
}

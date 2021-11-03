package io.github.taesk.app;

import io.github.taesk.MockSut;

@MockSut
public class SomethingService {
    private final SampleService1 sampleService1;
    private final SampleService2 sampleService2;
    private final SampleService3 sampleService3;
    private final SampleService4 sampleService4;
    private final SampleService5 sampleService5;

    public SomethingService(
            SampleService1 sampleService1,
            SampleService2 sampleService2,
            SampleService3 sampleService3,
            SampleService4 sampleService4,
            SampleService5 sampleService5) {
        this.sampleService1 = sampleService1;
        this.sampleService2 = sampleService2;
        this.sampleService3 = sampleService3;
        this.sampleService4 = sampleService4;
        this.sampleService5 = sampleService5;
    }

    public String joinAllSamples() {
        String sample1 = sampleService1.getSample();
        String sample2 = sampleService2.getSample();
        String sample3 = sampleService3.getSample();
        String sample4 = sampleService4.getSample();
        String sample5 = sampleService5.getSample();

        return String.join(sample1, sample2, sample3, sample4, sample5);
    }

    public String joinAllSamplesUppercase() {
        String sample1 = sampleService1.getSample().toUpperCase();
        String sample2 = sampleService2.getSample();
        String sample3 = sampleService3.getSample();
        String sample4 = sampleService4.getSample();
        String sample5 = sampleService5.getSample();

        return String.join(sample1, sample2, sample3, sample4, sample5).toUpperCase();
    }
}

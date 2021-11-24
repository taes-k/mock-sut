package io.github.taesk.app;

import io.github.taesk.app.data.SampleData;
import io.github.taesk.app.data.SampleData.SampleDataBuilder;

public class SampleService1 {
    public String getSample() {

        SampleDataBuilder sampleDataBuilder = SampleData.builder()
            .sampleString("sample1")
            .sampleInteger(1);

        SampleData sampleData = sampleDataBuilder.build();

        return sampleData.getSampleString();
    }
}

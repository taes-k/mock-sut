package io.github.taesk.app.data;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SampleData {
    private String sampleString;
    private Integer sampleInteger;
}

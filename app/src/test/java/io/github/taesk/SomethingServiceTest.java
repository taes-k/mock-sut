package io.github.taesk;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.taesk.app.SomethingService;
import io.github.taesk.app.SomethingServiceMockSutFactory;

class SomethingServiceTest {
    private final SomethingServiceMockSutFactory sutFactory =
        SomethingServiceMockSutFactory.builder()
            .withSpySampleService1()
            .build();
    private final SomethingService sut = sutFactory.getSut();

    @BeforeEach
    void beforeEach() {
        sutFactory.reset();
    }

    @Test
    void joinAllSamples() {
        // given
        String mkSample1 = sutFactory.getSampleService1().getSample(); // work as a spy
        String mkSample2 = RandomStringUtils.random(5);
        String mkSample3 = RandomStringUtils.random(5);
        String mkSample4 = RandomStringUtils.random(5);
        String mkSample5 = RandomStringUtils.random(5);

        Mockito.when(sutFactory.getSampleService2().getSample()).thenReturn(mkSample2);
        Mockito.when(sutFactory.getSampleService3().getSample()).thenReturn(mkSample3);
        Mockito.when(sutFactory.getSampleService4().getSample()).thenReturn(mkSample4);
        Mockito.when(sutFactory.getSampleService5().getSample()).thenReturn(mkSample5);

        // when
        String result = sut.joinAllSamples();

        // then
        Assertions.assertEquals(String.join(mkSample1, mkSample2, mkSample3, mkSample4, mkSample5), result);
    }

    @Test
    void joinAllSamplesUppercase() {
        // given
        String mkSample1 = sutFactory.getSampleService1().getSample(); // work as a spy
        String mkSample2 = RandomStringUtils.random(5);
        String mkSample3 = RandomStringUtils.random(5);
        String mkSample4 = RandomStringUtils.random(5);
        String mkSample5 = RandomStringUtils.random(5);

        Mockito.when(sutFactory.getSampleService2().getSample()).thenReturn(mkSample2);
        Mockito.when(sutFactory.getSampleService3().getSample()).thenReturn(mkSample3);
        Mockito.when(sutFactory.getSampleService4().getSample()).thenReturn(mkSample4);
        Mockito.when(sutFactory.getSampleService5().getSample()).thenReturn(mkSample5);

        // when
        String result = sut.joinAllSamplesUppercase();

        // then
        Assertions.assertEquals(String.join(mkSample1, mkSample2, mkSample3, mkSample4, mkSample5).toUpperCase(), result);

    }
}

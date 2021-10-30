package io.github.taesk;

import io.github.taesk.app.SomethingService;
import io.github.taesk.app.SomethingServiceSutFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SomethingServiceTest {
    private SomethingServiceSutFactory sutFactory = new SomethingServiceSutFactory();
    SomethingService sut = sutFactory.getSut();

    @Test
    void test() {
        // given
        var mkMessage = "mockMessage";
        Mockito.when(sutFactory.getSampleService1().getSample()).thenReturn(mkMessage);

        // when
        var result = sut.doSomething();

        // then
        Assertions.assertEquals(result, mkMessage);

    }
}

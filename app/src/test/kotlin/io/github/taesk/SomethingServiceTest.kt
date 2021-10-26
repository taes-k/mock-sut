package io.github.taesk

import io.github.taesk.app.SomethingServiceSutFactory
import org.junit.jupiter.api.Test
import org.mockito.Mockito


class SomethingServiceTest{
    val sutFactory = SomethingServiceSutFactory()
    val sut = sutFactory.sut

    @Test
    fun test(){
        Mockito.`when`(sutFactory.sampleService1.sample).thenReturn("SSS")
        sut.doSomething()
    }
}
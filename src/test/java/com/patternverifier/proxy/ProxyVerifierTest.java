package com.patternverifier.proxy;

import com.patternverifier.PatternAssertions;
import com.patternverifier.proxy.correct.ConcreteFieldProxy;
import com.patternverifier.proxy.correct.Image;
import com.patternverifier.proxy.correct.ImageProxy;
import com.patternverifier.proxy.correct.RealImage;
import com.patternverifier.proxy.wrong.AllViolationsProxy;
import com.patternverifier.proxy.wrong.MissingFieldProxy;
import com.patternverifier.proxy.wrong.MissingSubjectInterfaceProxy;
import com.patternverifier.proxy.wrong.NoDelegatingProxy;
import com.patternverifier.proxy.wrong.NotAnImage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProxyVerifierTest {

    @Test
    void correctProxyShouldPass() {
        PatternAssertions.assertThat(ImageProxy.class)
                .implementsProxy()
                .withRealSubject(RealImage.class)
                .forSubject(Image.class);
    }

    @Test
    void missingSubjectInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingSubjectInterfaceProxy.class)
                        .implementsProxy()
                        .withRealSubject(RealImage.class)
                        .forSubject(Image.class)
        );
        assertTrue(error.getMessage().contains("non implementa"),
                "Il messaggio dovrebbe indicare che il Proxy non implementa il Subject");
    }

    @Test
    void missingSubjectFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingFieldProxy.class)
                        .implementsProxy()
                        .withRealSubject(RealImage.class)
                        .forSubject(Image.class)
        );
        assertTrue(error.getMessage().contains("campo"),
                "Il messaggio dovrebbe indicare il campo Subject mancante");
    }

    @Test
    void invalidRealSubjectShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ImageProxy.class)
                        .implementsProxy()
                        .withRealSubject(NotAnImage.class)
                        .forSubject(Image.class)
        );
        assertTrue(error.getMessage().contains("non implementa"),
                "Il messaggio dovrebbe indicare che il RealSubject non implementa il Subject");
    }

    @Test
    void proxyWithConcreteFieldShouldPass() {
        // Variante: campo del tipo concreto RealImage invece dell'interfaccia Image
        PatternAssertions.assertThat(ConcreteFieldProxy.class)
                .implementsProxy()
                .withRealSubject(RealImage.class)
                .forSubject(Image.class);
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsProxy.class)
                        .implementsProxy()
                        .withRealSubject(RealImage.class)
                        .forSubject(Image.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("non implementa"), "Dovrebbe riportare la mancanza dell'interfaccia Subject");
        assertTrue(msg.contains("campo"),           "Dovrebbe riportare il campo Subject mancante");
        assertTrue(msg.contains("delega"),
                "Dovrebbe riportare che il Proxy non delega al Subject");
    }

    @Test
    void noDelegationProxyShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoDelegatingProxy.class)
                        .implementsProxy()
                        .withRealSubject(RealImage.class)
                        .forSubject(Image.class)
        );
        assertTrue(error.getMessage().contains("delega"),
                "Il messaggio dovrebbe indicare che il Proxy non delega al Subject");
    }
}

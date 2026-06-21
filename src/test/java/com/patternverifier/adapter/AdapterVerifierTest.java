package com.patternverifier.adapter;

import com.patternverifier.PatternAssertions;
import com.patternverifier.adapter.correct.LegacySocket;
import com.patternverifier.adapter.correct.ModernSocket;
import com.patternverifier.adapter.correct.MultiFieldAdapter;
import com.patternverifier.adapter.correct.SocketAdapter;
import com.patternverifier.adapter.wrong.AdapteeAlreadyImplementsTarget;
import com.patternverifier.adapter.wrong.AllViolationsAdapter;
import com.patternverifier.adapter.wrong.MissingFieldAdapter;
import com.patternverifier.adapter.wrong.MissingInterfaceAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdapterVerifierTest {

    @Test
    void correctAdapterShouldPass() {
        PatternAssertions.assertThat(SocketAdapter.class)
                .implementsAdapter()
                .fromAdaptee(LegacySocket.class)
                .toTarget(ModernSocket.class);
    }

    @Test
    void missingAdapteeFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingFieldAdapter.class)
                        .implementsAdapter()
                        .fromAdaptee(LegacySocket.class)
                        .toTarget(ModernSocket.class)
        );
        assertTrue(error.getMessage().contains("campo"),
                "Il messaggio dovrebbe indicare il campo Adaptee mancante");
    }

    @Test
    void missingTargetInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingInterfaceAdapter.class)
                        .implementsAdapter()
                        .fromAdaptee(LegacySocket.class)
                        .toTarget(ModernSocket.class)
        );
        assertTrue(error.getMessage().contains("non implementa"),
                "Il messaggio dovrebbe indicare che l'Adapter non implementa il Target");
    }

    @Test
    void adapteeAlreadyImplementsTargetShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(SocketAdapter.class)
                        .implementsAdapter()
                        .fromAdaptee(AdapteeAlreadyImplementsTarget.class)
                        .toTarget(ModernSocket.class)
        );
        assertTrue(error.getMessage().contains("implementa già"),
                "Il messaggio dovrebbe indicare che l'Adaptee implementa già il Target");
    }

    @Test
    void adapterWithExtraFieldsShouldPass() {
        PatternAssertions.assertThat(MultiFieldAdapter.class)
                .implementsAdapter()
                .fromAdaptee(LegacySocket.class)
                .toTarget(ModernSocket.class);
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsAdapter.class)
                        .implementsAdapter()
                        .fromAdaptee(LegacySocket.class)
                        .toTarget(ModernSocket.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("non implementa"), "Dovrebbe riportare la mancanza dell'interfaccia Target");
        assertTrue(msg.contains("campo"),           "Dovrebbe riportare il campo Adaptee mancante");
    }
}

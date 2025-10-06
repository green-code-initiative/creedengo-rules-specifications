package org.greencodeinitiative.mavenbuild.ruleexporter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class MainTest {

    @Test
    void shouldThrowIfThereIsNoSourceDir() {
        // Given
        Main main = new Main(new String[]{});

        // When
        Throwable thrown = catchThrowable(main::run);

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("argument 1 is required: sourceDir");
    }

    @Test
    void shouldThrowIfThereIsNoTargetDir() {
        // Given
        Main main = new Main(new String[]{"someSourceDir"});

        // When
        Throwable thrown = catchThrowable(main::run);

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("argument 2 is required: targetDir");
    }

}

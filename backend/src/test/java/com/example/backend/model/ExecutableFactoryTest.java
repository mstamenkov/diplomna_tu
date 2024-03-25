package com.example.backend.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutableFactoryTest {

    @ParameterizedTest
    @MethodSource
    void givenValidExecutableName_whenCreatingExecutable_thenCorrectExecutableIsReturned(String commandName, Class<?> executable) {
        ExecutableFactory executableFactory = new ExecutableFactory();
        assertThat(executableFactory.create(commandName)).isInstanceOf(executable);
    }

    private static Stream<Arguments> givenValidExecutableName_whenCreatingExecutable_thenCorrectExecutableIsReturned() {
        return Stream.of(
                Arguments.of("OSCommand", OSExecutable.class),
                Arguments.of("HttpRequestCommand", HttpRequestExecutable.class)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"InvalidCommand", "commandInvalid"})
    @EmptySource
    void givenUnsupportedExecutableName_whenCreatingExecutable_thenExceptionIsThrown(String input) {
        ExecutableFactory executableFactory = new ExecutableFactory();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            executableFactory.create(input);
        });
        assertThat(exception.getMessage()).isEqualTo(format("%s is not a valid executable", input));
    }


}

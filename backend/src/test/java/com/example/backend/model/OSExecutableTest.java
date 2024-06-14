package com.example.backend.model;

import com.example.backend.exception.ExecutableException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OSExecutableTest {
    private static final OSExecutable os = new OSExecutable();

    @Test
    void givenValidInputKeys_whenExecutingOSCommand_thenCommandResultIsReturned() {
        Map<String, Object> result = os.execute(Map.of("command", "echo This is test text line"));
        assertThat(result.get("output").toString()).contains("This is test text line");
        assertThat(result.get("exitCode")).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ming", "kracert"})
    void givenInvalidInputKeys_whenExecutingOSCommand_thenExceptionIsThrown(String command) {
        Throwable exception = assertThrows(ExecutableException.class, () -> os.execute(Map.of("command", command)));
        assertThat(exception.getMessage()).isIn(format("'%s' is not recognized as an internal or external command,\n" +
                "operable program or batch file.\n", command), format("sh: 1: %s: not found\n",command));
    }

    @Mock
    ProcessBuilder processBuilder = new ProcessBuilder();

    @Test
    void whenOSExecutableInternalError_thenExceptionIsThrown() throws IOException {
        Mockito.when(processBuilder.start()).thenThrow(new IOException("test message"));
        OSExecutable executable = new OSExecutable(processBuilder);
        Throwable exception = assertThrows(ExecutableException.class, () -> executable.execute(Map.of("command", "ping 8.8.8.8")));
        assertThat(exception.getMessage()).isEqualTo("test message");
    }
}

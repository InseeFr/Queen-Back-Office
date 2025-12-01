package fr.insee.queen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(OutputCaptureExtension.class)
class JMSApplicationTest {

    @Test
    void main_invokesConfigureBuildAndRun_withoutBooting() {
        // We will capture the mock object returned by build() to verify run(args).
        AtomicReference<SpringApplication> appRef = new AtomicReference<>();

        try (MockedConstruction<SpringApplicationBuilder> builderCons =
                     mockConstruction(SpringApplicationBuilder.class, (builderMock, ctx) -> {
                         // Fluent API : on renvoie le même builder
                         when(builderMock.sources(JMSApplication.class)).thenReturn(builderMock);
                         when(builderMock.listeners()).thenReturn(builderMock);

                         // build() renvoie un SpringApplication mocké
                         SpringApplication appMock = mock(SpringApplication.class);
                         appRef.set(appMock);
                         when(builderMock.build()).thenReturn(appMock);
                     })) {

            String[] args = {"--any=test"};
            JMSApplication.main(args);

            // We retrieve the constructed builder and check the sequence.
            SpringApplicationBuilder builderMock = builderCons.constructed().get(0);
            verify(builderMock).sources(JMSApplication.class);
            verify(builderMock).listeners();
            verify(builderMock).build();

            // And we verify that run() is called with the same arguments.
            verify(appRef.get()).run(args);

            verifyNoMoreInteractions(builderMock, appRef.get());
        }
    }

    @Test
    void configureApplicationBuilder_registersJmsApplicationAsSource() {
        // Given
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        // When
        SpringApplication springApplication =
                JMSApplication.configureApplicationBuilder(builder).build();
        // Then
        Set<Object> sources = springApplication.getAllSources();
        assertThat(sources).contains(JMSApplication.class);
    }

    @Test
    void handleApplicationReady_logsStartupMessage(CapturedOutput output) {
        JMSApplication app = new JMSApplication();
        app.handleApplicationReady(null);
        assertThat(output).contains(
                "=============== Queen listener JMS has successfully started. ==============="
        );
    }
}
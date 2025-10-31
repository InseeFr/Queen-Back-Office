package fr.insee.queen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(OutputCaptureExtension.class)
class JMSApplicationTest {

    @Test
    void main_invokesConfigureBuildAndRun_withoutBooting() {
        // On va capturer le mock retourné par build() pour vérifier run(args)
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

            // On récupère le builder construit et on vérifie l’enchaînement
            SpringApplicationBuilder builderMock = builderCons.constructed().get(0);
            verify(builderMock).sources(JMSApplication.class);
            verify(builderMock).listeners();
            verify(builderMock).build();

            // Et on vérifie que run() est appelé avec les mêmes args
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

//        ConfigurableApplicationContext ctx = mock(ConfigurableApplicationContext.class);
//        ApplicationReadyEvent event = new ApplicationReadyEvent(
//                new SpringApplication(JMSApplication.class),
//                new String[] {},
//                ctx
//        );

        app.handleApplicationReady(null);

        assertThat(output).contains(
                "=============== Queen listener JMS has successfully started. ==============="
        );
    }
}
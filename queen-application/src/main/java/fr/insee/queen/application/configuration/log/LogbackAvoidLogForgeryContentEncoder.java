package fr.insee.queen.application.configuration.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.text.StringEscapeUtils;

public class LogbackAvoidLogForgeryContentEncoder extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String content = super.convert(event);
        return StringEscapeUtils.escapeHtml4(
                content
                        .replace('\n', '_')
                        .replace('\r', '_'));
    }
}

package fr.insee.queen.application.configuration.rest;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte [] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        // Retrieve the Trace Identifier from MDC
        String traceIdentifier = MDC.get("id");

        if (traceIdentifier != null) {
            // Add the Trace ID to the outgoing request's headers
            request.getHeaders().add("traceparent", traceIdentifier);
        }

        return execution.execute(request, body);
    }
}

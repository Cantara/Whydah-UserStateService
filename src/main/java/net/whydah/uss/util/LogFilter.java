package net.whydah.uss.util;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.security.SecureRandom;
import java.util.Base64;


public class LogFilter implements ContainerResponseFilter, ContainerRequestFilter, ClientRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    private static final String MDC_REQUEST_ID = "req-id";
    private static final String MDC_TRACE_ID = "trace-id";
    public static final String REQ_ID_HEADER = "x-uss-req-id";
    public static final String TRACE_ID_HEADER = "x-uss-trace-id";

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();


    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String path = requestContext.getUriInfo().getPath();

        if (requestContext.getHeaders().containsKey(TRACE_ID_HEADER)) {
            final String traceId = requestContext.getHeaders().getFirst(TRACE_ID_HEADER);
            mdc(traceId);

        } else {
            mdc();
        }

        if (!"health".equals(path)) {
            log.info("Incoming request: {} {}", requestContext.getMethod(), path);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        final String path = requestContext.getUriInfo().getPath();
        if (!"health".equals(path)) {
            log.info("Response: {} {} {}", requestContext.getMethod(), path, responseContext.getStatus());
        }
        responseContext.getHeaders().add(REQ_ID_HEADER, MDC.get(MDC_REQUEST_ID));
        responseContext.getHeaders().add(TRACE_ID_HEADER, MDC.get(MDC_TRACE_ID));

        clear();


        //CORS
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(200);
        }
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
        // This does not work if okhttp is used for client-execution, check TracingOkHttpClientInterceptor.class
        clientRequestContext.getHeaders().add(TRACE_ID_HEADER, MDC.get(MDC_TRACE_ID));
    }


    public static void mdc() {
        final String id = createId();
        MDC.put(MDC_REQUEST_ID, id);
        MDC.put(MDC_TRACE_ID, id);
    }

    public static void mdc(String traceId) {
        final String id = createId();
        MDC.put(MDC_REQUEST_ID, id);
        MDC.put(MDC_TRACE_ID, traceId);
    }

    public static String trace() {
        return MDC.get(MDC_TRACE_ID);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String createId() {
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }


}
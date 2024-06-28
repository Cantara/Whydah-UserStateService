package net.whydah.uss.provider;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.whydah.uss.exception.AppException;
import net.whydah.uss.exception.ErrorMessage;
import net.whydah.uss.exception.ExceptionConfig;
import no.cantara.config.ApplicationProperties;

@Provider
public class AppExceptionProvider implements ExceptionMapper<Throwable> {

    private final ApplicationProperties config;

    public AppExceptionProvider(ApplicationProperties config) {
        this.config = config;
    }

    @Override
    public Response toResponse(Throwable t) {
        Throwable root = t;

        while (!(root instanceof AppException) && root.getCause() != null) {
            root = root.getCause();
        }

        if (root instanceof AppException) {
            String error_res = ExceptionConfig.handleSecurity(new ErrorMessage((AppException) root), config.get("app.errorlevel")).toString();
            return Response.status(((AppException) root).getStatus()).
                    entity(error_res).
                    type(MediaType.APPLICATION_JSON).
                    build();

        } else {
            ErrorMessage errorMessage = new ErrorMessage();
            setHttpStatus(t, errorMessage);
            errorMessage.setCode(9999);
            errorMessage.setMessage(t.getMessage());
            StringWriter errorStackTrace = new StringWriter();
            t.printStackTrace(new PrintWriter(errorStackTrace));
            errorMessage.setDeveloperMessage(errorStackTrace.toString());
            errorMessage.setLink("");

            return Response.status(errorMessage.getStatus())
                    .entity(errorMessage)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
        if (ex instanceof WebApplicationException) {
            errorMessage.setStatus(((WebApplicationException) ex).getResponse().getStatus());
        } else {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); //defaults to internal server error 500
        }
    }


}

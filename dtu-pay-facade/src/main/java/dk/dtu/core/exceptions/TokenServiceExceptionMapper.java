package dk.dtu.core.exceptions;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Token Service Exception Mapper with error codes
 *
 * @author Simão Teixeira (s232431)
 */
@Provider
public class TokenServiceExceptionMapper implements ExceptionMapper<TokenServiceException> {

    private static final Logger log = LogManager.getLogger(TokenServiceExceptionMapper.class);

    @Override
    public Response toResponse(TokenServiceException e) {
        String errorCode = e.getErrorCode();
        String errorMessage = e.getMessage();

        Response.Status status;
        String message;

        switch (errorCode) {
            case "ILLEGAL_NUMBER_REQUESTED_TOKENS", "INVALID_TOKEN", "INVALID_CUSTOMER_ACCOUNT":
                status = Response.Status.BAD_REQUEST;
                message = e.getMessage();
                break;
            case "UNKNOWN_ERROR":
                status = Response.Status.INTERNAL_SERVER_ERROR;
                message = e.getMessage();
                break;
            default:
                status = Response.Status.INTERNAL_SERVER_ERROR;
                message = "An unexpected error occurred";
                Log.error("Error code: " + errorCode + " not defined");
        }

        Log.error(errorCode, message, e);
        return Response.status(status).entity(message).build();
    }
}

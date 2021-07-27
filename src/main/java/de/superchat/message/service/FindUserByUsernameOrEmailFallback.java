package de.superchat.message.service;

import javax.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.jboss.logging.Logger;

public class FindUserByUsernameOrEmailFallback implements FallbackHandler<Response> {

    public static final Logger LOGGER = Logger.getLogger(FindUserByUsernameOrEmailFallback.class);

    /**
     * Circuit breaker for rest call to find user by username or email.
     *
     * @param context
     * @return always null
     */
    @Override
    public Response handle(ExecutionContext context) {
        LOGGER.error("RestCient was failed to find user by username or email " + context.getParameters()[0]);
        return null;
    }

}
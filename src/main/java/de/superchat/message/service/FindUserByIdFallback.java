package de.superchat.message.service;

import javax.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.jboss.logging.Logger;

public class FindUserByIdFallback implements FallbackHandler<Response> {

    public static final Logger LOGGER = Logger.getLogger(FindUserByIdFallback.class);

    /**
     * Circuit breaker for rest call to find user by id.
     *
     * @param context
     * @return user with id only
     */
    @Override
    public Response handle(ExecutionContext context) {
        LOGGER.error("RestCient was failed to find user by id " + context.getParameters()[0]);
        return null;
    }

}
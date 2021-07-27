package de.superchat.message.service;

import de.superchat.message.dto.UserDTO;
import java.util.UUID;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

public class FindUserByUsernameOrEmailFallback implements FallbackHandler<UserDTO> {

    /**
     * Circuit breaker for rest call to find user by username or email.
     *
     * @param context
     * @return always null
     */
    @Override
    public UserDTO handle(ExecutionContext context) {
        return null;
    }

}
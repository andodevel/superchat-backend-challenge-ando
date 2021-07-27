package de.superchat.message.service;

import de.superchat.message.dto.UserDTO;
import java.util.UUID;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

public class FindUserByIdFallback implements FallbackHandler<UserDTO> {

    /**
     * Circuit breaker for rest call to find user by id.
     *
     * @param context
     * @return user with id only
     */
    @Override
    public UserDTO handle(ExecutionContext context) {
        UUID userId = (UUID) context.getParameters()[0];
        UserDTO user = new UserDTO();
        user.setId(userId);
        return user;
    }

}
package de.superchat.auth.service;

import de.superchat.auth.repository.AuthUser;

public interface AuthService {

    /**
     * Check if user and password is valid Superchat user.
     *
     * @param username username or email
     * @param password raw password
     * @return Superchat user if correct user and password provided.
     */
    AuthUser authenticate(String username, String password);

    /**
     * Generate JWT access token for Superchat user. External(dummy) user is not allowed to login.
     *
     * @param authUser Superchat user only
     * @return jwt access token
     */
    String generateJWTToken(AuthUser authUser);

}

package de.superchat.auth.service;

import de.superchat.auth.repository.AuthUser;
import de.superchat.auth.repository.AuthRole;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import java.security.SecureRandom;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    public static final Logger LOGGER = Logger.getLogger(AuthService.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String jwtIssuer;

    @ConfigProperty(name = "de.superchat.jwt.duration")
    long jwtDuration;

    @ConfigProperty(name = "de.superchat.auth.bcrypt.secret")
    String bcryptSecret;

    @ConfigProperty(name = "de.superchat.auth.bcrypt.count")
    short bcryptCount;

    /**
     * Check if user and password is valid Superchat user.
     *
     * @param username username
     * @param password raw password
     * @return Superchat user if correct user and password provided.
     */
    @Override
    public AuthUser authenticate(String username, String password) {
        AuthUser authUser = AuthUser.findByUsernameOrEmail(username);
        if (authUser == null) {
            LOGGER.error("User " + username + " was not found in Auth DB");
            return null;
        }

        String hashedDBPassword = authUser.getPassword();
        String hexSalt = authUser.getSalt();
        String hashedPassword = null;
        try {
            hashedPassword = BcryptUtil.bcryptHash(bcryptSecret + password, bcryptCount, Hex.decodeHex(hexSalt));
        } catch (DecoderException e) {
            LOGGER.error("Failed to hash password of user " + username, e);
        }
        if (StringUtils.equals(hashedDBPassword, hashedPassword)) {
            LOGGER.info("User " + username + " was authenticated");
            return authUser;
        }

        LOGGER.warn("Failed to authenticate user " + username);
        return null;
    }

    /**
     * Generate JWT access token for Superchat user. External(dummy) user is not allowed to login.
     *
     * @param authUser Superchat user only
     * @return jwt access token
     */
    @Override
    public String generateJWTToken(AuthUser authUser) {
        String username = authUser.getUsername();
        String email = authUser.getEmail();
        Set<AuthRole> roles = authUser.getRoles();

        JwtClaimsBuilder claimsBuilder = Jwt.claims();
        long currentTimeInSecs = System.currentTimeMillis() / 1000;
        claimsBuilder.subject(username);
        claimsBuilder.issuer(jwtIssuer);
        claimsBuilder.groups(roles.stream().map(AuthRole::getId).collect(Collectors.toSet()));
        claimsBuilder.issuedAt(currentTimeInSecs);
        claimsBuilder.expiresAt(currentTimeInSecs + jwtDuration);
        claimsBuilder.claim(Claims.email.name(), email);

        String token = claimsBuilder.sign();
        LOGGER.info("Generated JWT token for `" + username + "`");
        return token;
    }

    /**
     * Random a password salt to be used with bcrypt
     *
     * @return salt as array of byte
     */
    private static byte[] randomBcryptSalt() {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}

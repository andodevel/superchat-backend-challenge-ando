package de.superchat.user.service;

import de.superchat.user.dto.CreateRequest;
import de.superchat.user.repository.User;
import de.superchat.user.repository.UserInfo;
import de.superchat.user.utils.Utils;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import java.util.Date;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    public static final Logger LOGGER = Logger.getLogger(UserService.class);

    @ConfigProperty(name = "de.superchat.auth.bcrypt.secret")
    String bcryptSecret;
    @ConfigProperty(name = "de.superchat.auth.bcrypt.count")
    Short bcryptCount;
    @ConfigProperty(name = "de.superchat.auth.default.page.size")
    Integer defaultPageSize;
    @ConfigProperty(name = "de.superchat.auth.max.page.size")
    Integer maxPageSize;

    /**
     * Find user by their id
     *
     * @param id
     * @return
     */
    @Override
    public User find(UUID id) {
        return User.findById(id);
    }

    /**
     * Find user by username or email
     *
     * @param usernameOrEmail
     * @return
     */
    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return User.findByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * List users with pagination
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PanacheQuery<User> list(Integer page, Integer size) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 0 ? defaultPageSize : size;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        PanacheQuery<PanacheEntityBase> all = User.findAll();
        LOGGER.info("Query users with page " + page + ", " + size);
        return all.page(Page.of(pageIndex, pageSize));
    }

    /**
     * Create new user
     *
     * @param createRequest
     * @return
     */
    @Override
    @Transactional
    public UUID create(CreateRequest createRequest) throws ResourceConflictException {
        String username = createRequest.getUsername().trim();
        String email = createRequest.getEmail().trim();
        User dbUser = User.find("username = ?1 OR email = ?2", username, email).firstResult();
        if (dbUser != null) {
            LOGGER.warn("User with name " + username + " or email " + email + " already existed!");
            throw new ResourceConflictException();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        byte[] salt = Utils.randomBcryptSalt();
        String password = createRequest.getPassword().trim();
        String hashedPassword = BcryptUtil.bcryptHash(bcryptSecret + password, bcryptCount, salt);
        newUser.setSalt(Hex.encodeHexString(salt));
        newUser.setPassword(hashedPassword);
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(newUser);
        userInfo.setFirstname(createRequest.getFirstname());
        userInfo.setLastname(createRequest.getLastname());
        userInfo.setCreated(new Date());
        userInfo.persist();
        newUser.persist();

        return newUser.getId();
    }
}

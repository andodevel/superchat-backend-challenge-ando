package de.superchat.message.substitution;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import de.superchat.user.repository.User;
import de.superchat.user.service.UserService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class UserSubstitution implements Substitution {

    /**
     * TODO: This code violate my idea of microservice. This is a work around because using Rest Client fail in case of
     * Webhook(see @MessageServiceImpl.create() method)
     */
    @Inject
    UserService userService;

    /**
     * TODO: Better use distribution cache like Redis + background jobs to update.
     */
    static Cache<String, String> userCache;

    private synchronized void initCache() {
        if (userCache != null) {
            return;
        }

        CacheLoader<String, String> loader;
        loader = new CacheLoader<>() {
            @Override
            public String load(String username) {
                User user = userService.findByUsernameOrEmail(username);
                if (user != null) {
                    String firstname = user.getUserInfo().getFirstname();
                    String lastname = user.getUserInfo().getLastname();
                    String fullname = (StringUtils.isBlank(firstname) ? "" : firstname)
                        + (StringUtils.isBlank(lastname) ? "" : (StringUtils.isBlank(firstname) ? "" : " ") + lastname);
                    return "".equals(fullname) ? username : fullname;
                }

                return username;
            }
        };

        userCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(loader);
    }

    @Override
    public String substitute(String placeholder) {
        initCache();

        String username = placeholder.replace("@", "");
        return userCache.getUnchecked(username);
    }

    @Override
    public String getPlaceholderPattern() {
        return "\\@[a-zA-Z0-9_-]+";
    }
}

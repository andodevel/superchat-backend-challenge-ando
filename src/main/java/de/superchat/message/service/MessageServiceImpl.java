package de.superchat.message.service;

import de.superchat.message.repository.Message;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageServiceImpl implements MessageService {

    public static final Logger LOGGER = Logger.getLogger(MessageService.class);

    @ConfigProperty(name = "de.superchat.auth.default.page.size")
    Integer defaultPageSize;
    @ConfigProperty(name = "de.superchat.auth.max.page.size")
    Integer maxPageSize;

    /**
     * List message with pagination. Support filters
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PanacheQuery<Message> list(Integer page, Integer size) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 0 ? defaultPageSize : size;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        PanacheQuery<PanacheEntityBase> all = Message.findAll();
        LOGGER.info("Query messages with page " + page + ", " + size);
        return all.page(Page.of(pageIndex, pageSize));
    }

}

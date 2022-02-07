package com.elepy.mongo;

import com.elepy.DatabaseSettings;
import com.elepy.dao.CrudFactory;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.uploads.FileService;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

@Slf4j
@ApplicationScoped
public class MongoExtension implements Extension {

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager bm) {
        if (bm.getBeans(MongoConfiguration.class).stream().findFirst().isEmpty()){
            throw new ElepyConfigException("No MongoConfiguration");
        }
        event.addBean()
                .beanClass(CrudFactory.class)
                .scope(ApplicationScoped.class)
                .createWith(x -> new MongoCrudFactory())
                .produceWith(objects -> new MongoCrudFactory());
        log.info("Setup Mongo extension");
    }

    @Produces
    public CrudFactory mongoCrudFactory(){
        return CDI.current().select(MongoCrudFactory.class).get();
    }

    @Produces
    public FileService mongoFileService(MongoConfiguration configuration) {

        if (configuration.getBucket() != null) {
            return new MongoFileService(configuration.getMongoClient().getDatabase(configuration.getDatabaseName()), configuration.getBucket());
        } else {
            return null;
        }
    }
}

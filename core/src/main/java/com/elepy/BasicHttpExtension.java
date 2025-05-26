package com.elepy;

import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import jakarta.enterprise.inject.spi.CDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class BasicHttpExtension implements ElepyExtension {
    private static final Logger logger = LoggerFactory.getLogger(BasicHttpExtension.class);

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepyCfg) {
        final var schemas = elepyCfg.modelSchemas();
        http.before(ctx -> {
            ctx.request().attribute("elepyContext", elepyCfg.getElepy());
            ctx.request().attribute("schemas", schemas);
            ctx.request().attribute("start", System.currentTimeMillis());
        });
        http.after(ctx -> {
            ctx.response().header("Access-Control-Allow-Origin", "*");
            ctx.response().header("Access-Control-Allow-Methods", "POST, PUT, DELETE");
            ctx.response().header("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin, Accept-Language");

            if (!ctx.request().method().equalsIgnoreCase("OPTIONS") && ctx.response().status() != 404)
                logger.info(String.format("%s\t['%s']: %dms", ctx.request().method(), ctx.request().uri(), System.currentTimeMillis() - ((Long) ctx.attribute("start"))));
        });

        http.exception(Exception.class, (exception, context) -> {
            final ElepyException elepyException;
            if (exception instanceof InvocationTargetException && ((InvocationTargetException) exception).getTargetException() instanceof ElepyException) {
                exception = (ElepyException) ((InvocationTargetException) exception).getTargetException();
            }
            if (exception instanceof ElepyException) {
                logger.debug("ElepyException", exception);
                elepyException = (ElepyException) exception;
            } else {
                logger.error(exception.getMessage(), exception);
                elepyException = ElepyException.internalServerError(exception);
            }

            if (elepyException.getStatus() == 500) {
                logger.error(exception.getMessage(), exception);
                exception.printStackTrace();
            }
            context.type("application/json");

            context.status(elepyException.getStatus());
            final var message = elepyException.getTranslatedMessage();
            context.result(Message.of(message, elepyException.getMetadata(), elepyException.getStatus()));

        });
    }
}

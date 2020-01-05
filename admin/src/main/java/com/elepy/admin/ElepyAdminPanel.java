package com.elepy.admin;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.concepts.ElepyAdminPanelPlugin;
import com.elepy.admin.concepts.PluginHandler;
import com.elepy.admin.concepts.ViewHandler;
import com.elepy.admin.views.ResourceLocation;
import com.elepy.annotations.Inject;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContextHandler;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.models.Schema;
import com.mitchellbosecke.pebble.PebbleEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.elepy.exceptions.HaltException.halt;


public class ElepyAdminPanel implements ElepyExtension {

    private final PebbleEngine engine = new PebbleEngine.Builder().build();

    private PluginHandler pluginHandler;
    private ViewHandler viewHandler;
    private boolean initiated = false;
    @Inject
    private Crud<User> userCrud;
    private List<Schema<?>> schemas;


    private NoUserFoundHandler noUserFoundHandler;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {


        this.schemas = elepy.models().stream().filter(Schema::isViewableOnCMS).collect(Collectors.toList());
        this.pluginHandler = new PluginHandler(this, http);
        this.viewHandler = new ViewHandler(schemas, this, http);
        this.noUserFoundHandler = (ctx) -> {
            ctx.response().redirect("/elepy-initial-user");
            halt();
        };
        setupLogin(http);

        setupInitialUser(http);
        setupAdminFilters(http, elepy);

        this.initiated = true;
    }


    public HttpContextHandler createProtectedFilter() {
        return ctx -> {

            try {
                ctx.loggedInUserOrThrow();
            } catch (ElepyException e) {
                ctx.redirect("/elepy-login");
            }
        };
    }

    private void setupAdminFilters(HttpService http, ElepyPostConfiguration elepy) {
        http.before("/admin/*/*", createProtectedFilter());
        http.before("/admin/*", createProtectedFilter());
        http.before("/admin", createProtectedFilter());

        http.get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("plugins", pluginHandler.getPlugins());
            response.result(renderWithDefaults(request, model, "admin-templates/base.peb"));
        });
        http.get("/admin-logout", (request, response) -> {
            response.removeCookie("ELEPY_TOKEN");
            response.redirect("/elepy-login");
        });
        this.viewHandler.setupModels(elepy);
        this.pluginHandler.setupPlugins(elepy);
        this.pluginHandler.setupRoutes(elepy);
    }


    private void setupLogin(HttpService http) {
        http.get("/elepy-login", (request, response) -> response.result(renderWithDefaults(request, new HashMap<>(), "admin-templates/login.peb")));

        http.before("/elepy-login", ctx -> {
            if (userCrud.count() <= 0) {
                noUserFoundHandler.handle(ctx);
            }
        });
    }

    private void setupInitialUser(HttpService http) {
        http.before("/elepy-initial-user", (request, response) -> {
            if (userCrud.count() > 0) {
                response.redirect("/elepy-login", 301);
                halt();
            }
        });
        http.get("/elepy-initial-user", (request, response) -> response.result(renderWithDefaults(request, new HashMap<>(), "admin-templates/initial-user.peb")));
    }


    public String renderWithDefaults(Request request, Map<String, Object> model, String templatePath) throws IOException {
        model.put("models", schemas);
        model.put("cssLocation", request.elepy().getDependency(ResourceLocation.class).getCssLocation());
        model.put("properties", request.elepy().getDependency(Properties.class));
        model.put("plugins", pluginHandler.getPlugins());
        return render(model, templatePath);
    }


    public String render(Map<String, Object> model, String templatePath) throws IOException {
        Writer writer = new StringWriter();
        engine.getTemplate(templatePath).evaluate(writer, model);
        return writer.toString();
    }

    public ElepyAdminPanel addPlugin(ElepyAdminPanelPlugin plugin) {
        return pluginHandler.addPlugin(plugin);
    }

    public boolean isInitiated() {
        return initiated;
    }

}

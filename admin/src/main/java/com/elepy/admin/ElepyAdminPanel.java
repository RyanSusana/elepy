package com.elepy.admin;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.concepts.ElepyAdminPanelPlugin;
import com.elepy.admin.concepts.PluginHandler;
import com.elepy.admin.concepts.ViewHandler;
import com.elepy.admin.models.Link;
import com.elepy.annotations.Inject;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.describers.Model;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContextHandler;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.mitchellbosecke.pebble.PebbleEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;


public class ElepyAdminPanel implements ElepyExtension {
    private PluginHandler pluginHandler;
    private ViewHandler viewHandler;
    private List<Link> links;
    private boolean initiated = false;

    @Inject
    private Crud<User> userCrud;


    private PebbleEngine engine;

    private List<Model<?>> modelContexts;


    private NoUserFoundHandler noUserFoundHandler;


    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        try {
            this.pluginHandler = new PluginHandler(this, http);
            this.viewHandler = new ViewHandler(this, http);
            this.noUserFoundHandler = (ctx) -> {
                ctx.response().redirect("/elepy-initial-user");
                halt();
            };


            this.links = new ArrayList<>();

            this.engine = new PebbleEngine.Builder().build();

            setupLogin(http);

            setupInitialUser(http);
            setupAdminFilters(http, elepy);

            this.initiated = true;
            this.modelContexts = elepy.getModelDescriptions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public HttpContextHandler filter() {

        return ctx -> {

            try {
                ctx.loggedInUserOrThrow();
            } catch (ElepyException e) {
                ctx.redirect("/elepy-login");
            }
        };
    }

    private void setupAdminFilters(HttpService http, ElepyPostConfiguration elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {


        http.before("/admin/*/*", filter());
        http.before("/admin/*", filter());
        http.before("/admin", filter());

        http.get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("plugins", pluginHandler.getPlugins());
            response.result(renderWithDefaults(request, model, "admin-templates/base.peb"));
        });
        http.get("/admin-logout", (request, response) -> {
            response.removeCookie("ELEPY_TOKEN");
            request.session().invalidate();
            response.redirect("/elepy-login");
        });
        this.viewHandler.setupModels(elepy);
        this.pluginHandler.setupPlugins(elepy);
        this.viewHandler.initializeRoutes(elepy);
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
        model.put("descriptors", modelContexts);
        model.put("plugins", pluginHandler.getPlugins());
        model.put("links", links);
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


    public ElepyAdminPanel addLink(Link link) {
        links.add(link);
        return this;
    }

    public ElepyAdminPanel addLink(String to) {
        return addLink(new Link(to, to));
    }

    public ElepyAdminPanel addLink(String to, String text) {
        return addLink(new Link(to, text));
    }

    public ElepyAdminPanel addLink(String to, String text, String fontAwesomeClass) {
        return addLink(new Link(to, text, fontAwesomeClass));
    }

    public ElepyAdminPanel onNoUserFound(NoUserFoundHandler noUserFoundHandler) {
        this.noUserFoundHandler = noUserFoundHandler;
        return this;
    }

    public boolean isInitiated() {
        return initiated;
    }

}

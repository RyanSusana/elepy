package com.elepy.admin;

import com.elepy.ElepyModule;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.admin.concepts.*;
import com.elepy.admin.models.Attachment;
import com.elepy.admin.models.AttachmentType;
import com.elepy.admin.models.Link;
import com.elepy.annotations.Inject;
import com.elepy.auth.User;
import com.elepy.auth.UserAuthenticationCenter;
import com.elepy.dao.Crud;
import com.elepy.describers.Model;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContextHandler;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.mitchellbosecke.pebble.PebbleEngine;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;


public class ElepyAdminPanel implements ElepyModule {
    public static final String ADMIN_USER = "adminUser";
    private final AttachmentHandler attachmentHandler;
    private final PluginHandler pluginHandler;
    private final ViewHandler viewHandler;
    private final List<Link> links;
    private boolean initiated = false;

    @Inject
    private Crud<User> userCrud;

    private HttpService http;
    private PebbleEngine engine;

    private List<Model<?>> modelContexts;


    private NoUserFoundHandler noUserFoundHandler;


    @Inject
    private UserAuthenticationCenter userAuthenticationCenter;

    public ElepyAdminPanel() {


        this.attachmentHandler = new AttachmentHandler(this);
        this.pluginHandler = new PluginHandler(this);


        this.viewHandler = new ViewHandler(this);


        this.noUserFoundHandler = (ctx) -> {
            ctx.response().redirect("/elepy-initial-user");
            halt();
        };


        this.links = new ArrayList<>();

        this.engine = new PebbleEngine.Builder().build();
    }


    @Override
    public void afterElepyConstruction(HttpService http, ElepyPostConfiguration elepy) {
        try {
            attachSrcDirectory(this.getClass().getClassLoader(), "admin-resources");
            setupLogin();
            setupAdmin(elepy);

            this.attachmentHandler.setupAttachments(elepy);
            this.initiated = true;
            this.modelContexts = elepy.getModelDescriptions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void beforeElepyConstruction(HttpService http, ElepyPreConfiguration elepy) {

        this.http = http;

    }


    public HttpContextHandler filter() {

        return ctx -> {

            try {
                userAuthenticationCenter.tryToLogin(ctx.request());
            } catch (ElepyException e) {

                ctx.redirect("/elepy-login");
            }
        };
    }

    private void setupAdmin(ElepyPostConfiguration elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {


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


    private void setupLogin() {


        http.get("/elepy-login", (request, response) -> response.result(renderWithDefaults(request, new HashMap<>(), "admin-templates/login.peb")));

        http.before("/elepy-login", ctx -> {
            if (userCrud.count() <= 0) {
                noUserFoundHandler.handle(ctx);
            }
        });


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
        model.put("user", request.attribute(ADMIN_USER));
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


    public ElepyAdminPanel attachSrc(Attachment attachment) {
        attachmentHandler.attachSrc(attachment);
        return this;
    }

    public ElepyAdminPanel attachSrc(String fileName, String contentType, byte[] src, AttachmentType type, boolean isFromDirectory, String directory) {
        attachmentHandler.attachSrc(fileName, contentType, src, type, isFromDirectory, directory);
        return this;
    }

    public ElepyAdminPanel attachSrc(ClassLoader classLoader, String file, boolean isFromDirectory, String directory) throws IOException {
        attachmentHandler.attachSrc(classLoader, file, isFromDirectory, directory);
        return this;
    }

    public ElepyAdminPanel attachSrc(String fileName, InputStream inputStream, boolean isFromDirectory, String directory) throws IOException {
        attachmentHandler.attachSrc(fileName, inputStream, isFromDirectory, directory);
        return this;
    }

    public ElepyAdminPanel attachSrc(File file, boolean isFromDirectory, String directory) throws IOException {
        attachmentHandler.attachSrc(file, isFromDirectory, directory);
        return this;
    }

    public ElepyAdminPanel attachSrcDirectory(ClassLoader classLoader, String directory) throws IOException {
        attachmentHandler.attachSrcDirectory(classLoader, directory);
        return this;
    }


    public ElepyAdminPanel onFirstTime(SetupHandler setupHandler) {
        return this;
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

    public HttpService http() {
        return http;
    }


    public boolean isInitiated() {
        return initiated;
    }


    public AttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }
}

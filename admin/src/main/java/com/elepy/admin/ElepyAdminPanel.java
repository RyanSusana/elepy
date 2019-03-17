package com.elepy.admin;

import com.elepy.ElepyModule;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.admin.concepts.*;
import com.elepy.admin.concepts.auth.Authenticator;
import com.elepy.admin.concepts.auth.BasicHandler;
import com.elepy.admin.concepts.auth.TokenHandler;
import com.elepy.admin.models.*;
import com.elepy.admin.services.UserService;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Filter;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.mitchellbosecke.pebble.PebbleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;


public class ElepyAdminPanel implements ElepyModule {
    public static final String ADMIN_USER = "adminUser";
    private static final Logger logger = LoggerFactory.getLogger(ElepyAdminPanel.class);
    private final AttachmentHandler attachmentHandler;
    private final PluginHandler pluginHandler;
    private final ViewHandler viewHandler;
    private final List<Link> links;
    private TokenHandler tokenHandler;
    private Filter baseAdminAuthenticationFilter;
    private SetupHandler setupHandler;
    private Authenticator authenticator;
    private UserService userService;
    private boolean initiated = false;
    private Crud<? extends UserInterface> userCrud;

    private HttpService http;
    private PebbleEngine engine;

    private List<ModelDescription<?>> modelDescriptions;


    private Class<? extends UserInterface> userClass;

    private NoUserFoundHandler noUserFoundHandler;

    public ElepyAdminPanel() {

        this.userClass = User.class;

        this.attachmentHandler = new AttachmentHandler(this);
        this.pluginHandler = new PluginHandler(this);


        this.authenticator = new Authenticator();
        this.viewHandler = new ViewHandler(this);


        this.noUserFoundHandler = (ctx, crud) -> {
            ctx.response().redirect("/elepy-initial-user");
            halt();
        };
        this.baseAdminAuthenticationFilter = context -> {
            if (userCrud.count() == 0) {
                noUserFoundHandler.handle(context, userCrud);
            }
            final UserInterface user = authenticator.authenticate(context.request());

            if (user != null) {
                context.request().attribute(ADMIN_USER, user);
            } else {

                context.request().session().attribute("redirectUrl", context.request().uri());
                context.response().redirect("/elepy-login", 301);
                halt();

            }

        };

        this.setupHandler = elepy -> {
        };

        this.links = new ArrayList<>();

        this.engine = new PebbleEngine.Builder().build();
    }


    @Override
    public void afterElepyConstruction(HttpService http, ElepyPostConfiguration elepy) {
        new AdminSetupEvaluation(this).evaluate();
        try {
            this.userCrud = elepy.getCrudFor(userClass);
            this.userService = new UserService(userCrud);
            this.tokenHandler = new TokenHandler(this.userService);
            this.authenticator.addAuthenticationMethod(tokenHandler).addAuthenticationMethod(new BasicHandler(this.userService));


            attachSrcDirectory(this.getClass().getClassLoader(), "admin-resources");
            setupLogin();
            setupAdmin(elepy);

            this.attachmentHandler.setupAttachments(elepy);
            this.initiated = true;
            this.modelDescriptions = elepy.getModelDescriptions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void beforeElepyConstruction(HttpService http, ElepyPreConfiguration elepy) {

        this.http = http;


        elepy.addAdminFilter(this.baseAdminAuthenticationFilter);
        elepy.addModel(this.userClass);
    }


    private void setupAdmin(ElepyPostConfiguration elepy) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {


        http.before("/admin/*/*", ctx -> elepy.getAllAdminFilters().authenticate(ctx));
        http.before("/admin/*", ctx -> elepy.getAllAdminFilters().authenticate(ctx));
        http.before("/admin", ctx -> elepy.getAllAdminFilters().authenticate(ctx));
        http.post("/retrieve-token", (request, response) -> {
            final Token token = tokenHandler.createToken(request);
            response.result(elepy.getObjectMapper().writeValueAsString(token));
        });


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

        http.before("/elepy-login", (request, response) -> {
            if (userCrud.count() <= 0) {
                response.redirect("/elepy-initial-user", 301);
                halt();
            }
        });

        http.post("/elepy-login", (request, response) -> {


            boolean keepLoggedIn = Boolean.parseBoolean(request.queryParamOrDefault("keepLoggedIn", "false"));

            int durationInSeconds = keepLoggedIn ? Integer.MAX_VALUE : 60 * 60;

            Token token = tokenHandler.createToken(request.queryParamOrDefault("username", "invalid"),
                    request.queryParamOrDefault("password", "invalid"), durationInSeconds * 1000L);
            final String redirectUrl = request.session().attribute("redirectUrl") == null ? "/admin" : request.session().attribute("redirectUrl");

            response.status(200);
            request.session().removeAttribute("redirectUrl");

            response.cookie("ELEPY_TOKEN", token.getId(), durationInSeconds);
            response.result(redirectUrl);

        });

        http.get("/elepy-login-check", ctx -> {
            final UserInterface user = authenticator.authenticate(ctx.request());

            if (user == null) {
                throw new ElepyException("You are not logged in.", 401);
            }
            throw new ElepyException("Your are logged in", 200);

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
        model.put("descriptors", modelDescriptions);
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

    public ElepyAdminPanel userClass(Class<? extends UserInterface> userClass) {
        this.userClass = userClass;
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
        this.setupHandler = setupHandler;
        return this;
    }

    public ElepyAdminPanel addLink(Link link) {
        links.add(link);
        return this;
    }

    public ElepyAdminPanel baseAdminFilter(Filter filter) {
        this.baseAdminAuthenticationFilter = filter;
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

    Class<? extends UserInterface> getUserClass() {
        return userClass;
    }

    public AttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }
}

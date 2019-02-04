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
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.ErrorMessageBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Service;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    private Service http;
    private PebbleEngine engine;


    private Class<?> userClass;

    public ElepyAdminPanel() {

        this.userClass = User.class;

        this.attachmentHandler = new AttachmentHandler(this);
        this.pluginHandler = new PluginHandler(this);


        this.authenticator = new Authenticator();
        this.viewHandler = new ViewHandler(this);


        this.baseAdminAuthenticationFilter = (request, response) -> {

            final User user = authenticator.authenticate(request);

            if (user != null) {
                request.attribute(ADMIN_USER, user);
                request.session().attribute(ADMIN_USER, user);
            } else {
                final User adminUser = request.session().attribute(ADMIN_USER);
                if (adminUser == null) {
                    request.session().attribute("redirectUrl", request.uri());
                    response.redirect("/elepy-login");
                    halt();
                }
            }

        };

        this.setupHandler = elepy -> {
        };

        this.links = new ArrayList<>();

        engine = new PebbleEngine.Builder().build();
    }


    @Override
    public void afterElepyConstruction(Service http, ElepyPostConfiguration elepy) {

        try {
            this.userService = new UserService(elepy.getCrudFor(User.class));
            tokenHandler = new TokenHandler(this.userService);
            authenticator.addAuthenticationMethod(tokenHandler).addAuthenticationMethod(new BasicHandler(this.userService));


            attachSrcDirectory(this.getClass().getClassLoader(), "admin-resources");
            setupLogin();
            setupAdmin(elepy);

            attachmentHandler.setupAttachments(elepy);
            initiated = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void beforeElepyConstruction(Service http, ElepyPreConfiguration elepy) {

        this.http = http;


        elepy.addAdminFilter(baseAdminAuthenticationFilter);
        elepy.addModel(this.userClass);
    }


    private void setupAdmin(ElepyPostConfiguration elepy) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {


        http.before("/admin/*/*", (request, response) -> elepy.getAllAdminFilters().handle(request, response));
        http.before("/admin/*", (request, response) -> elepy.getAllAdminFilters().handle(request, response));
        http.before("/admin", (request, response) -> elepy.getAllAdminFilters().handle(request, response));
        http.post("/retrieve-token", (request, response) -> {
            final Optional<Token> token = tokenHandler.createToken(request);

            if (token.isPresent()) {
                return elepy.getObjectMapper().writeValueAsString(token.get());
            } else {
                throw new ElepyException("Invalid username/password");
            }
        });
        http.get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("plugins", pluginHandler.getPlugins());
            return renderWithDefaults(request, model, "admin-templates/base.peb");
        });
        http.get("/admin-logout", (request, response) -> {

            request.session().invalidate();
            response.redirect("/elepy-login");

            return "";
        });
        viewHandler.setup(elepy);
        pluginHandler.setupPlugins(elepy);
        viewHandler.routes(elepy);
        pluginHandler.setupRoutes(elepy);
    }


    private void setupLogin() {


        http.get("/elepy-login", (request, response) -> renderWithDefaults(request, new HashMap<>(), "admin-templates/login.peb"));
        http.post("/elepy-login", (request, response) -> {

            final Optional<User> user = userService.login(request.queryParamOrDefault("username", "invalid"), request.queryParamOrDefault("password", "invalid"));

            final String redirectUrl = request.session().attribute("redirectUrl") == null ? "/admin" : request.session().attribute("redirectUrl");


            if (user.isPresent()) {
                if (user.get().getUserType().getLevel() < 0) {
                    throw new ElepyException("Your account has been suspended!");
                }
                request.session().attribute(ADMIN_USER, user.get());
                response.status(200);
                request.session().removeAttribute("redirectUrl");
                return redirectUrl;
            }


            response.status(401);
            throw ErrorMessageBuilder.anElepyErrorMessage().withMessage("Invalid login credentials").withStatus(401).build();
        });


    }


    public String renderWithDefaults(Request request, Map<String, Object> model, String templatePath) throws IOException {
        model.put("descriptors", viewHandler.getDescriptors());
        model.put("plugins", pluginHandler.getPlugins());
        model.put("user", request.session().attribute(ADMIN_USER));
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

    public ElepyAdminPanel userClass(Class<?> userClass) {
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

    public Service http() {
        return http;
    }


    public boolean isInitiated() {
        return initiated;
    }

    public AttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }
}

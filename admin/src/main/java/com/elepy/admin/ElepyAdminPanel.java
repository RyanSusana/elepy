package com.elepy.admin;

import com.elepy.ElepyModule;
import com.elepy.admin.concepts.AttachmentHandler;
import com.elepy.admin.concepts.ElepyAdminPanelPlugin;
import com.elepy.admin.concepts.PluginHandler;
import com.elepy.admin.concepts.ViewHandler;
import com.elepy.admin.dao.UserDao;
import com.elepy.admin.models.*;
import com.elepy.admin.services.BCrypt;
import com.elepy.admin.services.UserService;
import com.elepy.exceptions.RestErrorMessage;
import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.pebble.PebbleTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;


public class ElepyAdminPanel extends ElepyModule {
    public static final String ADMIN_USER = "adminUser";
    private static final Logger LOGGER = LoggerFactory.getLogger(ElepyAdminPanel.class);
    private final AttachmentHandler attachmentHandler;
    private final PluginHandler pluginHandler;
    private final ViewHandler viewHandler;
    private final List<Link> links;
    private UserDao userDao;
    private UserService userService;
    private boolean initiated = false;


    public ElepyAdminPanel() {

        this.attachmentHandler = new AttachmentHandler(this);
        this.pluginHandler = new PluginHandler(this);


        this.viewHandler = new ViewHandler(this);


        this.links = new ArrayList<>();
    }


    @Override
    public void routes() {

        try {

            attachSrcDirectory(this.getClass().getClassLoader(), "admin-resources");
            setupLogin();
            setupAdmin();

            attachmentHandler.setupAttachments();
            checkSetup();
            initiated = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setup() {

            this.userDao = new UserDao(elepy().getSingleton(DB.class));
            this.userService = new UserService(userDao);



            elepy().addPackage(User.class.getPackage().getName());
    }



    private void setupAdmin() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {


        http().before("/admin/*/*", (request, response) -> elepy().allAdminFilters().handle(request, response));
        http().before("/admin/*", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().before("/admin", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("plugins", pluginHandler.getPlugins());
            return renderWithDefaults(request, model, "templates/base.peb");
        });
        http().get("/admin-logout", (request, response) -> {

            request.session().invalidate();
            response.redirect("/login");

            return "";
        });
        viewHandler.setup();
        pluginHandler.setupPlugins();
        viewHandler.routes();
        pluginHandler.setupRoutes();
    }



    private void setupLogin() {


        elepy().addAdminFilter((request, response) -> {
            final User adminUser = request.session().attribute(ADMIN_USER);
            if (adminUser == null) {
                request.session().attribute("redirectUrl", request.uri());
                response.redirect("/login");
            }
        });
        http().get("/login", (request, response) -> {


            return renderWithDefaults(request, new HashMap<>(), "templates/login.peb");
        });
        http().post("/login", (request, response) -> {

            final Optional<User> user = userService.login(request.queryParamOrDefault("username", "invalid"), request.queryParamOrDefault("password", "invalid"));

            final String redirectUrl = request.session().attribute("redirectUrl") == null ? "/admin" : request.session().attribute("redirectUrl");


            if (user.isPresent()) {
                if (user.get().getUserType().getLevel() < 0) {
                    throw new RestErrorMessage("Your account has been suspended!");
                }
                request.session().attribute(ADMIN_USER, user.get());
                response.status(200);
                request.session().removeAttribute("redirectUrl");
                return redirectUrl;
            }


            response.status(401);
            return "Invalid login credentials";
        });
        http().get("/setup", (request, response) -> {

            if (userDao.count() == 0) {
                User user = new User(null, "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "", UserType.SUPER_ADMIN);
                userDao.create(user);
                return "Generated first admin account";
            }
            response.redirect("/admin");
            return "templates/base.peb";
        });


    }

    private void checkSetup() {
        if (userDao.count() == 0) {
            User user = new User(null, "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "", UserType.SUPER_ADMIN);
            userDao.create(user);
        }
    }

    public String renderWithDefaults(Request request, Map<String, Object> model, String templatePath) {
        model.put("descriptors", viewHandler.getDescriptors());
        model.put("plugins", pluginHandler.getPlugins());
        model.put("user", request.session().attribute(ADMIN_USER));
        model.put("links", links);
        return render(model, templatePath);
    }


    public String render(Map<String, Object> model, String templatePath) {

        return new PebbleTemplateEngine().render(new ModelAndView(model, templatePath));
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

    public ElepyAdminPanel attachSrcDirectory(ClassLoader classLoader, String directory) throws IOException, URISyntaxException {
        attachmentHandler.attachSrcDirectory(classLoader, directory);
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

    public boolean isInitiated() {
        return initiated;
    }

    public AttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }
}

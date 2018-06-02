package com.elepy.admin;

import com.elepy.Elepy;
import com.elepy.ElepyModule;
import com.elepy.admin.annotations.View;
import com.elepy.admin.concepts.ResourceView;
import com.elepy.admin.dao.UserDao;
import com.elepy.admin.models.Attachment;
import com.elepy.admin.models.AttachmentType;
import com.elepy.admin.models.User;
import com.elepy.admin.models.UserType;
import com.elepy.admin.services.BCrypt;
import com.elepy.admin.services.UserService;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.utils.ClassUtils;
import com.mongodb.DB;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Service;
import spark.template.pebble.PebbleTemplateEngine;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ElepyAdminPanel extends ElepyModule {
    public static final String ADMIN_USER = "adminUser";
    private static final Logger LOGGER = LoggerFactory.getLogger(ElepyAdminPanel.class);
    private final Set<ElepyAdminPanelPlugin> plugins;
    private final Set<Attachment> attachments;
    private UserDao userDao;
    private UserService userService;
    private boolean initiated = false;


    public ElepyAdminPanel(Elepy elepy, Service service) {
        super(elepy, service);
        this.userDao = new UserDao(elepy.getDb(), elepy.getMapper());
        this.userService = new UserService(userDao);
        this.plugins = new TreeSet<>();
        this.attachments = new TreeSet<>();

    }

    public ElepyAdminPanel(Elepy elepy) {
        super(elepy);
        this.userDao = new UserDao(elepy.getSingleton(DB.class), elepy.getMapper());
        this.userService = new UserService(userDao);
        this.plugins = new TreeSet<>();
        this.attachments = new TreeSet<>();
    }

    public ElepyAdminPanel() {
        this.plugins = new TreeSet<>();
        this.attachments = new TreeSet<>();
    }


    @Override
    public void routes() {

        try {
            attachSrcDirectory(this.getClass().getClassLoader(), "admin-resources");
            setupLogin();
            setupAdmin();
            setupAttachments();
            initiated = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    @Override
    public void setup() {

        this.userDao = new UserDao(elepy().getSingleton(DB.class), elepy().getMapper());
        this.userService = new UserService(userDao);
        elepy().addPackage(User.class.getPackage().getName());

    }

    public void setupAttachments() {
        for (Attachment attachment : attachments) {
            http().get(attachment.getDirectory() + (attachment.isFromDirectory() ? "" : attachment.getType().getRoute()) + attachment.getFileName(), (request, response) -> {
                response.type(attachment.getContentType());
                HttpServletResponse raw = response.raw();

                raw.getOutputStream().write(attachment.getSrc());
                raw.getOutputStream().flush();
                raw.getOutputStream().close();

                response.raw().getOutputStream();
                return response.raw();
            });
        }
    }

    private void defaultDecriptorPanel(Map<String, Object> descriptor, List<Map<String, Object>> descriptors) {
        http().get("/admin" + descriptor.get("slug"), (request, response) -> {

            Map<String, Object> model = new HashMap<>();

            model.put("descriptors", descriptors);
            model.put("plugins", plugins);

            model.put("currentDescriptor", descriptor);
            return render(model, "templates/model.peb");
        });
    }

    private void setupAdmin() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Map<String, Object>> descriptors = new ArrayList<>();
        for (Object o : elepy().getDescriptors()) {
            if (o instanceof Map) {
                descriptors.add((Map<String, Object>) o);
            }
        }
        for (Map<String, Object> descriptor : descriptors) {
            http().get("/admin/config" + descriptor.get("slug"), (request, response) -> {
                response.type("application/json");
                return elepy().getObjectMapper().writeValueAsString(
                        descriptor
                );
            });

            if (descriptor.containsKey("javaClass")) {
                final Class<?> javaClass = Class.forName((String) descriptor.get("javaClass"));

                if (javaClass.isAnnotationPresent(View.class)) {

                    final View annotation = javaClass.getAnnotation(View.class);

                    final Optional<Constructor<?>> emptyConstructor = ClassUtils.getEmptyConstructor(annotation.value());

                    if (!emptyConstructor.isPresent()) {
                        throw new IllegalAccessException("Resource View Class must contain an empty constructor");
                    }
                    final ResourceView resourceView = (ResourceView) emptyConstructor.get().newInstance();
                    resourceView.setup(this);
                    http().get("/admin" + descriptor.get("slug"), (request, response) -> {

                        Map<String, Object> model = new HashMap<>();

                        model.put("customView", resourceView.renderView(descriptor));
                        model.put("customHeaders", resourceView.renderExtraHeaders());
                        model.put("descriptors", descriptors);
                        model.put("plugins", plugins);

                        model.put("currentDescriptor", descriptor);
                        return render(model, "templates/model.peb");
                    });
                } else {
                    defaultDecriptorPanel(descriptor, descriptors);
                }
            } else {
                defaultDecriptorPanel(descriptor, descriptors);
            }


        }


        http().before("/admin/*/*", (request, response) -> elepy().allAdminFilters().handle(request, response));
        http().before("/plugins/*", (request, response) -> elepy().allAdminFilters().handle(request, response));
        http().before("/plugins/*/*", (request, response) -> elepy().allAdminFilters().handle(request, response));
        http().before("/admin/*", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().before("/admin", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("descriptors", descriptors);
            model.put("plugins", plugins);
            return render(model, "templates/base.peb");
        });
        http().get("/admin-logout", (request, response) -> {

            request.session().invalidate();
            response.redirect("/login");

            return "";
        });
        for (ElepyAdminPanelPlugin plugin : this.plugins) {
            plugin.setup(http(), elepy().getDb(), elepy().getObjectMapper());
            http().get("/plugins/" + plugin.getSlug(), (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                String content = plugin.renderContent(null);
                model.put("descriptors", descriptors);
                model.put("content", content);
                model.put("plugin", plugin);
                model.put("plugins", plugins);
                return render(model, "templates/plugin.peb");
            });
        }
    }

    private Map<String, Object> getDescriptor(String slug, List<Map<String, Object>> descriptors) {
        for (Map<String, Object> descriptor : descriptors) {
            if (descriptor.get("slug").equals(slug)) {
                return descriptor;
            }
        }
        return null;
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


            return render(new HashMap<>(), "templates/login.peb");
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

    private String render(Map<String, Object> model, String templatePath) {
        return new PebbleTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public ElepyAdminPanel addPlugin(ElepyAdminPanelPlugin plugin) {
        if (initiated) {
            throw new IllegalStateException("Can't add plugins after setup() has been called!");
        }
        plugin.setAdminPanel(this);
        this.plugins.add(plugin);
        return this;
    }


    public void attachSrc(Attachment attachment) {
        if (initiated) {
            throw new IllegalStateException("Can't attach after setup() has been called!");
        }
        attachments.add(attachment);
    }

    public void attachSrc(String fileName, String contentType, byte[] src, AttachmentType type, boolean isFromDirectory, String directory) {
        attachSrc(new Attachment(fileName, contentType, src, type, isFromDirectory, directory));
    }

    public void attachSrc(ClassLoader classLoader, String file, boolean isFromDirectory, String directory) throws IOException {
        attachSrc(file, classLoader.getResourceAsStream(file), isFromDirectory, directory);
    }

    public void attachSrc(String fileName, InputStream inputStream, boolean isFromDirectory, String directory) throws IOException {
        final String[] fileNameParts = fileName.split("\\.");


        final String extension = fileNameParts[fileNameParts.length - 1];
        final Tika tika = new Tika();

        final byte[] bytes = IOUtils.toByteArray(inputStream);

        final String contentType = tika.detect(inputStream, fileName);


        attachSrc(fileName, contentType, bytes, AttachmentType.guessTypeFromMime(contentType), isFromDirectory, directory);
    }

    public void attachSrc(File file, boolean isFromDirectory, String directory) throws IOException {
        final String[] fileNameParts = file.getName().split("\\.");


        final String extension = fileNameParts[fileNameParts.length - 1];
        final byte[] bytes = FileUtils.readFileToByteArray(file);
        final Tika tika = new Tika();
        final String contentType = tika.detect(new FileInputStream(file), file.getName());


        attachSrc(file.getName(), contentType, bytes, AttachmentType.guessTypeFromMime(contentType), isFromDirectory, directory);

    }

    public void attachSrcDirectory(ClassLoader classLoader, String directory) throws IOException, URISyntaxException {
        Enumeration<URL> en = classLoader.getResources(
                directory);
        List<String> profiles = new ArrayList<>();

        if (en.hasMoreElements()) {
            URL url = en.nextElement();
            try {
                JarURLConnection urlcon = (JarURLConnection) (url.openConnection());

                try (JarFile jar = urlcon.getJarFile();) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(directory) && !entry.isDirectory()) {
                            attachSrc(classLoader, entry.getName(), true, "");
                        }
                    }
                }
            } catch (ClassCastException e) {
                final URL resource = classLoader.getResource(directory);

                if (resource == null) {
                    throw new FileNotFoundException("Resource doen't exist: " + directory);
                }

                for (File file : FileUtils.listFiles(new File(resource.getFile()), null, true)) {
                    if (!file.isDirectory()) {

                        String pre = StringUtils.cleanPath(file.getPath()).split(directory)[1];

                        attachSrc(file, true, StringUtils.cleanPath(directory + (directory.endsWith("/") ? "" : "/") + (pre.replaceAll(file.getName(), ""))));
                    }
                }


            }
        }
    }
}

package com.ryansusana.elepy.admin;

import com.ryansusana.elepy.Elepy;
import com.ryansusana.elepy.ElepyModule;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.context.FileContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import spark.ModelAndView;
import spark.Service;
import spark.template.pebble.PebbleTemplateEngine;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ElepyAdminPanel extends ElepyModule {
    private final UserDao userDao;
    private final UserService userService;


    public ElepyAdminPanel(Elepy elepy, Service service) {
        super(elepy, service);
        this.userDao = new UserDao(elepy.getDb(), elepy.getMapper());
        this.userService = new UserService(userDao);
    }

    public ElepyAdminPanel(Elepy elepy) {
        super(elepy);
        this.userDao = new UserDao(elepy.getDb(), elepy.getMapper());
        this.userService = new UserService(userDao);


    }


    @Override
    public void routes() {
        compileSass();
        setupLogin();
        setupAdmin();
    }

    public void copyResourcesRecursively(URL originUrl, File destination) throws Exception {
        URLConnection urlConnection = originUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
        } else if (urlConnection instanceof FileURLConnection) {
            FileUtils.copyDirectory(new File(originUrl.getPath()), destination);
        } else {
            throw new Exception("URLConnection[" + urlConnection.getClass().getSimpleName() +
                    "] is not a recognized/implemented connection type.");
        }
    }

    public void copyJarResourcesRecursively(File destination, JarURLConnection jarConnection) throws IOException {
        JarFile jarFile = jarConnection.getJarFile();

        for (JarEntry entry : Collections.list(jarFile.entries())) {
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
                if (!entry.isDirectory()) {
                    InputStream entryInputStream = null;
                    entryInputStream = jarFile.getInputStream(entry);

                    FileUtils.copyInputStreamToFile(entryInputStream, new File(destination, fileName));
                }
            }
        }
    }

    private void compileSass() {


        new Thread(() -> {
            try {
                String fs = System.getProperty("file.separator");
                File outputFile = new File(fs + "elepy-admin" + fs + "main.css");
                if (!outputFile.exists()) {
                    outputFile.getParentFile().mkdirs();
                    outputFile.createNewFile();
                }

                File sassLocation = new File(fs + "elepy-admin" + fs + "scss");
                copyResourcesRecursively(this.getClass().getClassLoader().getResource("admin-public/scss"), sassLocation);
                URI inputFile = new File(fs + "elepy-admin" + fs + "scss" + fs + "main.scss").toURI();


                Compiler compiler = new Compiler();

                Options options = new Options();


                FileContext context = new FileContext(inputFile, outputFile.toURI(), options);
                Output output = compiler.compile(context);


                System.out.println("Compiled css");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }


    @Override
    public void setup() {
        http().staticFiles.location("/admin-public");

        elepy().addPackage(User.class.getPackage().getName());

    }

    private void setupAdmin() {
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
            http().get("/admin" + descriptor.get("slug"), (request, response) -> {

                Map<String, Object> model = new HashMap<>();

                model.put("descriptors", descriptors);

                model.put("currentDescriptor", descriptor);
                return render(model, "templates/model.peb");
            });

        }


        http().before("/admin/*/*", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().before("/admin/*", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().before("/admin", (request, response) -> {
            elepy().allAdminFilters().handle(request, response);
        });
        http().get("/admin", (request, response) -> {

            Map<String, Object> model = new HashMap<>();
            model.put("descriptors", descriptors);

            return render(model, "templates/base.peb");
        });
        http().get("/admin-logout", (request, response) -> {

            request.session().invalidate();
            response.redirect("/login");

            return "";
        });


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

        if (userDao.count() == 0) {
            User user = new User(null, "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "");
            userDao.create(user);
        }

        elepy().addAdminFilter((request, response) -> {
            final User adminUser = request.session().attribute("adminUser");
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
                request.session().attribute("adminUser", user.get());
                response.status(200);
                request.session().removeAttribute("redirectUrl");
                return redirectUrl;
            }

            response.status(401);
            return "Invalid login credentials";
        });
    }

    private String render(Map<String, Object> model, String templatePath) {
        return new PebbleTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}

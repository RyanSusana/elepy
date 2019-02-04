package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.Attachment;
import com.elepy.admin.models.AttachmentType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AttachmentHandler {
    private final List<Attachment> attachments = new ArrayList<>();

    private final ElepyAdminPanel adminPanel;


    public AttachmentHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }


    public void setupAttachments(ElepyPostConfiguration elepyPostConfiguration) {
        for (Attachment attachment : attachments) {
            adminPanel.http().get(elepyPostConfiguration.getBaseSlug() + attachment.getDirectory() + (attachment.isFromDirectory() ? "" : attachment.getType().getRoute()) + attachment.getFileName(), (request, response) -> {
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

    public void attachSrc(Attachment attachment) {

        if (adminPanel.isInitiated()) {
            throw new IllegalStateException("Can't attach a source after beforeElepyConstruction() has been called.");
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


        final Tika tika = new Tika();

        final byte[] bytes = IOUtils.toByteArray(inputStream);

        final String contentType = tika.detect(inputStream, fileName);


        attachSrc(fileName, contentType, bytes, AttachmentType.guessTypeFromMime(contentType), isFromDirectory, directory);
    }

    public void attachSrc(File file, boolean isFromDirectory, String directory) throws IOException {


        final byte[] bytes = FileUtils.readFileToByteArray(file);
        final Tika tika = new Tika();
        final String contentType = tika.detect(new FileInputStream(file), file.getName());


        attachSrc(file.getName(), contentType, bytes, AttachmentType.guessTypeFromMime(contentType), isFromDirectory, directory);

    }

    public void attachSrcDirectory(ClassLoader classLoader, String directory) throws IOException {
        Enumeration<URL> en = classLoader.getResources(
                directory);

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

    public List<Attachment> getAttachments() {
        return attachments;
    }
}

package com.elepy.uploads;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryFileService implements FileService {

    private final String rootFolderLocation;

    private final Tika tika = new Tika();

    public DirectoryFileService(String rootFolderLocation) {
        this.rootFolderLocation = rootFolderLocation;
        ensureRootFolderExists();
    }

    private static String replaceNMatches(String input, String regex,
                                          String replacement, int numberOfTimes) {

        final var quoteReplacement = Matcher.quoteReplacement(replacement);
        Matcher m = Pattern.compile(regex).matcher(input);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i++ < numberOfTimes && m.find()) {
            m.appendReplacement(sb, quoteReplacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    void ensureRootFolderExists() {
        Path path = Paths.get(rootFolderLocation);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ElepyConfigException("Can't create upload folder");
        }
    }

    @Override
    public synchronized void uploadFile(FileUpload file) {
        final Path path = Paths.get(rootFolderLocation + File.separator + decodeFileName(file.getName()));
        try {
            Files.createDirectories(path.getParent() == null ? path : path.getParent());
            Files.copy(file.getContent(), path);
        } catch (FileAlreadyExistsException e) {
            throw new ElepyException("FileReference Already Exists: " + file.getName(), 409);
        } catch (IOException e) {
            throw new ElepyException("Failed to upload file: " + file.getName(), 500, e);
        }
    }

    @Override
    public synchronized Optional<FileUpload> readFile(String name) {
        final Path path = Paths.get(rootFolderLocation + File.separator + decodeFileName(name));
        try {
            final FileUpload fileUpload = FileUpload.of(name, tika.detect(path), Files.newInputStream(path), Files.size(path));

            return Optional.of(fileUpload);
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new ElepyException("Failed at retrieving file: " + name, 500);
        }
    }

    @Override
    public List<String> listFiles() {
        final Path path = Paths.get(rootFolderLocation);
        try (Stream<Path> walk = Files.walk(path)) {
            return walk
                    .filter(path1 -> !Files.isDirectory(path1))
                    .map(Path::toString)
                    .map(filePath -> filePath.substring(path.toString().length() + 1))
                    .map(this::encodeFileName)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new ElepyException("Failed to list all files on Server", 500, e);
        }
    }

    @Override
    public void deleteFile(String encodedFileName) {
        final Path path = Paths.get(rootFolderLocation + File.separator + decodeFileName(encodedFileName));

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ElepyException("Failed to delete file: " + encodedFileName, 500);
        }
    }

    private String decodeFileName(String encodedFileName) {
        return replaceNMatches(encodedFileName, "_", File.separator, 2);
    }

    private String encodeFileName(String decodedFileName) {
        return decodedFileName.replaceAll("/", "_");
    }
}

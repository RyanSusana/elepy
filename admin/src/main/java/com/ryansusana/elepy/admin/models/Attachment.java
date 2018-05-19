package com.ryansusana.elepy.admin.models;

public class Attachment implements Comparable<Attachment> {
    private final String fileName;
    private final String contentType;
    private final byte[] src;

    private final AttachmentType type;


    public Attachment(String fileName, String contentType, byte[] src, AttachmentType type) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.src = src;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getSrc() {
        return src;
    }

    public AttachmentType getType() {
        return type;
    }

    @Override
    public int compareTo(Attachment o) {
        return this.getFileName().compareTo(o.getFileName());
    }
}

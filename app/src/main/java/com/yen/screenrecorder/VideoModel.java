package com.yen.screenrecorder;

import java.io.File;

public class VideoModel {
    private String filename;
    private String path;
    private long sizeBytes;
    private long lastModified;
    private long durationMs;

    public VideoModel(String filename, String path, long sizeBytes, long lastModified, long durationMs) {
        this.filename = filename;
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
        this.durationMs = durationMs;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public File getFile() {
        return new File(path);
    }
}

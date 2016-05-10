package com.mike.file.system;

import java.util.Map;

/**
 * Created by michaelbrennan on 5/4/16.
 */
public abstract class MetaFile {

    String name;
    String path;
    private double size;
    Map<String, TextFile> textFiles;
    Map<String, ZipFile> zipFiles;
    Map<String, Folder> folders;


    abstract String getName();

    abstract void setName(String name);

    abstract String getPath();

    abstract void setPath(String path);

    public void addTextFile(String name, TextFile t) {
        textFiles.put(name,  t);
    }

    public void addZipFile(String name, ZipFile z) {
        zipFiles.put(name, z);
    }

    public void addFolder(String name, Folder f) {
        folders.put(name,  f);
    }
    public Map<String, TextFile> getTextFiles() {
        return textFiles;
    }

    public void setTextFiles(Map<String, TextFile> textFiles) {
        this.textFiles = textFiles;
    }

    public Map<String, ZipFile> getZipFiles() {
        return zipFiles;
    }

    public void setZipFiles(Map<String, ZipFile> zipFiles) {
        this.zipFiles = zipFiles;
    }

    public Map<String, Folder> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, Folder> folders) {
        this.folders = folders;
    }

    public void deleteAll() {
        name = null;
        path = null;
        size = 0;
        textFiles = null;
        zipFiles = null;
        folders = null;
    }
}

package com.mike.file.system;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by michaelbrennan on 5/4/16.
 */
public class TextFile implements FsGen{

    private String name;
    private String path;
    private double size;
    private String content;

    public TextFile() {
    }

    public TextFile(String content) {
        this.content = content;
    }

    public TextFile(double size) {
        this.size = size;
    }

    public TextFile(String name, String path) {
        this.name = name + ".txt";
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name + ".txt";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public double getSize() {
        return StringUtils.isEmpty(content)? 0:content.length();
    }

    @Override
    public String getFullPath() {
        return this.path + "/" + name;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

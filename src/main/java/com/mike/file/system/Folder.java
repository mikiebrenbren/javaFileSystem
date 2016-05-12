package com.mike.file.system;

import com.mike.util.SumUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michaelbrennan on 5/4/16.
 */
public class Folder extends MetaFile implements FsGen {

    public Folder() {
        textFiles = new HashMap<>();
        folders = new HashMap<>();
        zipFiles = new HashMap<>();
    }

    public Map<String, ZipFile> getZipFiles() {
        return zipFiles;
    }

    public void setZipFiles(Map<String, ZipFile> zipFiles) {
        this.zipFiles = zipFiles;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;

    }

    @Override
    public double getSize() {
        double sum = 0;
        sum += SumUtil.sum(textFiles, zipFiles, folders, 0);
        return sum;
    }

    @Override
    public String getFullPath() {
        return this.path + "/" + name;
    }

}

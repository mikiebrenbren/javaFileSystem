package com.mike.file.system;

import com.mike.util.SumUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by michaelbrennan on 5/4/16.'
 * This entity can hold folders, files and zip
 */
public class Drive extends MetaFile implements FsGen {

    private final Logger lgr = Logger.getLogger(this.getClass().getName());

    public Drive() {
        textFiles = new HashMap<>();
        folders = new HashMap<>();
        zipFiles = new HashMap<>();
        lgr.info("Drive created");
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
        return this.path;
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

package com.mike.file.system;

import com.mike.util.SumUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michaelbrennan on 5/4/16.
 */
public class ZipFile extends MetaFile implements FsGen{

    public ZipFile() {
        textFiles = new HashMap<>();
        zipFiles = new HashMap<>();
        folders = new HashMap<>();
    }


    public Map<String, Folder> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, Folder> folders) {
        this.folders = folders;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name + ".zip";
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

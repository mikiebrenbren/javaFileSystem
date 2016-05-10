package com.mike.util;

import com.mike.file.system.Folder;
import com.mike.file.system.TextFile;
import com.mike.file.system.ZipFile;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by michaelbrennan on 5/5/16.
 */
public class SumUtil {

    private static final Logger lgr = Logger.getLogger(SumUtil.class.getName());

    /**
     * Sums the size of the maps of text files, zip files and folders
     * @param t
     * @param z
     * @param f
     * @return
     */
    //todo test this better from the drive level
    public static double sum(Map<String, TextFile> t, Map<String, ZipFile> z, Map<String, Folder> f) {

        if (isAllEmpty(t, z, f)) {
            lgr.info("all files are empty");
            return 0;
        }

        double tSum = 0;
        double zSum = 0;
        double fSum = 0;

        if (t != null && !t.isEmpty()) {
            tSum = t.values().stream()
                    .mapToDouble(TextFile::getSize)
                    .sum();
        }

        if (z != null && !z.isEmpty()) {
            zSum = z.values().stream()
                    .mapToDouble(ZipFile::getSize)
                    .sum();
        }

        if (f != null && !f.isEmpty()) {
            fSum = f.values().stream()
                    .mapToDouble(Folder::getSize)
                    .sum();
        }

        return tSum + zSum + fSum;
    }

    private static boolean isAllEmpty(Map<String, TextFile> t, Map<String, ZipFile> z, Map<String, Folder> f) {
        return t.isEmpty() && z.isEmpty() && f.isEmpty();
    }
}

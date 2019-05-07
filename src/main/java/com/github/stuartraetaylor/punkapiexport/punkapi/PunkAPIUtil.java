package com.github.stuartraetaylor.punkapiexport.punkapi;

import org.apache.commons.lang3.StringUtils;

class PunkAPIUtil {

    static String formatBeerName(String name) {
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("Empty beer name");

        return name.trim().replaceAll("\\s+", "_").toLowerCase();
    }

    static String removeExtension(String fileName) {
        if (StringUtils.isEmpty(fileName))
            throw new IllegalArgumentException("Empty file name");

        int pos = fileName.lastIndexOf('.');
        if (pos == -1)
            return fileName; // no extension.

        return fileName.substring(0, pos);
    }

}

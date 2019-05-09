package com.github.stuartraetaylor.punkapiexport.beerxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class YeastParser {

    private static final Pattern wyeast     = Pattern.compile(".*Wyeast.*?\\s([1-9][0-9]{3}).*");
    private static final Pattern whiteLabs  = Pattern.compile(".*(WLP[0-9]{3,4}).*");
    private static final Pattern safale     = Pattern.compile(".*?(U?S\\-[0-9]{2}).*");

    static String parse(String yeast) {
        if (yeast == null)
            return null;

        String strain;
        if ((strain = parse(yeast, wyeast)) != null)
            return strain;
        if ((strain = parse(yeast, whiteLabs)) != null)
            return strain;
        if ((strain = parse(yeast, safale)) != null)
            return strain;

        return null;
    }

    private static String parse(String yeast, Pattern pattern) {
        Matcher matcher = pattern.matcher(yeast);
        if (matcher.matches())
            return matcher.group(1);

        return null;
    }

}

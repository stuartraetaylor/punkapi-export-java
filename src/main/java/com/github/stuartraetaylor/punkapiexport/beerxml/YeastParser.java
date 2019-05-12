package com.github.stuartraetaylor.punkapiexport.beerxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.stuartraetaylor.punkapiexport.reference.Yeast;

class YeastParser {

    private static final Pattern wyeast     = Pattern.compile(".*wyeast.*?\\s([1-9][0-9]{3}).*");
    private static final Pattern whiteLabs  = Pattern.compile(".*(wlp[0-9]{3,4}).*");
    private static final Pattern safale     = Pattern.compile(".*?(u?s\\-[0-9]{2}).*");

    static String parse(String yeast) {
        String identifier = Yeast.normalise(yeast);
        if (identifier == null)
            return null;

        String strain;
        if ((strain = parse(identifier, wyeast)) != null)
            return strain;
        if ((strain = parse(identifier, whiteLabs)) != null)
            return strain;
        if ((strain = parse(identifier, safale)) != null)
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

package com.github.stuartraetaylor.punkapiexport.beerxml;

public enum HopUse {

    FIRST_WORT  ("First Wort", null),
    START       ("Boil", BeerXMLWriter.defaultBoilTime),
    MIDDLE      ("Boil", 15),
    END         ("Boil", 0),
    WHIRLPOOL   ("Whirlpool", 20),
    DRY_HOP("Dry Hop", null),

    SECONDARY   ("Secondary", null),
    MASH        ("Mash", null);

    private final String use;
    private final Integer time;

    HopUse(String use, Integer time) {
        this.use = use;
        this.time = time;
    }

    public String getUse() {
        return use;
    }

    public Integer getTime() {
        return time;
    }

}

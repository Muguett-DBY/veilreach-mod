package com.muguett.veilreach.world;

public enum HushlandsRegion {
    OUTER("hud.veilreach.hushlands.region.outer", "title.veilreach.hushlands.outer"),
    MID("hud.veilreach.hushlands.region.mid", "title.veilreach.hushlands.mid"),
    INNER("hud.veilreach.hushlands.region.inner", "title.veilreach.hushlands.inner");

    private final String hudKey;
    private final String titleKey;

    HushlandsRegion(String hudKey, String titleKey) {
        this.hudKey = hudKey;
        this.titleKey = titleKey;
    }

    public String hudKey() {
        return hudKey;
    }

    public String titleKey() {
        return titleKey;
    }

    public String subtitleKey() {
        return titleKey + ".subtitle";
    }
}

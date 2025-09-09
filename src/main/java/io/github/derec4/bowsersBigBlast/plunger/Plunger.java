package io.github.derec4.bowsersBigBlast.plunger;

public class Plunger {
    private Location location;
    private boolean isDetonator;

    public Plunger(Location location, boolean isDetonator) {
        this.location = location;
        this.isDetonator = isDetonator;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isDetonator() {
        return isDetonator;
    }
}

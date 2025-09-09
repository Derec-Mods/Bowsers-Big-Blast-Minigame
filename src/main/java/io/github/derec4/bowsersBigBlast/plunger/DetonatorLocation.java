package io.github.derec4.bowsersBigBlast.plunger;

public class DetonatorLocation {
    private double x, y, z;
    private String world;

    public DetonatorLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}


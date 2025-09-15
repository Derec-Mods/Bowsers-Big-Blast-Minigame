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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DetonatorLocation other = (DetonatorLocation) obj;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0 &&
               world.equals(other.world);
    }
}

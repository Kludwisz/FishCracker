package com.kludwisz.fishcracker.math;

import java.util.Objects;

public class Vec3i {
    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Vec3i add(Vec3i other) {
        return new Vec3i(x + other.x, y + other.y, z + other.z);
    }

    public Vec3i subtract(Vec3i other) {
        return new Vec3i(x - other.x, y - other.y, z - other.z);
    }

    public Vec3i multiply(int scalar) {
        return new Vec3i(x * scalar, y * scalar, z * scalar);
    }

    public Vec3i divide(int scalar) {
        return new Vec3i(x / scalar, y / scalar, z / scalar);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vec3i other = (Vec3i) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

package com.kludwisz.fishcracker.math;

public record Vec2(double x, double z) {
    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, z + other.z);
    }

    public Vec2 sub(Vec2 other) {
        return new Vec2(x - other.x, z - other.z);
    }

    public double dot(Vec2 other) {
        return x * other.x + z * other.z;
    }

    public Vec2 mul(int scalar) {
        return new Vec2(x * scalar, z * scalar);
    }

    public Vec2 div(int scalar) {
        return new Vec2(x / scalar, z / scalar);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vec2 other = (Vec2) obj;
        return x == other.x && z == other.z;
    }

}

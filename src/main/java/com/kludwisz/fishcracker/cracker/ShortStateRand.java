package com.kludwisz.fishcracker.cracker;

public class ShortStateRand {
    // Java Random but the states are 19-bit
    public static final int MASK = (1 << 19) - 1;
    public static final int MULTIPLIER = 321133;
    public static final int ADDEND = 11;

    private static final int REGION_SEED_A = (int)(341873128712L & MASK);
    private static final int REGION_SEED_B = (int)(132897987541L & MASK);

    private int seed;

    public void setSeed(int seed) {
        this.seed = (seed ^ MULTIPLIER) & MASK;
    }

    public void setRegionSeed(int seed, int regionX, int regionZ, int salt) {
        this.setSeed(seed + regionX * REGION_SEED_A + regionZ * REGION_SEED_B + salt);
    }

    public void nextSeed() {
        this.seed = (this.seed * MULTIPLIER + ADDEND) & MASK;
    }

    public int nextIntRemainder() {
        this.nextSeed();
        return (this.seed >>> 17) & 3;
    }
}

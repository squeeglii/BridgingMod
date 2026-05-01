package me.cg360.mod.bridging.util.flags;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Flag implements Comparable<Flag> {

    private static final AtomicInteger flagIncrementor = new AtomicInteger(0);

    private final int id;
    private final String label; // debugging really. Doesn't do anything functional.

    public Flag(String label) {
        this.id = flagIncrementor.getAndAdd(1);
        this.label = label;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Flag flag && flag.id == this.id;
    }

    @Override
    public int compareTo(@NotNull Flag o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return "Flag[#%s, nicknamed: '%s']".formatted(this.id, this.label);
    }
}

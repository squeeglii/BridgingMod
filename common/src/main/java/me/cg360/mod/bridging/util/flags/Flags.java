package me.cg360.mod.bridging.util.flags;

import java.util.Arrays;
import java.util.TreeSet;

public class Flags {

    public static final Flag SKIP_OUTLINE_RENDERING = new Flag("SKIP_OUTLINE_RENDERING");

    private final TreeSet<Flag> flags;

    public Flags(Flag... flagsIn) {
        this.flags = new TreeSet<>();
        this.flags.addAll(Arrays.asList(flagsIn));
    }

    private Flags(TreeSet<Flag> existingFlags, Flag... newFlags) {
        this.flags = new TreeSet<>(existingFlags);
        this.flags.addAll(Arrays.asList(newFlags));
    }

    public boolean hasAll(Flag... flagsIn) {
        for(Flag f : flagsIn)
            if(!this.flags.contains(f))
                return false;

        return true;
    }

    public Flags extend(Flag... newFlags) {
        return new Flags(this.flags, newFlags);
    }

    public static Flags empty() {
        return new Flags();
    }

}

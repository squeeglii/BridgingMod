package me.cg360.mod.bridging.config.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.cg360.mod.bridging.BridgingMod;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Plain Gson-backed replacement for YACL's ConfigClassHandler.
 * Needed on Forge, since YACL publishes no Forge build for 1.21.1.
 */
public class SimpleConfigHandler<T> {

    private final Path path;
    private final Class<T> type;
    private final Supplier<T> factory;
    private final Gson gson;
    private T instance;

    public SimpleConfigHandler(Path path, Class<T> type, Supplier<T> factory) {
        this.path = path;
        this.type = type;
        this.factory = factory;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
                .create();
    }

    public T instance() {
        if (this.instance == null)
            this.instance = this.factory.get();

        return this.instance;
    }

    public void load() {
        // Build a fresh instance first, so default-value tracking runs via its own constructor.
        T fresh = this.factory.get();
        this.instance = fresh;

        if (Files.exists(this.path)) {
            try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
                T loaded = this.gson.fromJson(reader, this.type);
                if (loaded != null) this.copyFields(loaded, fresh);
            } catch (IOException | JsonParseException err) {
                BridgingMod.getLogger().warn("Failed to load config, using defaults. [%s]".formatted(err.getMessage()));
            }
        }

        this.save();
    }

    public void save() {
        try {
            Path parent = this.path.getParent();
            if (parent != null) Files.createDirectories(parent);

            try (BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
                this.gson.toJson(this.instance(), this.type, writer);
            }
        } catch (IOException err) {
            BridgingMod.getLogger().warn("Failed to save config. [%s]".formatted(err.getMessage()));
        }
    }

    private void copyFields(T from, T to) {
        for (Field field : this.type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;

            try {
                field.setAccessible(true);
                field.set(to, field.get(from));
            } catch (Exception err) {
                BridgingMod.getLogger().warn("Unable to copy config field '%s' [%s]".formatted(field.getName(), err.getMessage()));
            }
        }
    }

    private static class ColorAdapter extends TypeAdapter<Color> {

        @Override
        public void write(JsonWriter out, Color value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.value(value.getRGB() & 0xFFFFFFFFL);
        }

        @Override
        public Color read(JsonReader in) throws IOException {
            long argb = in.nextLong();
            return new Color((int) argb, true);
        }
    }

}

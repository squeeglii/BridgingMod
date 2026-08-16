package me.cg360.mod.bridging.config;

import com.google.gson.*;
import me.cg360.mod.bridging.BridgingMod;

import java.awt.*;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class SimpleConfigHandler<T> {

    private final Path configPath;
    private final Class<T> configType;
    private final Supplier<T> defaultsSupplier;
    private final Gson gson;

    private T instance;

    public SimpleConfigHandler(Path configPath, Class<T> configType, Supplier<T> defaultsSupplier) {
        this.configPath = configPath;
        this.configType = configType;
        this.defaultsSupplier = defaultsSupplier;
        this.instance = defaultsSupplier.get();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Color.class, new ColorAdapter())
                .create();
    }

    public T instance() {
        return this.instance;
    }

    public void load() {
        this.instance = this.defaultsSupplier.get();

        try {
            if (this.configPath.getParent() != null) {
                Files.createDirectories(this.configPath.getParent());
            }

            if (!Files.exists(this.configPath)) {
                this.save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(this.configPath)) {
                T parsed = this.gson.fromJson(reader, this.configType);

                if (parsed != null) {
                    this.instance = parsed;
                }
            }

        } catch (Exception err) {
            BridgingMod.getLogger().warn("Unable to load config at {}: {}", this.configPath, err.getMessage());
        }
    }

    public void save() {
        try {
            if (this.configPath.getParent() != null) {
                Files.createDirectories(this.configPath.getParent());
            }

            try (Writer writer = Files.newBufferedWriter(this.configPath)) {
                this.gson.toJson(this.instance, this.configType, writer);
            }

        } catch (Exception err) {
            BridgingMod.getLogger().warn("Unable to save config at {}: {}", this.configPath, err.getMessage());
        }
    }

    public static Path resolveDefaultPath(String modId) {
        Path fabric = resolveFabricConfigDir();
        if (fabric != null) return fabric.resolve(modId + ".json");

        Path neoForge = resolveConfigDirByClass("net.neoforged.fml.loading.FMLPaths");
        if (neoForge != null) return neoForge.resolve(modId + ".json");

        Path forge = resolveConfigDirByClass("net.minecraftforge.fml.loading.FMLPaths");
        if (forge != null) return forge.resolve(modId + ".json");

        return Paths.get("config").resolve(modId + ".json");
    }

    private static Path resolveFabricConfigDir() {
        try {
            Class<?> loaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object loader = loaderClass.getMethod("getInstance").invoke(null);
            Object configDir = loaderClass.getMethod("getConfigDir").invoke(loader);

            if (configDir instanceof Path path) {
                return path;
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private static Path resolveConfigDirByClass(String className) {
        try {
            Class<?> fmlPathsClass = Class.forName(className);
            Field configDirField = fmlPathsClass.getField("CONFIGDIR");
            Object configDirToken = configDirField.get(null);
            Object pathObj = configDirToken.getClass().getMethod("get").invoke(configDirToken);

            if (pathObj instanceof Path path) {
                return path;
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private static class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getRGB());
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return new Color(0, 0, 0, 0);
            }

            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                return new Color(json.getAsInt(), true);
            }

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                int red = obj.has("red") ? obj.get("red").getAsInt() : 0;
                int green = obj.has("green") ? obj.get("green").getAsInt() : 0;
                int blue = obj.has("blue") ? obj.get("blue").getAsInt() : 0;
                int alpha = obj.has("alpha") ? obj.get("alpha").getAsInt() : 255;
                return new Color(red, green, blue, alpha);
            }

            throw new JsonParseException("Unsupported color format in config");
        }
    }
}

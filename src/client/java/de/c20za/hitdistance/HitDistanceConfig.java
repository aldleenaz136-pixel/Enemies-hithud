package de.c20za.hitdistance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HitDistanceConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int[] COLORS = {
            0xFFFF5555, 0xFFFFAA00, 0xFFFFFF55, 0xFF55FF55,
            0xFF55FFFF, 0xFF5599FF, 0xFFFF55FF, 0xFFFFFFFF
    };

    public static int x = 20;
    public static int y = 20;
    public static float scale = 1.0F;
    public static int decimals = 2;
    public static int colorIndex = 0;

    private HitDistanceConfig() {}

    public static int color() {
        return COLORS[Math.floorMod(colorIndex, COLORS.length)];
    }

    public static void load() {
        Path path = configPath();
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                Stored stored = GSON.fromJson(reader, Stored.class);
                if (stored != null) {
                    x = stored.x;
                    y = stored.y;
                    scale = stored.scale;
                    decimals = stored.decimals;
                    colorIndex = stored.colorIndex;
                }
            } catch (Exception ignored) {
                // Keep defaults if the file is damaged or unreadable.
            }
        }
        clamp();
    }

    public static void save() {
        clamp();
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(new Stored(x, y, scale, decimals, colorIndex), writer);
            }
        } catch (Exception ignored) {
            // A failed config write should never interrupt gameplay.
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("hitdistance.json");
    }

    private static void clamp() {
        x = Math.max(0, x);
        y = Math.max(0, y);
        scale = Math.max(0.75F, Math.min(2.0F, scale));
        decimals = Math.max(0, Math.min(5, decimals));
        colorIndex = Math.floorMod(colorIndex, COLORS.length);
    }

    private record Stored(int x, int y, float scale, int decimals, int colorIndex) {}
}

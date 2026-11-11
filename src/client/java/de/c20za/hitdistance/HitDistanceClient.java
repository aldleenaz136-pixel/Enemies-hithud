package de.c20za.hitdistance;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public final class HitDistanceClient implements ClientModInitializer {
    public static final String MOD_ID = "hitdistance";
    private static final long DISPLAY_MILLIS = 8_000L;
    private static HitRecord latest;

    @Override
    public void onInitializeClient() {
        HitDistanceConfig.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "controls")
        );
        KeyMapping openSettings = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.hitdistance.open_settings",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettings.consumeClick()) {
                if (client.player != null) {
                    client.gui.setScreen(new HitDistanceConfigScreen());
                }
            }
        });

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(MOD_ID, "hit_distance"),
                (graphics, deltaTracker) -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.player == null || client.screen != null) return;
                    HitRecord hit = latest;
                    if (hit == null || System.currentTimeMillis() - hit.timeMillis() > DISPLAY_MILLIS) return;
                    drawLabel(graphics, hit.displayText(), HitDistanceConfig.x, HitDistanceConfig.y);
                }
        );
    }

    public static void recordHit(String playerName, double distance) {
        latest = new HitRecord(playerName, distance, System.currentTimeMillis());
    }

    public static String previewText() {
        return "Treffer von Spieler: " + format(3.25) + " Blöcke";
    }

    public static int textWidth(String text) {
        return Minecraft.getInstance().font.width(text);
    }

    public static void drawLabel(GuiGraphicsExtractor graphics, String text, int x, int y) {
        Minecraft client = Minecraft.getInstance();
        var matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(HitDistanceConfig.scale, HitDistanceConfig.scale);
        int width = textWidth(text);
        int height = client.font.lineHeight;
        graphics.fill(-4, -3, width + 4, height + 3, 0xB0000000);
        graphics.outline(-4, -3, width + 8, height + 6, 0xA0FFFFFF);
        graphics.text(client.font, text, 0, 0, HitDistanceConfig.color(), true);
        matrices.popMatrix();
    }

    private static String format(double distance) {
        int decimals = Math.max(0, Math.min(5, HitDistanceConfig.decimals));
        return String.format(Locale.GERMANY, "%" + "." + decimals + "f", distance);
    }

    private record HitRecord(String playerName, double distance, long timeMillis) {
        private String displayText() {
            return "Hit von " + playerName + ": " + format(distance) + " Blöcke";
        }
    }
}

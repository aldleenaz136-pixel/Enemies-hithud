package de.c20za.hitdistance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class HitDistanceConfigScreen extends Screen {
    private static final int PANEL_WIDTH = 236;
    private static final int PANEL_HEIGHT = 166;
    private boolean draggingPreview;
    private double dragOffsetX;
    private double dragOffsetY;

    public HitDistanceConfigScreen() {
        super(Component.literal("Hit Distance HUD"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int left = width / 2 - PANEL_WIDTH / 2;
        int top = height / 2 - PANEL_HEIGHT / 2;
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xE011141A);
        graphics.outline(left, top, PANEL_WIDTH, PANEL_HEIGHT, 0xFF8FA7C5);
        graphics.text(font, "Hit-Distanz HUD", left + 14, top + 12, 0xFFFFFFFF, true);
        graphics.text(font, "Vorschau mit der Maus verschieben", left + 14, top + 28, 0xFFC7D0DC, false);

        graphics.text(font, "Textfarbe", left + 14, top + 51, 0xFFFFFFFF, false);
        button(graphics, left + 148, top + 45, 72, 20, "Wechseln");
        graphics.fill(left + 124, top + 49, left + 140, top + 61, HitDistanceConfig.color());
        graphics.outline(left + 124, top + 49, 16, 12, 0xFFFFFFFF);

        graphics.text(font, "Größe", left + 14, top + 78, 0xFFFFFFFF, false);
        button(graphics, left + 90, top + 70, 28, 20, "-");
        graphics.text(font, String.format(java.util.Locale.ROOT, "%.2f", HitDistanceConfig.scale), left + 127, top + 76, 0xFFFFFFFF, false);
        button(graphics, left + 176, top + 70, 28, 20, "+");

        graphics.text(font, "Dezimalstellen (0-5)", left + 14, top + 105, 0xFFFFFFFF, false);
        button(graphics, left + 136, top + 97, 28, 20, "-");
        graphics.text(font, Integer.toString(HitDistanceConfig.decimals), left + 174, top + 103, 0xFFFFFFFF, false);
        button(graphics, left + 192, top + 97, 28, 20, "+");

        graphics.text(font, "Die Distanz ist eine Näherung.", left + 14, top + 128, 0xFFFFD080, false);
        button(graphics, left + 84, top + 137, 68, 20, "Fertig");

        HitDistanceClient.drawLabel(graphics, HitDistanceClient.previewText(), HitDistanceConfig.x, HitDistanceConfig.y);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        if (click.button() != 0) return super.mouseClicked(click, doubleClick);
        double mx = click.x();
        double my = click.y();

        int left = width / 2 - PANEL_WIDTH / 2;
        int top = height / 2 - PANEL_HEIGHT / 2;

        if (inside(mx, my, left + 148, top + 45, left + 220, top + 65)) {
            HitDistanceConfig.colorIndex++;
            HitDistanceConfig.save();
            return true;
        }
        if (inside(mx, my, left + 90, top + 70, left + 118, top + 90)) {
            HitDistanceConfig.scale = Math.max(0.75F, HitDistanceConfig.scale - 0.25F);
            HitDistanceConfig.save();
            return true;
        }
        if (inside(mx, my, left + 176, top + 70, left + 204, top + 90)) {
            HitDistanceConfig.scale = Math.min(2.0F, HitDistanceConfig.scale + 0.25F);
            HitDistanceConfig.save();
            return true;
        }
        if (inside(mx, my, left + 136, top + 97, left + 164, top + 117)) {
            HitDistanceConfig.decimals = Math.max(0, HitDistanceConfig.decimals - 1);
            HitDistanceConfig.save();
            return true;
        }
        if (inside(mx, my, left + 192, top + 97, left + 220, top + 117)) {
            HitDistanceConfig.decimals = Math.min(5, HitDistanceConfig.decimals + 1);
            HitDistanceConfig.save();
            return true;
        }
        if (inside(mx, my, left + 84, top + 137, left + 152, top + 157)) {
            onClose();
            return true;
        }
        if (insidePreview(mx, my)) {
            draggingPreview = true;
            dragOffsetX = mx - HitDistanceConfig.x;
            dragOffsetY = my - HitDistanceConfig.y;
            return true;
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double dragX, double dragY) {
        if (draggingPreview && click.button() == 0) {
            movePreview(click.x(), click.y());
            return true;
        }
        return super.mouseDragged(click, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (click.button() == 0 && draggingPreview) {
            draggingPreview = false;
            HitDistanceConfig.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    private boolean insidePreview(double mx, double my) {
        String text = HitDistanceClient.previewText();
        double scale = HitDistanceConfig.scale;
        double previewWidth = (HitDistanceClient.textWidth(text) + 8) * scale;
        double previewHeight = (Minecraft.getInstance().font.lineHeight + 6) * scale;
        return mx >= HitDistanceConfig.x - 4 && mx <= HitDistanceConfig.x + previewWidth
                && my >= HitDistanceConfig.y - 3 && my <= HitDistanceConfig.y + previewHeight;
    }

    private void movePreview(double mouseX, double mouseY) {
        String text = HitDistanceClient.previewText();
        int previewWidth = (int) ((HitDistanceClient.textWidth(text) + 8) * HitDistanceConfig.scale);
        int previewHeight = (int) ((Minecraft.getInstance().font.lineHeight + 6) * HitDistanceConfig.scale);
        HitDistanceConfig.x = clamp((int) (mouseX - dragOffsetX), 0, Math.max(0, width - previewWidth));
        HitDistanceConfig.y = clamp((int) (mouseY - dragOffsetY), 0, Math.max(0, height - previewHeight));
    }

    private static void button(GuiGraphicsExtractor graphics, int x, int y, int width, int height, String label) {
        graphics.fill(x, y, x + width, y + height, 0xFF303946);
        graphics.outline(x, y, width, height, 0xFF8FA7C5);
        int textWidth = Minecraft.getInstance().font.width(label);
        graphics.text(Minecraft.getInstance().font, label, x + (width - textWidth) / 2, y + 5, 0xFFFFFFFF, false);
    }

    private static boolean inside(double x, double y, int left, int top, int right, int bottom) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}

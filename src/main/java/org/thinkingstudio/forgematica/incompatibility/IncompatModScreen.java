package org.thinkingstudio.forgematica.incompatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class IncompatModScreen extends Screen {
    public final Screen lastScreen;

    private static final Text titleText = Text.translatable("forgematica.incompat_check_screen.title").formatted(Formatting.RED, Formatting.BOLD);
    private static final Text text = Text.translatable("forgematica.incompat_check_screen.text", IncompatModChecker.getIncompatModNames()).formatted(Formatting.RED, Formatting.BOLD);
    private static final Text closeScreenButtonText = Text.translatable("selectWorld.backupJoinSkipButton");
    private static final Text closeGameButtonText = Text.translatable("forgematica.incompat_check_screen.closeGameButton");

    private static final int HEADER_HEIGHT = 40;
    private static final int FOOTER_HEIGHT = 50;

    public IncompatModScreen(Screen lastScreen) {
        super(titleText);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        super.init();

        this.addDrawableChild(ButtonWidget.builder(closeScreenButtonText, buttonWidget -> this.close()).dimensions(centerX - 5 - 150, this.height - (FOOTER_HEIGHT / 2) - 10, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(closeGameButtonText, buttonWidget -> Objects.requireNonNull(this.client).close()).dimensions(centerX + 5, this.height - (FOOTER_HEIGHT / 2) - 10, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, (HEADER_HEIGHT / 2) - (this.textRenderer.fontHeight / 2), -1);
        context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, 70, -1);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);

        //Render header and footer separators
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.fillGradient(0, 40 - 2, 0, 0, this.width, 2, 32);
        context.fillGradient(0, this.height - 50, 0, 0, this.width, 2, 32);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.lastScreen);
    }
}

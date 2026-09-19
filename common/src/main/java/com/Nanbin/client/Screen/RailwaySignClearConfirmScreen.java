package com.Nanbin.client.Screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Generic "are you sure you want to clear everything?" confirmation screen with a
 * confirm/cancel pair, backed by the Nanbin GUI background texture.
 */
public class RailwaySignClearConfirmScreen extends Screen {

	private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath("nanbin", "textures/gui/background.png");
	private static final int BACKGROUND_WIDTH = 3840;
	private static final int BACKGROUND_HEIGHT = 2160;

	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;

	private final Screen parent;
	private final Runnable onConfirm;
	private final Button btnConfirm;
	private final Button btnCancel;

	public static Screen create(Screen parent, Runnable onConfirm) {
		return new RailwaySignClearConfirmScreen(parent, onConfirm);
	}

	private RailwaySignClearConfirmScreen(Screen parent, Runnable onConfirm) {
		super(Component.translatable("gui.nanbin.clear"));
		this.parent = parent;
		this.onConfirm = onConfirm;
		this.btnConfirm = Button.builder(Component.translatable("gui.nanbin.confirm"), button -> {
			this.onConfirm.run();
			minecraft.setScreen(this.parent);
		}).bounds(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT).build();
		this.btnCancel = Button.builder(Component.translatable("gui.nanbin.cancel"), button -> onClose())
				.bounds(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT).build();
	}

	@Override
	protected void init() {
		final int centerX = width / 2;
		final int centerY = height / 2;
		btnConfirm.setPosition(centerX - BUTTON_WIDTH - 5, centerY);
		btnCancel.setPosition(centerX + 5, centerY);
		addRenderableWidget(btnConfirm);
		addRenderableWidget(btnCancel);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, 0F, 0F, width, height,
				BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
		final Component question = Component.translatable("gui.nanbin.clear.question");
		guiGraphics.centeredText(font, question, width / 2, height / 2 - 40, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}
}

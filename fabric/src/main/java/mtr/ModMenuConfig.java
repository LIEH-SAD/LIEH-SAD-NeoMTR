package mtr;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mtr.screen.ConfigScreen;
import net.minecraft.client.Minecraft;

public class ModMenuConfig implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::createScreen;
	}
}
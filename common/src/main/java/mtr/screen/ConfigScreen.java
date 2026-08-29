package mtr.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mtr.Keys;
import mtr.client.Config;
import mtr.data.IGui;
import mtr.mappings.Text;
import mtr.packet.PacketTrainDataGuiClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreen implements IGui {

	public static Screen createScreen(Screen parent) {
		Config.refreshProperties();
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("gui.mtr.config_title",Keys.MOD_VERSION));
		builder.setSavingRunnable(Config::save);

		ConfigCategory g = builder.getOrCreateCategory(Text.translatable("gui.mtr.config_category_general"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		if (Minecraft.getInstance().level != null) {
			g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("gui.mtr.use_time_and_wind_sync"), false).setDefaultValue(false).setSaveConsumer(PacketTrainDataGuiClient::sendUseTimeAndWindSyncC2S).build());
		}
		g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.use_mtr_font"), Config.useMTRFont()).setDefaultValue(true).setSaveConsumer(Config::setUseMTRFont).build());
		g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.shift_to_toggle_sitting", "SHIFT"), Config.shiftToToggleSitting()).setDefaultValue(true).setSaveConsumer(Config::setShiftToToggleSitting).build());
		g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.hide_translucent_parts"), Config.hideTranslucentParts()).setDefaultValue(false).setSaveConsumer(Config::setHideTranslucentParts).build());
		if (!Keys.LIFTS_ONLY) {
			g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.show_announcement_messages"), Config.showAnnouncementMessages()).setDefaultValue(true).setSaveConsumer(Config::setShowAnnouncementMessages).build());
			g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.use_tts_announcements"), Config.useTTSAnnouncements()).setDefaultValue(false).setSaveConsumer(Config::setUseTTSAnnouncements).build());
			g.addEntry(entryBuilder.startSelector(Text.translatable("options.mtr.language_options"), new Integer[]{0, 1, 2}, Config.languageOptions()).setNameProvider(value -> Text.translatable("options.mtr.language_options_" + value)).setSaveConsumer(Config::setLanguageOptions).build());
			g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.hide_special_rail_colors"), Config.hideSpecialRailColors()).setDefaultValue(false).setSaveConsumer(Config::setHideSpecialRailColors).build());
			g.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.mtr.use_dynamic_fps"), Config.useDynamicFPS()).setDefaultValue(true).setSaveConsumer(Config::setUseDynamicFPS).build());
			g.addEntry(entryBuilder.startIntSlider(Text.translatable("options.mtr.track_texture_offset"), Config.trackTextureOffset(), 0, Config.TRACK_OFFSET_COUNT - 1).setDefaultValue(0).setSaveConsumer(Config::setTrackTextureOffset).setTextGetter(value -> Text.literal(value.toString())).build());
			g.addEntry(entryBuilder.startIntSlider(Text.translatable("options.mtr.dynamic_texture_resolution"), Config.dynamicTextureResolution(), 0, Config.DYNAMIC_RESOLUTION_COUNT - 1).setDefaultValue(3).setSaveConsumer(Config::setDynamicTextureResolution).setTextGetter(value -> Text.literal(value.toString())).build());
			g.addEntry(entryBuilder.startIntSlider(Text.translatable("options.mtr.vehicle_render_distance_ratio"), Config.trainRenderDistanceRatio(), 1, Config.TRAIN_RENDER_DISTANCE_RATIO_COUNT).setDefaultValue(Config.TRAIN_RENDER_DISTANCE_RATIO_COUNT).setSaveConsumer(Config::setTrainRenderDistanceRatio).setTextGetter(value -> Text.literal(String.format("%d%%", value * 100 / Config.TRAIN_RENDER_DISTANCE_RATIO_COUNT))).build());
		}

		ConfigCategory c = builder.getOrCreateCategory(Text.translatable("gui.mtr.config_contributor"));
		c.addEntry(entryBuilder.startTextDescription(Text.translatable("options.mtr.support_patreon")).build());

		return builder.build();
	}
}
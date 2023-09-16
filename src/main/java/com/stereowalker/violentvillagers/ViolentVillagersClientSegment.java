package com.stereowalker.violentvillagers;

import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.mod.ClientSegment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class ViolentVillagersClientSegment extends ClientSegment {

	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation(ViolentVillagers.MOD_ID, "textures/gui/controller_icon2.png");
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, ViolentVillagers.CONFIG);
	}

}

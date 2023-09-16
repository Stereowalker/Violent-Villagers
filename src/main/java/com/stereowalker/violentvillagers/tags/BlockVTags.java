package com.stereowalker.violentvillagers.tags;

import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.violentvillagers.ViolentVillagers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockVTags {
    public static final TagKey<Block> GUARDED_BY_VILLAGERS = BlockVTags.create("guarded_by_villagers");

	public BlockVTags() {
	}

	private static TagKey<Block> create(String pName) {
		return TagKey.create(RegistryHelper.blockKey(), new ResourceLocation(ViolentVillagers.MOD_ID, pName));
	}

	public static TagKey<Block> create(ResourceLocation name) {
		return TagKey.create(RegistryHelper.blockKey(), name);
	}
}

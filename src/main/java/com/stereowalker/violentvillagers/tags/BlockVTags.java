package com.stereowalker.violentvillagers.tags;

import com.stereowalker.violentvillagers.ViolentVillagers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockVTags {
    public static final TagKey<Block> GUARDED_BY_VILLAGERS = BlockVTags.create("guarded_by_villagers");

	public BlockVTags() {
	}

	private static TagKey<Block> create(String pName) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(ViolentVillagers.MOD_ID, pName));
	}

	public static TagKey<Block> create(ResourceLocation name) {
		return TagKey.create(Registries.BLOCK, name);
	}
}

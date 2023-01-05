package com.stereowalker.violentvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.violentvillagers.ViolentVillagers;
import com.stereowalker.violentvillagers.tags.BlockVTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public abstract class BlockMixin {

	@Inject(method = "playerWillDestroy", at = @At("TAIL"))
	public void use_inject(Level level, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
		if (state.is(BlockVTags.GUARDED_BY_VILLAGERS)) {
			ViolentVillagers.upsetNearbyVillagers(player, pos, false);
        }
	}
}

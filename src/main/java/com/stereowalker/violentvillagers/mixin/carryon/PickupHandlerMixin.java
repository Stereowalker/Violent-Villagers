package com.stereowalker.violentvillagers.mixin.carryon;

import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.violentvillagers.ViolentVillagers;
import com.stereowalker.violentvillagers.tags.BlockVTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tschipp.carryon.common.carry.PickupHandler;

@Mixin(PickupHandler.class)
public abstract class PickupHandlerMixin {

	@Inject(method = "tryPickUpBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
	private static void tryPickUpBlock_inject(ServerPlayer player, BlockPos pos, Level level, @Nullable BiFunction<BlockState, BlockPos, Boolean> pickupCallback, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockState = level.getBlockState(pos);
		if (blockState.is(BlockVTags.GUARDED_BY_VILLAGERS) && player != null) {
			ViolentVillagers.upsetNearbyVillagers(player, pos, false);
        }
	}
}

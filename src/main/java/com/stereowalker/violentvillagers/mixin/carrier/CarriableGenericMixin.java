package com.stereowalker.violentvillagers.mixin.carrier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.violentvillagers.ViolentVillagers;
import com.stereowalker.violentvillagers.tags.BlockVTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

//1.19.2
//@Mixin(me.steven.carrier.impl.CarriableGeneric.class)
//1.19.3+
@Mixin(me.steven.carrier.impl.blocks.BaseCarriableBlock.class)
public abstract class CarriableGenericMixin {

	//1.19.2
	/*@Inject(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
	public void tryPickup_inject(@NotNull CarrierComponent carrier, @NotNull Level world, @NotNull BlockPos blockPos, @Nullable Entity entity, CallbackInfoReturnable<InteractionResult> cir) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.is(BlockVTags.GUARDED_BY_VILLAGERS) && carrier != null) {
			ViolentVillagers.upsetNearbyVillagers(carrier.getOwner(), blockPos, false);
        }
	}*/
	
	//1.19.3+
	@Inject(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
	public void tryPickup_inject(@NotNull Player player, @NotNull Level world, @NotNull BlockPos blockPos, @Nullable Entity entity, CallbackInfoReturnable<InteractionResult> cir) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.is(BlockVTags.GUARDED_BY_VILLAGERS) && player != null) {
			ViolentVillagers.upsetNearbyVillagers(player, blockPos, false);
        }
	}
}

package com.stereowalker.violentvillagers.mixin.carrier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.violentvillagers.ViolentVillagers;
import com.stereowalker.violentvillagers.tags.BlockVTags;

import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.impl.CarriableGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CarriableGeneric.class)
public abstract class CarriableGenericMixin {

	@Inject(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
	public void tryPickup_inject(@NotNull CarrierComponent carrier, @NotNull Level world, @NotNull BlockPos blockPos, @Nullable Entity entity, CallbackInfoReturnable<InteractionResult> cir) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.is(BlockVTags.GUARDED_BY_VILLAGERS)) {
			ViolentVillagers.upsetNearbyVillagers(carrier.getOwner(), blockPos, false);
        }
	}
}

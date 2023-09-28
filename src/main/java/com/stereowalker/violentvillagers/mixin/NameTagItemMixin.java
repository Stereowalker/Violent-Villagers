package com.stereowalker.violentvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.violentvillagers.ViolentVillagers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;

@Mixin(NameTagItem.class)
public class NameTagItemMixin {
	
	@Redirect(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isAlive()Z"))
	private boolean interactLivingEntity1(LivingEntity pTarget, ItemStack pStack, Player pPlayer, LivingEntity pTarget2, InteractionHand pHand) {
		if (pTarget instanceof Villager villager) {
			return villager.isAlive() && villager.getPlayerReputation(pPlayer) >= ViolentVillagers.CONFIG.rename_req;
		} else return pTarget.isAlive();
	}
}

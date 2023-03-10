package com.stereowalker.violentvillagers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.config.ConfigBuilder;
import com.stereowalker.unionlib.mod.MinecraftMod;
import com.stereowalker.violentvillagers.config.Config;
import com.stereowalker.violentvillagers.tags.BlockVTags;
import com.stereowalker.violentvillagers.world.entity.ai.sensing.VillagerAttackablesSensor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class ViolentVillagers extends MinecraftMod
{
	public static ViolentVillagers instance;
	public static final String MOD_ID = "violentvillagers";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Config CONFIG = new Config();
    public static final ReputationEventType VILLAGER_KILLED = ReputationEventType.register("villager_killed");

    public static final SensorType<VillagerAttackablesSensor> VILLAGER_ATTACKABLES = SensorType.register("villager_attackables", VillagerAttackablesSensor::new);

	public ViolentVillagers() 
	{
		super(MOD_ID, new ResourceLocation(MOD_ID, "textures/gui/controller_icon2.png"), LoadType.BOTH);
		new BlockVTags();
		ConfigBuilder.registerConfig(MOD_ID, CONFIG);
		instance = this;
	}
	
	@Override
	public void onModStartup() {
		super.onModStartup();
	}
	
	@Override
	public Map<EntityType<? extends LivingEntity>, List<Tuple<Attribute, Double>>> appendAttributesWithValues() {
		Map<EntityType<? extends LivingEntity>, List<Tuple<Attribute, Double>>> map = Maps.newHashMap();
		map.put(EntityType.VILLAGER, Lists.newArrayList(new Tuple<Attribute, Double>(Attributes.ATTACK_DAMAGE, 2.0D), new Tuple<Attribute, Double>(Attributes.ATTACK_KNOCKBACK, 2.0D)));
		return map;
	}

	@Override
	public void onModStartupInClient() {
	}

	@Override
	public void initClientAfterMinecraft(Minecraft mc) {
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, CONFIG);
	}

	public static ViolentVillagers getInstance() {
		return instance;
	}

//	public static void debug(String message) {
//		if (CONFIG.debug)LOGGER.info(message);
//	}
//
//	public static void debug(String message, Object o1) {
//		if (CONFIG.debug)LOGGER.info(message, o1);
//	}

	public ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
	
    public static void upsetNearbyVillagers(Player player, BlockPos pos, boolean angerOnlyIfCanSee) {
        List<Villager> list = player.level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(16.0));
        list.stream().filter(villager -> !angerOnlyIfCanSee || BehaviorUtils.canSee(villager, player)).forEach(villager -> {
        	Brain<Villager> brain = villager.getBrain();
    		if (brain.getMemory(MemoryModuleType.HOME).isPresent() && brain.getMemory(MemoryModuleType.HOME).get().pos().closerThan(pos, ViolentVillagers.CONFIG.distance_from_site)) {
        		villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, ViolentVillagers.CONFIG.chest_open_loss);
    		} else if (brain.getMemory(MemoryModuleType.JOB_SITE).isPresent() && brain.getMemory(MemoryModuleType.JOB_SITE).get().pos().closerThan(pos, ViolentVillagers.CONFIG.distance_from_site)) {
        		villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, ViolentVillagers.CONFIG.chest_open_loss);
    		} 
        });
    }

	public static class Locations {
	}
}

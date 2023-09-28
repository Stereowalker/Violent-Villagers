package com.stereowalker.violentvillagers.world.entity.ai.sensing;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.entity.ai.sensing.SensorType;

@RegistryHolder(registry = SensorType.class)
public class VSensorType {
	@RegistryObject("villager_attackables")
	public static final SensorType<VillagerAttackablesSensor> VILLAGER_ATTACKABLES = new SensorType<>(VillagerAttackablesSensor::new);
}

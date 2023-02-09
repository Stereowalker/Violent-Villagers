package com.stereowalker.violentvillagers.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class ViolentVillagersMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		boolean apply = true;
		if (mixinClassName.contains("mixin.carrier"))
		{
			try{Class.forName("me.steven.carrier.ClientUtils");}
			catch (ClassNotFoundException e)
			{apply = false;
			System.out.println("Not appliyng mixin for "+targetClassName);}
		}
		if (mixinClassName.contains("mixin.carryon"))
		{
			try{Class.forName("tschipp.carryon.Constants");}
			catch (ClassNotFoundException e)
			{apply = false;
			System.out.println("Not appliyng mixin for "+targetClassName);}
		}
		System.out.println(targetClassName+" "+mixinClassName+" "+apply);
		return apply;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
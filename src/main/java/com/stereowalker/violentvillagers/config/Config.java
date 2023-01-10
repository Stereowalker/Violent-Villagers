package com.stereowalker.violentvillagers.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(name = "violentvillagers", autoReload = true, translatableName = "gui.violent_villagers")
public class Config implements ConfigObject {
	@UnionConfig.Entry(group = "General", name = "Debug Mode")
	public boolean debug = false;
	
	@UnionConfig.Entry(group = "Reputation", name = "Reputation Loss For Opening Chest")
//	@UnionConfig.Range(max = 1, min = 10, useSlider = true)
	@UnionConfig.Comment(comment = "The amount of reputation the player losses when opening a chest in front of a villager")
	public int chest_open_loss = 5;
	
	@UnionConfig.Entry(group = "Reputation", name = "Distance From Home Or Work")
	@UnionConfig.Range(min = 1.0d, max = 20.0d, useSlider = true)
	@UnionConfig.Comment(comment = "How far the chest has to be from a villagers home or work for it to count towards reputation loss")
	public double distance_from_site = 10.0d;
}

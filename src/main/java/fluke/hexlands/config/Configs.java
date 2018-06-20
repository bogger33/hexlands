package fluke.hexlands.config;

import fluke.hexlands.util.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID, category = "")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class Configs {

	public static ConfigWorldGen worldgen = new ConfigWorldGen();
	public static ConfigDimension dimension = new ConfigDimension();

	public static class ConfigWorldGen {
		@Config.Comment({"Controls size of hex tiles. Larger number = Bigger hex", "Default: 36"})
		@Config.RequiresWorldRestart
		public int hexSize = 36;
		@Config.Comment({"Size of biomes. Lower values = Larger biomes", "Default: 600"})
		@Config.RequiresWorldRestart
		public int biomeSize = 600;
		@Config.Comment({"Height (y-level) terrain is adjusted by (aka extra terrain noise inside the hex)", "Default: 78"})
		@Config.RequiresWorldRestart
		public int terrainHeight = 78;
		@Config.Comment({"Height (y-level) terrain is before adjustments", "Default: 66"})
		@Config.RequiresWorldRestart
		public int terrainBaseline = 66;
		@Config.Comment({"How often lakes generate. Lower numbers = More lakes", "Default: 6"})
		@Config.RequiresWorldRestart
		public int lakeRarity = 6;
		@Config.Comment({"Height (y-level) of oceans", "Default: 60"})
		@Config.RequiresWorldRestart
		public int seaLevel = 60;
		@Config.Comment({"How much biomes influence the final height of the hex", "Default: 18"})
		@Config.RequiresWorldRestart
		public int biomeHeightAdjustment = 18;
		@Config.Comment({"Extra height (y-level) to adjust each hex by, best used with outlineAll setting", "Default: 0"})
		@Config.RequiresWorldRestart
		public int extraHexNoise = 0;
		@Config.Comment({"How many attempts per chunk to generate dungeons. Higher numbers = More dungeons", "Default: 8"})
		@Config.RequiresWorldRestart
		public int dungeonCount = 8;
		@Config.Comment({"How much lower to adjust ocean and deep ocean biomes", "Default: 16"})
		@Config.RequiresWorldRestart
		public int oceanHeight = 16;
		@Config.Comment({"Percent chance that a hex generates as a void", "Default: 0"})
		@Config.RequiresWorldRestart
		public int missingHexChance = 0;

		@Config.Comment({"Draw borders around every hex", "Default: false"})
		@Config.RequiresWorldRestart
		public boolean outlineAll = false;
		@Config.Comment({"Master command for generating all vanilla structures: mineshaft, village, stronghold, temples, etc", "Default: true"})
		@Config.RequiresWorldRestart
		public boolean generateStructures = true;
		@Config.Comment({"Generate mineshaft"})
		@Config.RequiresWorldRestart
		public boolean generateMineshaft = true;
		@Config.Comment({"Generate temples"})
		@Config.RequiresWorldRestart
		public boolean generateTemples = true;
		@Config.Comment({"Generate villages"})
		@Config.RequiresWorldRestart
		public boolean generateVillage = true;
		@Config.Comment({"Generate stronghold"})
		@Config.RequiresWorldRestart
		public boolean generateStronghold = true;
		@Config.Comment({"Generate ocean monuments"})
		@Config.RequiresWorldRestart
		public boolean generateMonuments = true;
		@Config.Comment({"Generate caves", "Default: true"})
		@Config.RequiresWorldRestart
		public boolean generateCaves = true;
		@Config.Comment({"Generate ravines", "Default: true"})
		@Config.RequiresWorldRestart
		public boolean generateRavines = true;
		@Config.Comment({"Generate border block down to bedrock rather than just at the surface", "Default: false"})
		@Config.RequiresWorldRestart
		public boolean borderToBedrock = false;
		@Config.Comment({"Should water lakes generate", "Default: true"})
		@Config.RequiresWorldRestart
		public boolean lakesGenerate = true;
		@Config.Comment({"Should rare lava lakes generate on the surface", "Default: true"})
		@Config.RequiresWorldRestart
		public boolean lavaLakesGenerate = true;

		@Config.RequiresWorldRestart
		@Config.Comment({"What block to use for dividing the grid, use @ for metadata", "Example: minecraft:concrete@6", "Default: minecraft:stonebrick"})
		public String rimBlock = "minecraft:stonebrick";
		//TODO figure out why this doesnt work
//		@Config.Comment({"Biomes black listed from generating (no ocean plains)", "Example: minecraft:forest", "Default:"})
//		@Config.RequiresWorldRestart
//		public String[] biomeBlacklist = {};
	}
	
	public static class ConfigDimension 
	{
		@Config.Comment({"Generate new dimension with hex-land generation", "Note: no method exists to travel to this dimension and must be added by the pack maker", "Default: false"})
		@Config.RequiresWorldRestart
		public boolean generateDim = false;
		
		@Config.Comment({"What dimension ID to use", "Default: 88"})
		@Config.RequiresWorldRestart
		public int dimID = 88;
	}

	@SubscribeEvent
	public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (Reference.MOD_ID.equals(event.getModID()))
			ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
	}
}


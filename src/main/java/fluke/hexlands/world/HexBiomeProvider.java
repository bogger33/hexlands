package fluke.hexlands.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import fluke.hexlands.config.Configs;
import fluke.hexlands.util.hex.Hex;
import fluke.hexlands.util.hex.Layout;
import fluke.hexlands.util.hex.Point;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;

public class HexBiomeProvider extends BiomeProvider
{

    protected Layout hex_layout = new Layout(Layout.flat, new Point(ChunkGeneratorOverworldHex.HEX_X_SIZE, ChunkGeneratorOverworldHex.HEX_Z_SIZE), new Point(0, 0));
    
    private GenLayer genBiomes;
    /** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
    private GenLayer biomeIndexLayer;
    /** The biome list. */
    private final BiomeCache biomeCache;
    public static final int BIOME_SIZE = Configs.biomeSize;
    
    public HexBiomeProvider(long seed, WorldType worldTypeIn)
    {
    	this.biomeCache = new BiomeCache(this);
        //this.biome = biomeIn;
        
        GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, null);
        agenlayer = getModdedBiomeGenerators(worldTypeIn, seed, agenlayer);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }
    
    public HexBiomeProvider(WorldInfo info)
    {
    	this(info.getSeed(), info.getTerrainType());
    }

    public Biome getBiome(BlockPos pos)
    {
        return this.getBiome(pos, (Biome)null);
    }

    public Biome getBiome(BlockPos pos, Biome defaultBiome)
    {
    	//convert x,z to a hex cords (q,r)
        Hex hexy = hex_layout.pixelToHex(new Point(pos.getX(), pos.getZ())).hexRound();
        
        //convert hex cords back to x,z to get center point
        Point center_pt =  hex_layout.hexToPixel(hexy);
        return this.biomeCache.getBiome(center_pt.getX(), center_pt.getZ(), defaultBiome);
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public Biome[] getBiomesForGeneration(Biome[] biomes, int chunkX, int chunkZ, int width, int height)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        //int[] aint = this.genBiomes.getInts(x, z, width, height);

        try
        {
        	Hex prev_hex = hex_layout.pixelToHex(new Point(chunkX-1000, chunkZ-1000)).hexRound();
        	Biome prev_biome = Biomes.DEFAULT;
            for (int i = 0; i < width * height; ++i)
            {
            	int realX = (i%width)+chunkX;
            	int realZ = (int)(i/height)+chunkZ;
            	Hex hexy = hex_layout.pixelToHex(new Point(realX, realZ)).hexRound();
            	
            	if (hexy.q != prev_hex.q || hexy.r != prev_hex.r)
            	{
            		prev_biome = Biome.getBiome(this.biomeIndexLayer.getInts(hexy.q*BIOME_SIZE, hexy.r*BIOME_SIZE, 1, 1)[0], Biomes.DEFAULT);
            		prev_hex = hexy;
            	}
            	
            	biomes[i] = prev_biome;
            }

            return biomes;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
            crashreportcategory.addCrashSection("x", Integer.valueOf(chunkX));
            crashreportcategory.addCrashSection("z", Integer.valueOf(chunkZ));
            crashreportcategory.addCrashSection("w", Integer.valueOf(width));
            crashreportcategory.addCrashSection("h", Integer.valueOf(height));
            throw new ReportedException(crashreport);
        }
    }
    
    /**
     * Gets biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager.
     */
    public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomes(oldBiomeList, x, z, width, depth, true);
    }

    /**
     * Gets a list of biomes for the specified blocks.
     */
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int chunkX, int chunkZ, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (chunkX & 15) == 0 && (chunkZ & 15) == 0)
        {
            Biome[] abiome = this.biomeCache.getCachedBiomes(chunkX, chunkZ);
            System.arraycopy(abiome, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            //int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);
        	
        	Hex prev_hex = hex_layout.pixelToHex(new Point(chunkX-1000, chunkZ-1000)).hexRound();
        	Biome prev_biome = Biomes.DEFAULT;
            for (int i = 0; i < width * length; ++i)
            {
            	//get the hex cords of the current point
            	int realX = (i%width)+chunkX;
            	int realZ = (int)(i/length)+chunkZ;
            	Hex hexy = hex_layout.pixelToHex(new Point(realX, realZ)).hexRound();
            	
            	//if this hex has different cords from the last hex
            	if (hexy.q != prev_hex.q || hexy.r != prev_hex.r)
            	{	//get a new biome based on current hex cords
            		prev_biome = Biome.getBiome(this.biomeIndexLayer.getInts(hexy.q*600, hexy.r*600, 1, 1)[0], Biomes.DEFAULT);
            		prev_hex = hexy;
            	}
            	
                listToReuse[i] = prev_biome;
            }

            return listToReuse;
        }
    }

    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        IntCache.resetIntCache();
        int xStart = x - range >> 2;
        int zStart = z - range >> 2;
        int xEnd = x + range >> 2;
        int zEnd = z + range >> 2;
        int xRange = xEnd - xStart + 1;
        int zRange = zEnd - zStart + 1;
        
        //int[] aint = this.genBiomes.getInts(xStart, zStart, xRange, zRange);
        Biome[] biomeList = getBiomes(null, xStart, zStart, xRange, zRange);

        BlockPos blockpos = null;
        int k1 = 0;

        for (int l1 = 0; l1 < xRange * zRange; ++l1)
        {
            int i2 = xStart + l1 % xRange << 2;
            int j2 = zStart + l1 / xRange << 2;
            Biome biome = biomeList[l1];

            if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0))
            {
                blockpos = new BlockPos(i2, 0, j2);
                ++k1;
            }
        }

        return blockpos;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(int x, int z, int range, List<Biome> allowed)
    {
    	IntCache.resetIntCache();
        int xStart = x - range >> 2;
        int zStart = z - range >> 2;
        int xEnd = x + range >> 2;
        int zEnd = z + range >> 2;
        int xRange = xEnd - xStart + 1;
        int zRange = zEnd - zStart + 1;
        
        //int[] aint = this.genBiomes.getInts(xStart, zStart, xRange, zRange);
        Biome[] biomeList = getBiomes(null, xStart, zStart, xRange, zRange);

        try
        {
            for (int k1 = 0; k1 < xRange * zRange; ++k1)
            {
                Biome biome = biomeList[k1];

                if (!allowed.contains(biome))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("radius", Integer.valueOf(range));
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

    public boolean isFixedBiome()
    {
        return false;
    }

    public Biome getFixedBiome()
    {
        return null;
    }
}
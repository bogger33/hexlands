package fluke.hexlands.util.hex;

import java.util.ArrayList;
import java.util.Random;

import fluke.hexlands.config.Configs;

public class Hex
{
	//magic hex numbers so I can avoid calling sqrt function a bunch
	public static final double sqr3 = 1.732050808;
	public static final double sqr3o2 = sqr3/2;
	
    public Hex(int q, int r, int s)
    {
        this.q = q;
        this.r = r;
        this.s = s;
        if (q + r + s != 0) throw new IllegalArgumentException("q + r + s must be 0");
    }
    
    public Hex(int q, int r)
    {
        this.q = q;
        this.r = r;
        this.s = -q - r;
    }
    
    public final int q;
    public final int r;
    public final int s;

    public Hex add(Hex b)
    {
        return new Hex(q + b.q, r + b.r, s + b.s);
    }


    public Hex subtract(Hex b)
    {
        return new Hex(q - b.q, r - b.r, s - b.s);
    }


    public Hex scale(int k)
    {
        return new Hex(q * k, r * k, s * k);
    }


    public Hex rotateLeft()
    {
        return new Hex(-s, -q, -r);
    }


    public Hex rotateRight()
    {
        return new Hex(-r, -s, -q);
    }
    
    //SE, NE, N, NW, SW, S
    static public ArrayList<Hex> directions = new ArrayList<Hex>(){{add(new Hex(1, 0, -1)); add(new Hex(1, -1, 0)); add(new Hex(0, -1, 1)); add(new Hex(-1, 0, 1)); add(new Hex(-1, 1, 0)); add(new Hex(0, 1, -1));}};

    static public Hex direction(int direction)
    {
        return Hex.directions.get(direction);
    }


    public Hex neighbor(int direction)
    {
        return add(Hex.direction(direction));
    }

    static public ArrayList<Hex> diagonals = new ArrayList<Hex>(){{add(new Hex(2, -1, -1)); add(new Hex(1, -2, 1)); add(new Hex(-1, -1, 2)); add(new Hex(-2, 1, 1)); add(new Hex(-1, 2, -1)); add(new Hex(1, 1, -2));}};

    public Hex diagonalNeighbor(int direction)
    {
        return add(Hex.diagonals.get(direction));
    }


    public int length()
    {
        return (int)((Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2);
    }


    public int distance(Hex b)
    {
        return subtract(b).length();
    }
    
    public static void setupHexSeed(long worldSeed, Random rand, int q, int r)
    {
    	//System.out.println(worldSeed);
    	rand.setSeed(worldSeed);
        long i = rand.nextLong();
        long j = rand.nextLong();
        long k = (long)q * i;
        long l = (long)r * j;
        rand.setSeed(k ^ l ^ worldSeed);
    }
    
    public static boolean isHexVoid(long worldSeed, Random rand, int q, int r)
    {
    	Hex.setupHexSeed(worldSeed, rand, q, r);
    	int voidRand = rand.nextInt(100);
    	return voidRand < Configs.worldgen.missingHexChance;
    }

}
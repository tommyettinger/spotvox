package com.github.tommyettinger.io;

public class ShapeModel extends AbstractChunk {
    public int minX = Integer.MAX_VALUE;
    public int maxX;
    public int minY = Integer.MAX_VALUE;
    public int maxY;
    public int minZ = Integer.MAX_VALUE;
    public int maxZ;
    public int offsetX;
    public int offsetY;
    public int offsetZ;
    public ShapeModel(){
        attributes = new String[0][0];
    }
    public ShapeModel(int id, String[][] attributes){
        this.id = id;
        this.attributes = attributes == null ? new String[0][0] : attributes;
    }
}

package com.github.tommyettinger.io;

public class GroupChunk extends AbstractChunk {
    /**
     * Each childId refers to a ShapeChunk, by index.
     */
    public int[] childIds;

    public GroupChunk() {
        attributes = new String[0][0];
        childIds = new int[0];
    }

    public GroupChunk(int id, String[][] attributes, int[] childIds) {
        this.id = id;
        this.attributes = attributes;
        this.childIds = childIds;
    }
}

package com.github.tommyettinger.io;

/**
 * The parent class for the more-complex node chunks that can exist in recent .vox files.
 * Only stores an id for this chunk and an array of pairs of Strings for attributes.
 */
public abstract class AbstractChunk {
    /**
     * An ID uses to reference this chunk from other chunks.
     */
    public int id;
    /**
     * Meant to store an array of pairs of Strings, as in {@code chunk.attributes = new String[size][2];} .
     */
    public String[][] attributes;
}

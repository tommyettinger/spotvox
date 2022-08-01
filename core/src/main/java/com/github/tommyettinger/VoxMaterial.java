package com.github.tommyettinger;

import com.github.tommyettinger.ds.IntFloatMap;
import com.github.tommyettinger.ds.ObjectIntMap;

/**
 * Represents the physical qualities of a given material, like how reflective it is or how much light it emits.
 * Each material is associated with one palette index.
 * <br>
 * Created by Tommy Ettinger on 8/17/2020.
 */
public class VoxMaterial {
	public enum MaterialType {
		//0
		_diffuse("Diffuse"),
		//1
		_metal("Metal"),
		//2
		_emit("Emit"),
		//3
		_glass("Glass"),
		//4
		_blend("Blend"),
		//5
		_media("Cloud");
		public String name;
		MaterialType(String name){
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum MaterialTrait {
		//0 lower is more opaque, higher is more transparent
		_alpha("Transparency"),
		//1 currently unused
		_d("Density"),
		//2 brightness of light emitted
		_emit("Emission"),
		//3 for emissive materials, this is called "strength," here it may get used for lighting range?
		_flux("Flux"),
		//4 affects scatter media? unused
		_g("Phase"),
		//5 affects extra lightness applied when both the front and top are lit
		_ior("Reflection"),
		//6 unused
		_ldr("LDR"),
		//7 not related to the "Cloud" _media MaterialType; unused
		_media("Special"),
		//8 unused
		_metal("Metal"),
		//9 determines how much lighting affects the color of a surface
		_rough("Roughness"),
		//10 used in newer MV instead of a separate MaterialType
		_type("Type")
		;
		
		public String name;
		MaterialTrait(String name){
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	public static final MaterialType[] ALL_TYPES = MaterialType.values();
	public static final MaterialTrait[] ALL_TRAITS = MaterialTrait.values();

	public static final ObjectIntMap<String> TYPE_MAP = new ObjectIntMap<>(6), TRAIT_MAP = new ObjectIntMap<>(10);

	static {
		for(MaterialType t : ALL_TYPES) {
			TYPE_MAP.put(t.name, t.ordinal());
		}
		for(MaterialTrait t : ALL_TRAITS) {
			TRAIT_MAP.put(t.name, t.ordinal());
		}
	}
	public MaterialType type;
	public final IntFloatMap traits = new IntFloatMap(16);
	
	public VoxMaterial(){
		type = MaterialType._diffuse;
		traits.put(9, 0.1f);
		traits.put(5, 0.3f);
		traits.put(4, 0.41f);
	}

	public VoxMaterial(String traitMap) {
		traits.put(9, 0.1f);
		traits.put(5, 0.3f);
		traits.put(4, 0.41f);
		String[] split = traitMap.split("[ ,;]+");
		for (int i = 1; i < (split.length & -2); i+=2) {
			int trait = TRAIT_MAP.getOrDefault(split[i-1], -1);
			if(trait == 10) // _type
			{
				int t = TRAIT_MAP.getOrDefault(split[i], 0);
				type = ALL_TYPES[t];
				traits.put(trait, t);
			}
			else
				traits.put(trait, Float.parseFloat(split[i]));
		}
	}

	public float getTrait(MaterialTrait trait){
		return traits.getOrDefault(trait.ordinal(), 0.0f);
	}
	public void putTrait(MaterialTrait trait, float value){
		int ord = trait.ordinal();
		traits.put(ord, value);
	}
	public void putTrait(String trait, String value) {
		try {
			int ord = MaterialTrait.valueOf(trait).ordinal();
			if ("_type".equals(trait)) {
				int t = MaterialType.valueOf(value).ordinal();
				traits.put(ord, t);
				type = ALL_TYPES[t];
			} else {
				traits.put(ord, Float.parseFloat(value));
			}
		}catch (IllegalArgumentException ignored){
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("VoxMaterial{").append(type.name).append(": ");
		for(IntFloatMap.Entry e : traits.entrySet())
			sb.append(ALL_TRAITS[e.key]).append('=').append(e.value).append(", ");
		sb.setLength(sb.length() - 1);
		sb.setCharAt(sb.length() - 1, '}');
		return sb.toString();
	}

	public static final VoxMaterial DEFAULT_MATERIAL = new VoxMaterial();
}

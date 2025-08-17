package eu.pb4.slingshot.block;

import eu.pb4.slingshot.ModInit;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SlingshotBlockTags {
    public static final TagKey<Block> BRICK_BREAKABLE = of("brick_breakable");
    private static TagKey<Block> of(String path) {
        return TagKey.of(RegistryKeys.BLOCK, ModInit.id(path));
    }
}

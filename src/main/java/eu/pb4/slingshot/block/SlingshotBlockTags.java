package eu.pb4.slingshot.block;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SlingshotBlockTags {
    public static final TagKey<Block> BRICK_BREAKABLE = of("brick_breakable");
    private static TagKey<Block> of(String path) {
        return TagKey.create(Registries.BLOCK, ModInit.id(path));
    }
}

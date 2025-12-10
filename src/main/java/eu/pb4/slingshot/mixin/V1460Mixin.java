package eu.pb4.slingshot.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

@Mixin(V1460.class)
public abstract class V1460Mixin extends Schema {
    @Shadow protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {};

    @Shadow
    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
    }

    public V1460Mixin(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Inject(method = "registerEntities", at = @At("RETURN"))
    private void registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();
        schema.register(map, mod("item_projectile"), () -> DSL.allWithRemainder(
                DSL.optionalFields("stack", References.ITEM_STACK.in(schema)),
                DSL.optionalFields("weapon", References.ITEM_STACK.in(schema))
        ));

        schema.register(map, mod("fake_projectile"), () -> DSL.allWithRemainder(
                DSL.optionalFields("entity", References.ENTITY.in(schema))
        ));
    }

    @Unique
    private static String mod(String path) {
        return "slingshot:" + path;
    }
}
package eu.pb4.slingshot;


import eu.pb4.slingshot.entity.SlingshotEntities;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.item.SlingshotItems;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import eu.pb4.slingshot.util.SlingshotSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModInit implements ModInitializer {
	public static final String MOD_ID = "simply-slingshot";
	public static final String ID = "slingshot";
	public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
	public static final Logger LOGGER = LoggerFactory.getLogger("Simply Slingshot!");
    public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final boolean DEV_MODE = VERSION.contains("-dev.") || DEV_ENV;
    @SuppressWarnings("PointlessBooleanExpression")
	public static final boolean DYNAMIC_ASSETS = true && DEV_ENV;

    public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}

	@Override
	public void onInitialize() {
		if (VERSION.contains("-dev.")) {
			LOGGER.warn("=====================================================");
			LOGGER.warn("You are using development version of Simply Slingshot!");
			LOGGER.warn("Support is limited, as features might be unfinished!");
			LOGGER.warn("You are on your own!");
			LOGGER.warn("=====================================================");
		}

		SlingshotItems.register();
		SlingshotEntities.register();
		SlingshotEnchantmentComponents.register();
		SlingshotEnchantments.register();
		SlingshotSoundEvents.register();

		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

	}
}

package voxelum.sentry;

import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Sentry.MODID)
public class Sentry {
    public static final String MODID = "hurtcore";

    public static final String BANDAGE_ITEM_NAME = "bandage";
    public static final String BLEEDING_EFFECT_NAME = "bleeding";
    public static final String BROKEN_LEG_EFFECT_NAME = "broken_leg";

    private static DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static DeferredRegister<Effect> EFFECTS = new DeferredRegister<>(ForgeRegistries.POTIONS, MODID);

//    public static final RegistryObject<Item> BANDAGE_ITEM = ITEMS.register(BANDAGE_ITEM_NAME,
//            () -> new BandageItem(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(16)));
//
//    public static final RegistryObject<Effect> BLEEDING_EFFECT = EFFECTS.register(BLEEDING_EFFECT_NAME, BleedEffect::new);
//    public static final RegistryObject<Effect> BROKEN_LEG_EFFECT = EFFECTS.register(BROKEN_LEG_EFFECT_NAME,BrokenLegEffect::new);
    public Sentry() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
//        bus.addListener(this::onInit);
//        bus.addListener(this::gatherData);
//        ITEMS.register(bus);
//        EFFECTS.register(bus);
    }

//    public void onInit(FMLCommonSetupEvent event) {
//        FuckMyHand.register();
//    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterModel(ModelRegistryEvent registryEvent) {

    }
}

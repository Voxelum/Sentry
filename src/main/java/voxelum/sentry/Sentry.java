package voxelum.sentry;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Sentry.MODID)
public class Sentry {
    public static final String MODID = "sentry";

    public static final String SENTRY_SUPP_BLOCK_NAME = "sentry_supp_block";
    public static final String SENTRY_BASE_BLOCK_NAME = "sentry_base_block";

    private static DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static DeferredRegister<Effect> EFFECTS = new DeferredRegister<>(ForgeRegistries.POTIONS, MODID);
    private static DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static DeferredRegister<TileEntityType<?>> TILE = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);


    public static final RegistryObject<Block> SENTRY_BASE_BLOCK = BLOCKS.register(SENTRY_BASE_BLOCK_NAME, () -> new SentryBaseBlock(Block.Properties
            .create(Material.WOOD)
            .hardnessAndResistance(2F, 6F)
            .sound(SoundType.WOOD).noDrops()));
    public static final RegistryObject<Item> SENTRY_BASE_BLOCK_ITEM = ITEMS.register(SENTRY_BASE_BLOCK_NAME, () -> new BlockItem(SENTRY_BASE_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));

    public static final RegistryObject<Block> SENTRY_SUPP_BLOCK = BLOCKS.register(SENTRY_SUPP_BLOCK_NAME, () -> new SentrySuppBlock(Block.Properties.create(Material.WOOD)
            .hardnessAndResistance(2.0F, 6.0F)
            .sound(SoundType.WOOD).noDrops()));
    public static final RegistryObject<Item> SENTRY_SUPP_BLOCK_ITEM = ITEMS.register(SENTRY_SUPP_BLOCK_NAME, () -> new BlockItem(SENTRY_SUPP_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
//    public static final RegistryObject<TileEntityType<SentryBaseTileEntity>> SENTRY_BASE_TILE_ENTITY = TILE.register("sentry_base_tile_entity",
//            () -> TileEntityType.Builder.create(SentryBaseTileEntity::new, SENTRY_BASE_BLOCK.get())
//                    .build(DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, "sentry_base_tile_entity")));

    //
//    public static final RegistryObject<Effect> BLEEDING_EFFECT = EFFECTS.register(BLEEDING_EFFECT_NAME, BleedEffect::new);
//    public static final RegistryObject<Effect> BROKEN_LEG_EFFECT = EFFECTS.register(BROKEN_LEG_EFFECT_NAME,BrokenLegEffect::new);
    public Sentry() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
//        bus.addListener(this::onInit);
//        bus.addListener(this::gatherData);
        ITEMS.register(bus);
        EFFECTS.register(bus);
        BLOCKS.register(bus);
        TILE.register(bus);
    }

//    public void onInit(FMLCommonSetupEvent event) {
//        FuckMyHand.register();
//    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterModel(ModelRegistryEvent registryEvent) {

    }
}

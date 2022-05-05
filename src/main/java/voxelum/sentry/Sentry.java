package voxelum.sentry;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voxelum.sentry.block.SentryBaseBlock;
import voxelum.sentry.block.SentryShooterBlock;
import voxelum.sentry.block.SentrySuppBlock;
import voxelum.sentry.container.SentryBaseContainer;
import voxelum.sentry.screen.SentryBaseScreen;
import voxelum.sentry.tileentity.SentryBaseTileEntity;
import voxelum.sentry.tileentity.SentryShooterTileEntity;

@Mod(Sentry.MODID)
public class Sentry {
    public static final String MODID = "sentry";

    private static final DeferredRegister<Item> ITEMS =DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS =DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILE =DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINER =DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static final String SENTRY_SUPP_BLOCK_NAME = "sentry_supp_block";
    public static final String SENTRY_BASE_BLOCK_NAME = "sentry_base_block";
    public static final String SENTRY_SHOOTER_BLOCK_NAME = "sentry_shooter_block";

    public static final RegistryObject<Block> SENTRY_BASE_BLOCK = BLOCKS.register(SENTRY_BASE_BLOCK_NAME,
            () -> new SentryBaseBlock(Block.Properties
                    .of(Material.WOOD)
                    .explosionResistance(6F)
                    .destroyTime(2f)
                    .sound(SoundType.WOOD)
                    .);
    public static final RegistryObject<Item> SENTRY_BASE_BLOCK_ITEM = ITEMS.register(SENTRY_BASE_BLOCK_NAME,
            () -> new BlockItem(SENTRY_BASE_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<TileEntityType<SentryBaseTileEntity>> SENTRY_BASE_TILE_ENTITY = TILE.register("sentry_base_tile_entity",
            () -> TileEntityType.Builder.create(SentryBaseTileEntity::new, SENTRY_BASE_BLOCK.get())
                    .build(null));
    public static final RegistryObject<ContainerType<SentryBaseContainer>> SENTRY_BASE_CONTAINER = CONTAINER.register("sentry_base_container",
            () -> new ContainerType<>(SentryBaseContainer::new));

    public static final RegistryObject<Block> SENTRY_SUPP_BLOCK = BLOCKS.register(SENTRY_SUPP_BLOCK_NAME,
            () -> new SentrySuppBlock(Block.Properties.create(Material.WOOD)
                    .tickRandomly()
                    .hardnessAndResistance(2.0F, 6.0F)
                    .sound(SoundType.WOOD)));
    public static final RegistryObject<Item> SENTRY_SUPP_BLOCK_ITEM = ITEMS.register(SENTRY_SUPP_BLOCK_NAME,
            () -> new BlockItem(SENTRY_SUPP_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));

    public static final RegistryObject<Block> SENTRY_SHOOTER_BLOCK = BLOCKS.register(SENTRY_SHOOTER_BLOCK_NAME,
            () -> new SentryShooterBlock(Block.Properties.create(Material.WOOD)
                    .tickRandomly()
                    .hardnessAndResistance(2.0F, 6.0F)
                    .sound(SoundType.WOOD)));
    public static final RegistryObject<Item> SENTRY_SHOOTER_BLOCK_ITEM = ITEMS.register(SENTRY_SHOOTER_BLOCK_NAME,
            () -> new BlockItem(SENTRY_SHOOTER_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<TileEntityType<SentryShooterTileEntity>> SENTRY_SHOOTER_TILE_ENTITY = TILE.register("sentry_shooter_tile_entity",
            () -> TileEntityType.Builder.create(SentryShooterTileEntity::new, SENTRY_SHOOTER_BLOCK.get())
                    .build(null));

    public Sentry() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
        bus.addListener(this::onInit);
        ITEMS.register(bus);
        EFFECTS.register(bus);
        BLOCKS.register(bus);
        TILE.register(bus);
        CONTAINER.register(bus);
    }

    public void onInit(FMLCommonSetupEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ScreenManager.registerFactory(SENTRY_BASE_CONTAINER.get(), SentryBaseScreen::new));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterModel(ModelRegistryEvent registryEvent) {

    }
}

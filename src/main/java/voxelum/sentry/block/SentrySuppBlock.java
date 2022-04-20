package voxelum.sentry.block;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemStackHandler;
import voxelum.sentry.Sentry;
import voxelum.sentry.tileentity.SentryShooterTileEntity;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SentrySuppBlock extends Block {
    public SentrySuppBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape MY_SHAPE;


    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos down = pos.down();
        if (worldIn.getBlockState(down).getBlock() == Sentry.SENTRY_BASE_BLOCK.get()) {
            return true;
        }
        return false;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SentrySuppBlock.MY_SHAPE;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if (!worldIn.isRemote) {
            ServerWorld serverWorld = (ServerWorld) worldIn;
            MinecraftServer server = serverWorld.getServer();
            if (worldIn.getTileEntity(pos.up()) != null) {
                SentryShooterTileEntity tileEntity = (SentryShooterTileEntity) worldIn.getTileEntity(pos.up());
                if (tileEntity.getPlacer() == null) {
                    return;
                } else {
                    PlayerEntity placer = tileEntity.getPlacer();
                    ItemStackHandler handler = new ItemStackHandler();
                    ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
                    if (player.getUniqueID() == placer.getUniqueID() && heldItem.getItem() instanceof WritableBookItem) {
                        List<SentryShooterTileEntity.PlayerInfo> whiteList = tileEntity.getWhiteList();
                        CompoundNBT tag = heldItem.getTag();
                        if (tag != null) {
                            byte resolved = tag.getByte("resolved");
                            if (resolved == 0) {
                                ListNBT pages = tag.getList("pages", 8);
                                Stream<String> newList = pages.stream().map(p -> Arrays.stream(p.getString().split("\n")).filter(v -> v.length() > 0))
                                        .reduce(Stream::concat)
                                        .orElse(Stream.empty());
                                Stream<SentryShooterTileEntity.PlayerInfo> oldList = whiteList.stream();
                                newList
                                        .filter((name) -> oldList.noneMatch(v -> Objects.equals(v.name, name)))
                                        .map((name) -> server.getPlayerList().getPlayerByUsername(name))
                                        .filter(Objects::nonNull)
                                        .forEach(tileEntity::addPlayerToWhiteList);
                                oldList
                                        .filter((info) -> newList.noneMatch(v -> Objects.equals(v, info.name)))
                                        .forEach(tileEntity::removePlayerFromWhiteList);
                            } else {

                            }
                        }
                    }
                }
            }
        }
        super.onBlockClicked(state, worldIn, pos, player);
    }

//    @Override
//    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        if (worldIn.getTileEntity(pos.up()) != null) {
//            SentryShooterTileEntity tileEntity = (SentryShooterTileEntity) worldIn.getTileEntity(pos.up());
//
//            if (tileEntity.getPlacer() == null) {
//                return ActionResultType.FAIL;
//            } else {
//                PlayerEntity placer = tileEntity.getPlacer();
//                ItemStackHandler handler = new ItemStackHandler();
//                ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
//                if (player.getUniqueID() == placer.getUniqueID() && heldItem.getItem() instanceof WrittenBookItem) {
//                    handler.extractItem(EquipmentSlotType.MAINHAND.getSlotIndex(), 1, true);
//                    CompoundNBT tag = heldItem.getTag();
//                    if (tag != null) {
//                        byte resolved = tag.getByte("resolved");
//                        if (resolved == 1) {
//                            ListNBT pages = tag.getList("pages", 8);
//                            for (INBT page : pages) {
//                                String content = page.getString();
//                                System.out.println(content);
//                                try {
//                                    IFormattableTextComponent component = ITextComponent.Serializer.getComponentFromJsonLenient(content);
//                                    component.getString();
//                                    System.out.println(component);
//                                } catch (Exception e) {
//                                    //2313123123123123
//                                }
//                            }
//                        } else {
//                            tag.putString("title", "WTF");
//                            tag.putString("author", "SHIT");
//                        }
//                    }
//                }
//            }
//        }
//
//        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
//    }

    static {
        final VoxelShape voxelShape1 = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
        final VoxelShape voxelShape2 = Block.makeCuboidShape(4.0, 3.0, 4.0, 12.0, 16.0, 12.0);
        MY_SHAPE = VoxelShapes.or(voxelShape1, voxelShape2);
    }
}

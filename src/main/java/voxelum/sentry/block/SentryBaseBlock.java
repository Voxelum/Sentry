package voxelum.sentry.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;
import voxelum.sentry.Sentry;
import voxelum.sentry.container.SentryBaseContainer;
import voxelum.sentry.tileentity.SentryBaseTileEntity;

import javax.annotation.Nullable;

public class SentryBaseBlock extends BlockContainer {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public SentryBaseBlock(Material material) {
        super(material);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand handIn, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.getHeldItem(handIn).getItem() == Sentry.SENTRY_SUPP_BLOCK_ITEM.get()) {
            return false;
        }
        if (!worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof SentryBaseTileEntity) {
//                player.openContainer((INamedContainerProvider) tileentity);
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof SentryBaseTileEntity) {
//                ((SentryBaseTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }


//    @Override
//    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (state.getBlock() != newState.getBlock()) {
//            TileEntity tileentity = worldIn.getTileEntity(pos);
//            if (tileentity instanceof SentryBaseTileEntity) {
////                InventoryHelper.dropInventoryItems(worldIn, pos, (SentryBaseTileEntity) tileentity);
//                worldIn.updateComparatorOutputLevel(pos, this);
//            }
//            super.onReplaced(state, worldIn, pos, newState, isMoving);
//        }
//    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SentryBaseTileEntity();
    }
}

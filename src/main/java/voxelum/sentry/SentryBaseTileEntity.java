package voxelum.sentry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SentryBaseTileEntity extends TileEntity {
    private ItemStackHandler inventory = new ItemStackHandler(3);

    public SentryBaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

//    public SentryBaseTileEntity() {
//        super(Sentry.SENTRY_BASE_TILE_ENTITY.get());
//    }

    @Override
    public CompoundNBT serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        inventory.deserializeNBT(nbt);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) LazyOptional.of(this::getInventory);
        }
        return super.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) LazyOptional.of(this::getInventory);
        }
        return super.getCapability(cap, side);
    }
}

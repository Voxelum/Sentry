package voxelum.sentry.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import voxelum.sentry.ItemStackSentryBaseHandler;
import voxelum.sentry.Sentry;
import voxelum.sentry.container.SentryBaseContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SentryBaseTileEntity extends TileEntity implements INamedContainerProvider {
    private ItemStackSentryBaseHandler inventory = new ItemStackSentryBaseHandler(3);

    public SentryBaseTileEntity() {
        super(Sentry.SENTRY_BASE_TILE_ENTITY.get());
    }

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

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.sentry_base");
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new SentryBaseContainer(id, playerInventory, this.inventory);
    }
}

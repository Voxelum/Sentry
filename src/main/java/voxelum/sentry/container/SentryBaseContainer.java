package voxelum.sentry.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import voxelum.sentry.ItemStackSentryBaseHandler;
import voxelum.sentry.Sentry;

public class SentryBaseContainer extends Container {
    private IItemHandlerModifiable inv;

    public SentryBaseContainer(int id, PlayerInventory inventory) {
        this(id, inventory, new ItemStackSentryBaseHandler(3));
    }

    public SentryBaseContainer(int id, PlayerInventory inventory, IItemHandlerModifiable itemHandlerModifiable) {
        super(Sentry.SENTRY_BASE_CONTAINER.get(), id);
        this.inv = itemHandlerModifiable;

        for (int j = 0; j < 3; ++j) {
            this.addSlot(new SlotItemHandler(inv, j, 44 + 18 + j * 18, 20));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 109));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index < 3) {
                if (!this.mergeItemStack(stack, 3, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}

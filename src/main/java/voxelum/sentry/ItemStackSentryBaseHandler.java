package voxelum.sentry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackSentryBaseHandler extends ItemStackHandler {
    public ItemStackSentryBaseHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Item item = stack.getItem().getItem();
        return item == Items.ARROW
                || item == Items.SPECTRAL_ARROW;
    }
}

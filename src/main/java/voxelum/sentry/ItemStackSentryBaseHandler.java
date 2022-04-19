package voxelum.sentry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Set;

public class ItemStackSentryBaseHandler extends ItemStackHandler {
    public ItemStackSentryBaseHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Item item = stack.getItem().getItem();
        Set<ResourceLocation> tags = item.getTags();
        ResourceLocation arrows = new ResourceLocation("arrows");
        for(ResourceLocation tag:tags){
            if(tag.equals(arrows)){
                return true;
            }
        }
        return false;
    }
}

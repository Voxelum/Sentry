package voxelum.sentry;

import net.minecraft.item.Item;

public class Stone extends Item {
    public Stone(Properties properties) {
        super(properties);
    }

    public enum SentryType {
        ARROW,
        MAGIC,
        LASER;
    }
}

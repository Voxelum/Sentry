package voxelum.sentry.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import voxelum.sentry.Sentry;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class SentryShooterTileEntity extends TileEntity implements ITickableTileEntity {
    private static ThreadLocal<SentryShooterTileEntity> lastTile = new ThreadLocal<>();

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld().isRemote()) return;
        if (event.getPlacedBlock().getBlock() != Sentry.SENTRY_SHOOTER_BLOCK.get()) return;
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            SentryShooterTileEntity sentryShooterTileEntity = lastTile.get();
            sentryShooterTileEntity.placerId = playerEntity.getUniqueID();
            sentryShooterTileEntity.placer = playerEntity;
        }
        lastTile.set(null);
    }

    private LivingEntity target;
    private int attackTick = 0;
    private int updateTargetTick = 0;
    private AxisAlignedBB bb;
    private UUID placerId;
    private PlayerEntity placer;

    public SentryShooterTileEntity() {
        super(Sentry.SENTRY_SHOOTER_TILE_ENTITY.get());
        lastTile.set(this);
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        bb = new AxisAlignedBB(pos.getX() - 10, pos.getY() - 3, pos.getZ() - 10, pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10);
    }

    @Override
    public void tick() {
        if (this.world.isRemote) return;
        if (shouldUpdateTarget()) {
            this.updateTarget();
        }
        if (shouldAttack()) {
            attackTarget();
        }
    }

    private void attackTarget() {
        TileEntity tileEntity = world.getTileEntity(this.pos.down(2));
        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(EmptyHandler.INSTANCE);
        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 0.5;
        double z = this.pos.getZ() + 0.5;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack result = handler.extractItem(i, 1, true);
            if (result.getItem() instanceof ArrowItem) {
                handler.extractItem(i, 1, false);
                Vec3d vec3d = new Vec3d(this.target.posX - x, this.target.posY + this.target.getEyeHeight() - y, this.target.posZ - z).normalize();
                ArrowEntity arrowEntity = new ArrowEntity(world, x + vec3d.x, y + vec3d.y, z + vec3d.z);
                arrowEntity.shoot(vec3d.x, vec3d.y, vec3d.z, 3, 1);
                world.addEntity(arrowEntity);
                break;
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (this.placerId != null) {
            compound.put("placer", NBTUtil.writeUniqueId(this.placerId));
        }
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.placerId = NBTUtil.readUniqueId(compound.getCompound("placer"));
    }

    private boolean shouldAttack() {
        attackTick++;
        if (attackTick > 20) {
            attackTick = 0;
            return target != null;
        }
        return false;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity instanceof MonsterEntity) {
            return true;
        }
        return false;
    }

    private boolean shouldUpdateTarget() {
        updateTargetTick++;
        if (updateTargetTick > 20) {
            updateTargetTick = 0;
            return true;
        }
        return false;
    }

    private void checkPlacer() {
        if (placer != null && placer.removed) {
            placer = null;
        }

        if (placer == null && this.placerId != null) {
            this.placer = world.getPlayerByUuid(this.placerId);
        }
    }

    private void updateTarget() {
        World world = this.world;
        BlockPos pos = this.pos;
        this.checkPlacer();
        List<LivingEntity> entityList = (List) world.getEntitiesInAABBexcluding(this.placer, bb, this::isValidTarget);
        EntityPredicate predicate = new EntityPredicate();
        this.target = world.getClosestEntity(entityList, predicate, this.placer, pos.getX(), pos.getY(), pos.getZ());
    }
}

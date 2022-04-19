package voxelum.sentry.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.lwjgl.system.CallbackI;
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
        bb = new AxisAlignedBB(pos.getX() - 12, pos.getY() - 3, pos.getZ() - 12, pos.getX() + 12, pos.getY() + 10, pos.getZ() + 12);
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        bb = new AxisAlignedBB(pos.getX() - 12, pos.getY() - 3, pos.getZ() - 12, pos.getX() + 12, pos.getY() + 10, pos.getZ() + 12);
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
            Vector3d direction = new Vector3d(this.target.getPosX() - x, this.target.getPosY() - y, this.target.getPosZ() - z);
            direction = direction.normalize();
            ItemStack result = handler.extractItem(i, 1, true);
            if (result.getItem() instanceof ArrowItem) {
                handler.extractItem(i, 1, false);
                ArrowItem arrow = (ArrowItem) result.getItem();
                AbstractArrowEntity entity = arrow.createArrow(this.world, result.getStack(), this.placer);
                entity.setNoGravity(true);
                entity.setPosition(x + direction.x, y + direction.y, z + direction.z);
                entity.shoot(direction.x, direction.y, direction.z, 3, 1);
                this.world.addEntity(entity);
                break;
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.placerId != null) {
            compound.put("placer", NBTUtil.func_240626_a_(this.placerId));
        }
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if (compound.hasUniqueId("placer")) {
            this.placerId = compound.getUniqueId("placer");

        }
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
        return entity instanceof MonsterEntity || entity instanceof SlimeEntity || (!entity.isAlive() && false);
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
        if (placer != null && !placer.isAlive()) {
            placer = null;
        }

        if (placer == null && this.placerId != null) {
            this.placer = world.getPlayerByUuid(this.placerId);
        }
    }

    private void updateTarget() {
        World world = this.world;
        this.checkPlacer();
        if (this.bb == null) {
            return;
        }
        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 0.5;
        double z = this.pos.getZ() + 0.5;
        List<LivingEntity> entityList = (List) world.getEntitiesInAABBexcluding(placer, bb, this::isValidTarget);
        entityList.sort((a, b) -> (int) (b.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) - a.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ())));
        for (LivingEntity entity : entityList) {
            Vector3d vec3d = new Vector3d(entity.getPosX() - x, entity.getPosY() - y, entity.getPosZ() - z);
            vec3d = vec3d.normalize();
            Vector3d src = vec3d.add(x, y, z);
            BlockRayTraceResult result = this.getWorld().rayTraceBlocks(new RayTraceContext(entity.getPositionVec().add(0, entity.getEyeHeight(), 0), src, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
            if (result.getType() == RayTraceResult.Type.MISS) {
                this.target = entity;
                return;
            }
        }
        this.target = null;
    }
}

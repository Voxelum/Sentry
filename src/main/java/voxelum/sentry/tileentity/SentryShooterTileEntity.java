package voxelum.sentry.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import voxelum.sentry.Sentry;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class SentryShooterTileEntity extends TileEntity implements ITickable {
    private LivingEntity target;
    private int attackTick = 0;
    private int updateTargetTick = 0;
    private AxisAlignedBB bb;
    private UUID placerId;
    private EntityPlayer placer;

    public SentryShooterTileEntity() {
        super(Sentry.SENTRY_SHOOTER_TILE_ENTITY.get());
    }

    public void setPlacer(EntityPlayer entity) {
        this.placerId = entity.getUniqueID();
        this.placer = entity;
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        bb = new AxisAlignedBB(pos.getX() - 10, pos.getY() - 3, pos.getZ() - 10, pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10);
    }

    @Override
    public void update() {
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
        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(EmptyHandler.INSTANCE);
        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 0.5;
        double z = this.pos.getZ() + 0.5;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack result = handler.extractItem(i, 1, true);
            if (result.getItem() instanceof ItemArrow) {
                handler.extractItem(i, 1, false);
                Vec3d vec3d = new Vec3d(this.target.posX - x, this.target.posY + this.target.getEyeHeight() - y, this.target.posZ - z).normalize();
                EntityArrow arrowEntity = new EntityTippedArrow(world, x + vec3d.x, y + vec3d.y, z + vec3d.z);
                arrowEntity.shoot(vec3d.x, vec3d.y, vec3d.z, 3, 1);
                world.spawnEntity(arrowEntity);
                break;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (this.placerId != null) {
            compound.setTag("placer", NBTUtil.createUUIDTag(this.placerId));
        }
        return super.writeToNBT(compound);
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.placerId = NBTUtil.getUUIDFromTag(compound.getTag("placer"));
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
        if (entity instanceof EntityMob) {
            return true;
        }
        if (entity instanceof EntitySlime) {
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
        if (placer != null && placer.isDead) {
            placer = null;
        }

        if (placer == null && this.placerId != null) {
            this.placer = world.getPlayerEntityByUUID(this.placerId);
        }
    }

    private void updateTarget() {
        World world = this.world;
        BlockPos pos = this.pos;
        this.checkPlacer();
        List<EntityLiving> entityList = (List) world.getEntitiesInAABBexcluding(this.placer, bb, this::isValidTarget);
        EntityPredicate predicate = new EntityPredicate();
        this.target = world.getClosestEntity(entityList, predicate, this.placer, pos.getX(), pos.getY(), pos.getZ());
    }
}

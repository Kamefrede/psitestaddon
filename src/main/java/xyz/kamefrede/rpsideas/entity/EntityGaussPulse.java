package com.kamefrede.rpsideas.entity;

import com.kamefrede.rpsideas.effect.RPSPotions;
import com.kamefrede.rpsideas.items.RPSItems;
import com.kamefrede.rpsideas.network.MessageSparkleSphere;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PsiSoundHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.teamwizardry.librarianlib.features.network.PacketExtensionKt.sendToAllAround;

public class EntityGaussPulse extends EntityThrowable implements ISpellImmune {
    private static final String TAG_CASTER = "caster";
    private static final String TAG_TIME_ALIVE = "timeAlive";

    private static final String TAG_LAST_MOTION_X = "lastMotionX";
    private static final String TAG_LAST_MOTION_Y = "lastMotionY";
    private static final String TAG_LAST_MOTION_Z = "lastMotionZ";

    private static final String TAG_AMMO = "ammo";
    private static final DataParameter<Byte> AMMO_STATUS = EntityDataManager.createKey(EntityGaussPulse.class, DataSerializers.BYTE);
    public int timeAlive = 0;
    @Nullable
    public UUID uuid;
    private EnumFacing ejectFacing = null;

    public EntityGaussPulse(World worldIn) {
        super(worldIn);
        setSize(0F, 0F);
    }

    public EntityGaussPulse(World worldIn, EntityLivingBase throwerIn, AmmoStatus ammo) {
        super(worldIn, throwerIn);


        shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 4.5F, 0.0F);
        uuid = throwerIn.getUniqueID();
        this.setAmmoStatus(ammo);

        this.motionX -= throwerIn.motionX;
        this.motionZ -= throwerIn.motionZ;
        if (!throwerIn.onGround)
            this.motionY -= throwerIn.motionY;
    }

    public static void sparkle(World world, int count, Vector3 lookVector, double x, double y, double z, float r, float g, float b) {
        for (int i = 0; i < count; i++) {
            Vector3 look = lookVector.copy();
            double spread = 0.6;
            double dist = 0.15;

            look.x += (Math.random() - 0.5) * spread;
            look.y += (Math.random() - 0.5) * spread;
            look.z += (Math.random() - 0.5) * spread;

            look.normalize().multiply(dist);

            Psi.proxy.sparkleFX(world, x, y, z, r, g, b, (float) look.x, (float) look.y, (float) look.z, 1.2F, 12);
        }
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(AMMO_STATUS, (byte) AmmoStatus.PSI.ordinal());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (uuid != null)
            tagCompound.setUniqueId(TAG_CASTER, uuid);
        tagCompound.setInteger(TAG_TIME_ALIVE, this.timeAlive);
        tagCompound.setDouble(TAG_LAST_MOTION_X, this.motionX);
        tagCompound.setDouble(TAG_LAST_MOTION_Y, this.motionY);
        tagCompound.setDouble(TAG_LAST_MOTION_Z, this.motionZ);
        tagCompound.setInteger(TAG_AMMO, (byte) this.getAmmo().ordinal());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        this.uuid = tagCompound.getUniqueId(TAG_CASTER);
        if (uuid != null && uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L)
            uuid = null;
        this.timeAlive = tagCompound.getInteger(TAG_TIME_ALIVE);
        this.motionX = tagCompound.getDouble(TAG_LAST_MOTION_X);
        this.motionY = tagCompound.getDouble(TAG_LAST_MOTION_Y);
        this.motionZ = tagCompound.getDouble(TAG_LAST_MOTION_Z);
        this.setAmmoStatus(AmmoStatus.values()[tagCompound.getInteger(TAG_AMMO) % AmmoStatus.values().length]);
    }

    public AmmoStatus getAmmo() {
        return AmmoStatus.values()[dataManager.get(AMMO_STATUS) % (byte) AmmoStatus.values().length];
    }

    public void setAmmoStatus(AmmoStatus status) {
        dataManager.set(AMMO_STATUS, (byte) status.ordinal());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        timeAlive = this.ticksExisted;

        int granularity = 1;
        if (timeAlive > this.getLiveTime() || (prevPosX - posX) * (prevPosX - posX) +
                (prevPosY - posY) * (prevPosY - posY) +
                (prevPosZ - posZ) * (prevPosZ - posZ) < granularity)
            this.setDead();
        timeAlive++;

        Color color = new Color(getAmmo().color);
        float r = (float) color.getRed() / 255.0F;
        float g = (float) color.getGreen() / 255.0F;
        float b = (float) color.getBlue() / 255.0F;
        double x = posX;
        double y = posY;
        double z = posZ;


        Vector3 lookOrig = new Vector3(motionX, motionY, motionZ).normalize();
        sparkle(world, getParticleCount(), lookOrig, x, y, z, r, g, b);
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result) {
        if (world.isRemote) return;
        Entity entityHit = result.entityHit;
        if (entityHit != null) {
            if (Objects.equals(entityHit.getCachedUniqueIdString(), thrower.getCachedUniqueIdString()))
                return;

            if (getAmmo() == AmmoStatus.AMMO)
                setAmmoStatus(AmmoStatus.DEPLETED);

            entityHit.attackEntityFrom(new EntityDamageSourceIndirect("magic", this, thrower).setProjectile(), getAmmo().damage);

            if (entityHit instanceof EntityLivingBase)
                ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(RPSPotions.psishock, getAmmo().shockDuration));

            if (entityHit instanceof EntityEnderman)
                return;
        }
        posX = result.hitVec.x;
        posY = result.hitVec.y;
        posZ = result.hitVec.z;
        ejectFacing = result.sideHit;
        setDead();
    }

    public int getLiveTime() {
        return 600;
    }

    public int getParticleCount() {
        return 5;
    }

    @Override
    public void setDead() {
        super.setDead();
        playSound(PsiSoundHandler.compileError, 1f, 1f);
        if (!world.isRemote) {
            AxisAlignedBB box = new AxisAlignedBB(posX - 5, posY - 5, posZ - 5, posX + 5, posY + 5, posZ + 5);
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, box, (EntityLivingBase e) ->
                    e != null && e != thrower && e.getPositionVector().squareDistanceTo(this.getPositionVector()) <= 25);

            List<EntityLivingBase> players = new ArrayList<>(list);
            players.removeIf(entityLivingBase -> !(entityLivingBase instanceof EntityPlayer) && entityLivingBase.isPotionActive(RPSPotions.psishock));

            if (list.size() > 0 || getAmmo() != AmmoStatus.AMMO) {
                for (EntityLivingBase player : players)
                    player.addPotionEffect(new PotionEffect(RPSPotions.psishock, getAmmo().shockDuration));
                for (EntityLivingBase ent : list)
                    ent.attackEntityFrom(new EntityDamageSourceIndirect("arrow", this, thrower).setProjectile(), getAmmo().damage);
                sendToAllAround(PacketHandler.NETWORK, new MessageSparkleSphere(getPositionVector(), getAmmo()), world, getPositionVector(), 128.0);

            } else if (getAmmo() == AmmoStatus.AMMO) {
                EntityItem item = new EntityItem(world, posX, posY, posZ, new ItemStack(RPSItems.gaussBullet));
                Vec3d motionVec = new Vec3d(motionX, motionY, motionZ);
                Vec3d vec = motionVec.normalize().scale(1 / 4.5F);
                EnumFacing eject = ejectFacing;
                if (eject != null) {
                    Vec3d normal = new Vec3d(eject.getDirectionVec());
                    vec = vec.subtract(normal.scale(2 * vec.dotProduct(normal)).subtract(normal.scale(0.25)));
                } else
                    vec = vec.scale(-1);
                item.motionX = vec.x;
                item.motionY = vec.y;
                item.motionZ = vec.z;
                item.setPickupDelay(40);
                world.spawnEntity(item);
            }

        }

    }

    @Nonnull
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    public EntityLivingBase getThrower() {
        EntityLivingBase superThrower = super.getThrower();
        if (superThrower != null)
            return superThrower;

        if (uuid == null) return null;
        return this.world.getPlayerEntityByUUID(uuid);
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    public boolean isImmune() {
        return true;
    }

    public enum AmmoStatus {
        PSI(ICADColorizer.DEFAULT_SPELL_COLOR, 2f, 100, false),
        DEPLETED(0xB87333, 8f, 25, false),
        AMMO(0xB87333, 8f, 25, true),
        BLOOD(0xFF0000, 10f, 100, false);

        public final int color;
        public final float damage;
        public final int shockDuration;
        public final boolean dropsItem;

        AmmoStatus(int color, float damage, int shockDuration, boolean dropsItem) {
            this.color = color;
            this.damage = damage;
            this.dropsItem = dropsItem;
            this.shockDuration = shockDuration;
        }
    }
}

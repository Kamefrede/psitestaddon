package com.kamefrede.rpsideas.entity;

import com.kamefrede.rpsideas.RPSIdeas;
import com.kamefrede.rpsideas.entity.botania.EntityPsiManaBurst;
import com.kamefrede.rpsideas.render.RenderClone;
import com.kamefrede.rpsideas.render.RenderConjuredText;
import com.kamefrede.rpsideas.render.RenderFancyCircle;
import com.kamefrede.rpsideas.render.RenderHailParticle;
import com.kamefrede.rpsideas.util.libs.RPSEntityNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RPSEntities {
    private static int id = 0;

    public static void init() {
        registerModEntity(EntityGaussPulse.class, RPSEntityNames.ENTITY_GAUSS_PULSE, 256, 10, true);
        registerModEntity(EntityPsireworkRocket.class, RPSEntityNames.ENTITY_PSI_FIREWORK, 256, 10, true);
        registerModEntity(EntityConjuredText.class, RPSEntityNames.ENTITY_CONJURED_TEXT, 256, 10, false);
        registerModEntity(EntityFancyCircle.class, RPSEntityNames.ENTITY_FANCY_CIRCLE, 256, 64, false);
        registerModEntity(EntityHailParticle.class, RPSEntityNames.ENTITY_HAIL_PARTICLE, 256, 10, true);
        registerModEntity(EntityClone.class, RPSEntityNames.ENTITY_SUMMONED_CLONE, 256, 10, false);
    }

    @SideOnly(Side.CLIENT)
    public static void clientInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPsireworkRocket.class,
                (manager) -> new RenderSnowball<>(manager, Items.FIREWORKS, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityConjuredText.class, RenderConjuredText::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFancyCircle.class, RenderFancyCircle::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityHailParticle.class, RenderHailParticle::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityClone.class, RenderClone::new);
    }

    @Optional.Method(modid = "botania")
    public static void initBotania() {
        registerModEntity(EntityPsiManaBurst.class, RPSEntityNames.ENTITY_PSI_BURST, 64, 10, true);
    }

    public static void registerModEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(RPSIdeas.MODID, entityName), entityClass, entityName, id++, RPSIdeas.INSTANCE, trackingRange, updateFrequency, sendsVelocityUpdates);
    }
}

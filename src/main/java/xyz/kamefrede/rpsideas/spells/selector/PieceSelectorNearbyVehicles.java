package com.kamefrede.rpsideas.spells.selector;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearby;

public class PieceSelectorNearbyVehicles extends PieceSelectorNearby {

    public PieceSelectorNearbyVehicles(Spell spell) {
        super(spell);
    }

    @Override
    @SuppressWarnings("Guava")
    public Predicate<Entity> getTargetPredicate() {
        return (Entity e) -> e instanceof EntityMinecart || e instanceof EntityBoat || (e instanceof AbstractHorse && ((AbstractHorse) e).isHorseSaddled()) || (e instanceof EntityPig && ((EntityPig) e).getSaddled());
    }
}

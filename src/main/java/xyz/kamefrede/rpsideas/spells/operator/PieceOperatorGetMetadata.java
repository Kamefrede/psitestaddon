package com.kamefrede.rpsideas.spells.operator;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorGetMetadata extends PieceOperator {

    public PieceOperatorGetMetadata(Spell spell) {
        super(spell);
    }


    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        int slot = context.getTargetSlot();
        if (!context.caster.inventory.getStackInSlot(slot).isEmpty()) {
            return context.caster.inventory.getStackInSlot(slot).getMetadata() * 1.0;
        }
        return 0.0;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Double.class;
    }
}

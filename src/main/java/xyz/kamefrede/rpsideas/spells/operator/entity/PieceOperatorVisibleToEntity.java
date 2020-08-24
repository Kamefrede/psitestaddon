package com.kamefrede.rpsideas.spells.operator.entity;

import com.kamefrede.rpsideas.spells.base.SpellParams;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVisibleToEntity extends PieceOperator {

    private SpellParam viewer;
    private SpellParam viewed;

    public PieceOperatorVisibleToEntity(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        super.initParams();
        addParam(viewer = new ParamEntity(SpellParams.GENERIC_NAME_VIEWER, SpellParam.BLUE, false, false));
        addParam(viewed = new ParamEntity(SpellParams.GENERIC_NAME_VIEWED, SpellParam.RED, false, false));
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        EntityLivingBase viewerEntity = SpellHelpers.ensureNonnullAndLivingEntity(this, context, viewer);
        Entity viewedEntity = SpellHelpers.ensureNonnullEntity(this, context, viewed);

        return viewerEntity.canEntityBeSeen(viewedEntity) ? 1.0 : 0.0;

    }

    @Override
    public Class<?> getEvaluationType() {
        return Double.class;
    }
}

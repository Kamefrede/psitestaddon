package com.kamefrede.rpsideas.spells.trick.misc;

import com.kamefrede.rpsideas.entity.EntityConjuredText;
import com.kamefrede.rpsideas.spells.base.SpellParams;
import com.kamefrede.rpsideas.spells.base.SpellRuntimeExceptions;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.Objects;

public class PieceTrickConjureText extends PieceTrick {

    private SpellParam text;
    private SpellParam position;
    private SpellParam time;

    public PieceTrickConjureText(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.RED, false, false));
        addParam(text = new ParamAny(SpellParams.GENERIC_NAME_TEXT, SpellParam.BLUE, false));
        addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.CYAN, false, true));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
        super.addToMetadata(meta);
        double timeAlive = SpellHelpers.ensurePositiveAndNonzero(this, time);
        meta.addStat(EnumSpellStat.POTENCY, (int) (timeAlive / 20));
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        World world = context.caster.world;
        Vector3 pos = SpellHelpers.getVector3(this, context, position, true, true, false);
        Object targetVal = this.getParamValue(context, text);
        double maxTimeAlive = SpellHelpers.getBoundedNumber(this, context, time, 3600);

        String text = Objects.toString(targetVal);


        if (text.length() > 32)
            throw new SpellRuntimeException(SpellRuntimeExceptions.TEXT);

        if (text.isEmpty() || text.matches("\\s+"))
            throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);

        ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
        ItemStack colorizer = ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);


        EntityConjuredText conjuredText = new EntityConjuredText(world);
        conjuredText.setInfo(context.caster, colorizer, text, pos, (int) maxTimeAlive);
        world.spawnEntity(conjuredText);

        return null;
    }
}

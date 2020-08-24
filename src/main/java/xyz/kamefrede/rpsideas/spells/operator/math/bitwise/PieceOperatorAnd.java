package com.kamefrede.rpsideas.spells.operator.math.bitwise;

import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

import java.util.List;

/**
 * @author WireSegal
 * Created at 7:50 PM on 2/17/19.
 */
public class PieceOperatorAnd extends PieceOperator {
    private SpellParam num1;
    private SpellParam num2;
    private SpellParam num3;

    public PieceOperatorAnd(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(num1 = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER1, SpellParam.RED, false, false));
        addParam(num2 = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER2, SpellParam.GREEN, false, false));
        addParam(num3 = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER3, SpellParam.GREEN, true, false));
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        int num1 = (int) SpellHelpers.getNumber(this, context, this.num1, -1);
        int num2 = (int) SpellHelpers.getNumber(this, context, this.num2, -1);
        int num3 = (int) SpellHelpers.getNumber(this, context, this.num3, -1);

        return (double) (num1 & num2 & num3);
    }

    @Override
    public void addToTooltipAfterShift(List<String> tooltip) {
        tooltip.add(TextFormatting.GRAY + I18n.format(this.getUnlocalizedDesc())); // Because & is used in desc
        TooltipHelper.addToTooltip(tooltip, "");
        String eval = this.getEvaluationTypeString();
        TooltipHelper.addToTooltip(tooltip, "<- " + TextFormatting.GOLD + eval);

        for (SpellParam param : this.paramSides.keySet()) {
            String pName = TooltipHelper.local(param.name);
            String pEval = param.getRequiredTypeString();
            TooltipHelper.addToTooltip(tooltip, (param.canDisable ? "[->] " : " ->  ") + TextFormatting.YELLOW + pName + " [" + pEval + "]");
        }
    }

    @Override
    public Class<?> getEvaluationType() {
        return Double.class;
    }
}

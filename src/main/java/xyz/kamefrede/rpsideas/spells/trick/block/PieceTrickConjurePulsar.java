package com.kamefrede.rpsideas.spells.trick.block;

import com.kamefrede.rpsideas.blocks.BlockConjuredPulsar;
import com.kamefrede.rpsideas.blocks.RPSBlocks;
import com.kamefrede.rpsideas.tiles.TileEthereal;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickConjurePulsar extends PieceTrick implements IPulsarConjuration {

    protected SpellParam positionParam;
    private SpellParam timeParam;

    public PieceTrickConjurePulsar(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        positionParam = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false);
        timeParam = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.RED, true, false);
        SpellHelpers.addAllParams(this, positionParam, timeParam);
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
        super.addToMetadata(meta);
        addStats(meta);
    }

    protected void addStats(SpellMetadata meta) {
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST, 30);
        meta.addStat(EnumSpellStat.COMPLEXITY, 2);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        conjurePulsar(context, positionParam, timeParam);
        return null;
    }

    @Override
    public IBlockState getStateToSet() {
        return RPSBlocks.conjuredPulsar.getDefaultState().withProperty(BlockConjuredPulsar.SOLID, true);
    }

    @Override
    public void postSet(SpellContext context, World world, BlockPos pos, int time) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEthereal) {
            TileEthereal pulsar = (TileEthereal) tile;
            if (time > 0) {
                pulsar.time = time;
            }

            ItemStack cad = PsiAPI.getPlayerCAD(context.caster);

            if (cad != null)
                pulsar.colorizer = ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);

        }
    }
}

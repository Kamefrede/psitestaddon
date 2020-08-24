package com.kamefrede.rpsideas.spells.operator.energy;

import com.kamefrede.rpsideas.spells.base.OperatorEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import vazkii.psi.api.spell.Spell;

public class OperatorMaxOutput extends OperatorEnergy {

    public OperatorMaxOutput(Spell spell) {
        super(spell);
    }

    @Override
    protected double result(World world, BlockPos pos, TileEntity tile, IEnergyStorage storage) {
        return storage.extractEnergy(storage.getMaxEnergyStored(), true) * 1.0;
    }
}

package com.kamefrede.rpsideas.spells.trick.block;

import com.kamefrede.rpsideas.spells.base.SpellParams;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.client.core.handler.HUDHandler;
import vazkii.psi.common.block.base.ModBlocks;

import static vazkii.psi.common.spell.trick.block.PieceTrickPlaceBlock.removeFromInventory;

public class PieceTrickDirectionPlaceBlock extends PieceTrick {

    private SpellParam position;
    private SpellParam direction;

    public PieceTrickDirectionPlaceBlock(Spell spell) {
        super(spell);
    }

    public static void placeBlock(EntityPlayer player, World world, BlockPos pos, int slot, boolean particles, boolean conjure, EnumFacing facing) {
        if (!world.isBlockLoaded(pos) || !world.isBlockModifiable(player, pos)) {
            return;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();


        if (block.isReplaceable(world, pos)) {
            if (conjure) {
                world.setBlockState(pos, ModBlocks.conjured.getDefaultState());
            } else {
                ItemStack stack = player.inventory.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                    ItemStack rem = removeFromInventory(player, stack);
                    ItemBlock itemBlock = (ItemBlock) rem.getItem();

                    Block blockToPlace = Block.getBlockFromItem(rem.getItem());
                    IBlockState newState = blockToPlace.getStateForPlacement(world, pos, facing, 0, 0, 0, rem.getItemDamage(), player, player.getActiveHand());
                    itemBlock.placeBlockAt(stack, player, world, pos, facing, 0, 0, 0, newState);
                    PieceTrickRotateBlock.rotateBlock(world, pos, facing);

                    if (player.capabilities.isCreativeMode) {
                        HUDHandler.setRemaining(rem, -1);
                    } else {
                        HUDHandler.setRemaining(player, rem, null);
                    }
                }
                if (particles) {
                    world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
                }
            }
        }


    }

    @Override
    public void initParams() {
        addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
        addParam(direction = new ParamVector(SpellParams.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);

        meta.addStat(EnumSpellStat.POTENCY, 8);
        meta.addStat(EnumSpellStat.COST, 8);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        BlockPos pos = SpellHelpers.getBlockPos(this, context, position, true, true, false);
        EnumFacing facing = SpellHelpers.getFacing(this, context, direction);
        placeBlock(context.caster, context.caster.world, pos, context.getTargetSlot(), false, false, facing);

        return null;
    }
}

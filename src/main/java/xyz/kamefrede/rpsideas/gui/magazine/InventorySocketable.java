package com.kamefrede.rpsideas.gui.magazine;

import com.kamefrede.rpsideas.util.helpers.IteratorSocketable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import vazkii.psi.api.cad.ISocketableCapability;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.spell.SpellCompiler;

import javax.annotation.Nonnull;

public class InventorySocketable implements IInventory {

    private final ItemStack stack;
    private final ISocketableCapability socketable;
    private final int maxBandwidth;

    public InventorySocketable(ItemStack stack, int maxBandwidth) {
        this.stack = stack;
        this.maxBandwidth = maxBandwidth;

        socketable = ISocketableCapability.socketable(stack);
    }

    public InventorySocketable(ItemStack stack) {
        this(stack, -1);
    }

    private IteratorSocketable getSockerator() {
        return new IteratorSocketable(stack);
    }

    private int totalSlots() {
        IteratorSocketable sockerator = getSockerator();
        int count = 0;
        while (sockerator.hasNext()) {
            sockerator.next();
            count++;
        }
        return count - 1;
    }


    @Override
    public int getSizeInventory() {
        return totalSlots();
    }

    @Override
    public boolean isEmpty() {
        IteratorSocketable sockerator = getSockerator();
        while (sockerator.hasNext()) {
            if (!sockerator.next().isEmpty()) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return socketable.getBulletInSocket(index);
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack bullet = socketable.getBulletInSocket(index);
        if (!bullet.isEmpty()) socketable.setBulletInSocket(index, ItemStack.EMPTY);
        return bullet;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return decrStackSize(index, 1);
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack bullet) {
        socketable.setBulletInSocket(index, bullet);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
        //no-op
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
        //no-op
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
        //no-op
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (!(ISpellAcceptor.isContainer(stack))) return false;
        if (maxBandwidth < 0)
            return true;
        ISpellAcceptor acceptor = ISpellAcceptor.acceptor(stack);
        Spell spell = acceptor.getSpell();
        SpellCompiler cmp = new SpellCompiler(spell);
        int bandwidth = cmp.getCompiledSpell().metadata.stats.get(EnumSpellStat.BANDWIDTH);
        return bandwidth <= maxBandwidth;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        //no-op
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        IteratorSocketable sockerator = getSockerator();
        while (sockerator.hasNext()) {
            sockerator.remove();
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "rpsideas.container.socketable";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getName());
    }
}

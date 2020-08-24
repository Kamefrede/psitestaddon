package com.kamefrede.rpsideas.items;

import com.kamefrede.rpsideas.network.MessageCuffSync;
import com.kamefrede.rpsideas.util.helpers.IFlowColorAcceptor;
import com.kamefrede.rpsideas.util.libs.RPSItemNames;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.network.message.MessageDataSync;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPsiCuffs extends ItemMod implements IFlowColorAcceptor {

    public ItemPsiCuffs() {
        super(RPSItemNames.PSI_CUFFS);
        setMaxStackSize(1);
    }

    private static final String TAG_CUFFED = "rpsideas:cuffed";
    private static final String TAG_KEYNAME = "rpsideas:cuffKeyName";


    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (playerIn.isSneaking() && NBTHelper.getString(playerIn.getHeldItem(handIn), TAG_KEYNAME) != null) {
            NBTHelper.removeNBTEntry(playerIn.getHeldItem(handIn), TAG_KEYNAME);
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target instanceof EntityPlayer) {
            String keyName = NBTHelper.getString(stack, TAG_KEYNAME);
            if (keyName == null)
                return false;

            EntityPlayer targetPlayer = (EntityPlayer) target;
            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(targetPlayer);
            if (!data.getCustomData().getBoolean(TAG_CUFFED)) {
                data.getCustomData().setBoolean(TAG_CUFFED, true);
                if (!playerIn.world.isRemote) {
                    targetPlayer.getEntityData().setString(TAG_KEYNAME, keyName);
                }
                PacketHandler.NETWORK.sendToAll(new MessageCuffSync(targetPlayer.getEntityId(), true));
                stack.shrink(1);
                data.save();
                if (targetPlayer instanceof EntityPlayerMP)
                    NetworkHandler.INSTANCE.sendTo(new MessageDataSync(data), (EntityPlayerMP) targetPlayer);
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @javax.annotation.Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        TooltipHelper.tooltipIfShift(tooltip, () -> {
            if (NBTHelper.getString(stack, TAG_KEYNAME) == null)
                TooltipHelper.addToTooltip(tooltip, getTranslationKey(stack) + ".desc");
            else
                TooltipHelper.addToTooltip(tooltip, getTranslationKey(stack) + ".desc1", Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName());

        });
    }


}

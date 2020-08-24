package com.kamefrede.rpsideas.items.components.botania;

import com.kamefrede.rpsideas.RPSIdeas;
import com.kamefrede.rpsideas.items.base.ItemComponent;
import com.kamefrede.rpsideas.spells.enabler.botania.EnumManaTier;
import com.kamefrede.rpsideas.spells.enabler.botania.IBlasterComponent;
import com.kamefrede.rpsideas.spells.enabler.botania.IManaTrick;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import com.kamefrede.rpsideas.util.libs.RPSItemNames;
import com.teamwizardry.librarianlib.features.base.IExtraVariantHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler;
import vazkii.botania.common.item.ItemManaGun;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.item.base.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RPSIdeas.MODID)
public class ItemBlasterAssembly extends ItemComponent implements IBlasterComponent, IExtraVariantHolder {

    public static final String[] CAD_MODELS = {
            "cad_blaster"
    };

    private static final Pattern advancedMatcher = Pattern.compile("\\s+(?=\\(#\\d+\\))");


    public ItemBlasterAssembly() {
        super(RPSItemNames.CAD_ASSEMBLY);
        ModItems.cad.addPropertyOverride(new ResourceLocation(RPSIdeas.MODID, "clip"), ((stack, world, ent) -> ItemManaGun.hasClip(stack) ? 1f : 0f));
    }

    @Override
    @Optional.Method(modid = "botania")
    public boolean enables(ItemStack cad, ItemStack component, SpellPiece piece) {
        if (piece instanceof IManaTrick) {
            EnumManaTier cadTier = EnumManaTier.BASE;
            if (ItemManaGun.hasClip(cad))
                cadTier = EnumManaTier.ALFHEIM;
            EnumManaTier pieceTier = ((IManaTrick) piece).tier();
            return EnumManaTier.allowed(cadTier, pieceTier);
        }
        return false;
    }

    @SubscribeEvent
    @Optional.Method(modid = "botania")
    public static void tooltip(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        Item item = stack.getItem();

        if (!(item instanceof ICAD)) return;
        ICAD icad = (ICAD) item;

        EntityPlayer player = e.getEntityPlayer();
        World world = player == null ? null : player.world;

        ItemStack assemblyStack = icad.getComponentInSlot(e.getItemStack(), EnumCADComponent.ASSEMBLY);
        if (assemblyStack.isEmpty() || !(assemblyStack.getItem() instanceof IBlasterComponent)) return;

        ItemStack lens = ItemManaGun.getLens(stack);
        if (!lens.isEmpty()) {
            String itemName = e.getToolTip().get(0);

            StringBuilder joined = new StringBuilder();
            String[] match = advancedMatcher.split(itemName);
            if (match.length > 1) {
                for (int i = 1; i < match.length; i++) {
                    if (i != 1) joined.append(' ');
                    joined.append(match[i]);
                }
            }

            itemName = match[0].trim() + ' ' + TextFormatting.RESET + '(' + TextFormatting.GREEN + lens.getDisplayName() + TextFormatting.RESET + ')' + joined.toString();

            if (GuiScreen.isShiftKeyDown()) {
                List<String> gunTooltip = new ArrayList<>();
                vazkii.botania.common.item.ModItems.manaGun.addInformation(stack, world, gunTooltip, e.getFlags());
                e.getToolTip().addAll(2, gunTooltip);
            }

            e.getToolTip().set(0, itemName);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "botania")
    public static void onInteract(PlayerInteractEvent.RightClickItem e) {
        if (e.getEntityPlayer().isSneaking()) {
            ItemStack heldStack = e.getItemStack();
            EnumHand hand = e.getHand();
            World world = e.getWorld();

            if (!heldStack.isEmpty() && heldStack.getItem() instanceof ICAD) {
                ICAD icad = (ICAD) heldStack.getItem();
                boolean hasBlasterAssembly = icad.getComponentInSlot(heldStack, EnumCADComponent.ASSEMBLY).getItem() instanceof IBlasterComponent;
                if (hasBlasterAssembly && ItemManaGun.hasClip(heldStack)) {
                    ItemManaGun.rotatePos(heldStack);

                    SpellHelpers.emitSoundFromEntity(world, e.getEntityPlayer(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, .6f, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

                    if (world.isRemote) e.getEntityPlayer().swingArm(hand);

                    ItemsRemainingRenderHandler.set(ItemManaGun.getLens(heldStack), -2);

                    e.setCanceled(true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelResourceLocation getCADModel(ItemStack itemStack, ItemStack itemStack1) {
        return new ModelResourceLocation(new ResourceLocation(RPSIdeas.MODID, "cad_blaster"), "inventory");
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void addTooltipTags(Minecraft minecraft, @Nullable World world, KeyBinding sneak, ItemStack stack, List<String> tooltip, ITooltipFlag advanced) {
        addTooltipTag(tooltip, true, RPSIdeas.MODID + ".requirement.mana_cad");
    }

    @Override
    public void registerStats() {
        addStat(EnumCADStat.EFFICIENCY, 80);
        addStat(EnumCADStat.POTENCY, 250);
    }

    @Nonnull
    @Override
    public String[] getExtraVariants() {
        return CAD_MODELS;
    }
}

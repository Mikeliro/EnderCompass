package io.github.mribby.endercompass;

import io.github.mribby.endercompass.network.EnderCompassProxy;
import io.github.mribby.endercompass.network.MessageGetStrongholdPos;
import io.github.mribby.endercompass.network.MessageSetStrongholdPos;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnderCompassMod.ID, name = EnderCompassMod.NAME, version = EnderCompassMod.VERSION, updateJSON = EnderCompassMod.UPDATE_JSON_URL, acceptedMinecraftVersions = EnderCompassMod.MINECRAFT_VERSIONS)
public class EnderCompassMod {
    public static final String ID = "endercompass";
    public static final String NAME = "Ender Compass";
    public static final String VERSION = "@VERSION@";
    public static final String UPDATE_JSON_URL = "https://gist.github.com/MrIbby/174385130d65a4da3d9d6c472ac47114/raw";
    public static final String MINECRAFT_VERSIONS = "*";

    public static final Item ENDER_COMPASS = new ItemEnderCompass().setUnlocalizedName("compassEnd").setCreativeTab(CreativeTabs.TOOLS).setRegistryName("ender_compass");

    @SidedProxy(clientSide = "io.github.mribby.endercompass.client.EnderCompassClient", serverSide = "io.github.mribby.endercompass.network.EnderCompassProxy")
    public static EnderCompassProxy proxy;
    public static SimpleNetworkWrapper network;

    public static boolean checkInventory;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        checkInventory = config.getBoolean("checkInventory", Configuration.CATEGORY_GENERAL, false, "Should the mod check if the player has an ender compass?");
        config.save();

        GameRegistry.register(ENDER_COMPASS);
        GameRegistry.addRecipe(new ItemStack(ENDER_COMPASS), " E ", "ECE", " E ", 'E', Items.ENDER_EYE, 'C', Items.COMPASS);
        //todo: ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(ender_compass, 0, 1, 1, 1));

        network = NetworkRegistry.INSTANCE.newSimpleChannel("endercompass");
        network.registerMessage(new MessageGetStrongholdPos(), MessageGetStrongholdPos.class, 0, Side.SERVER);
        network.registerMessage(new MessageSetStrongholdPos(), MessageSetStrongholdPos.class, 1, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(proxy);

        proxy.preInit();
    }

    public static boolean containsCompass(IInventory inventory) {
        if (checkInventory) {
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.func_190926_b() && stack.getItem() == ENDER_COMPASS) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}

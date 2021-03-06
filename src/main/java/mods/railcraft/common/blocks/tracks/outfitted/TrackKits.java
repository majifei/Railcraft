/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted;

import com.google.common.collect.ObjectArrays;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.tracks.outfitted.kits.*;
import mods.railcraft.common.core.*;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemRailbed.EnumRailbed;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum TrackKits implements IRailcraftObjectContainer<IRailcraftObject<TrackKit>> {

    ACTIVATOR(ModuleTracks.class, 2, "activator", 8, TrackKitActivator.class, () -> recipe(Items.REDSTONE, Items.REDSTONE)),
    BOOSTER(ModuleTracksStrapIron.class, 2, "booster", 8, TrackKitBooster.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    BUFFER_STOP(ModuleTracks.class, 2, "buffer", 8, TrackBufferStop.class, () -> recipe("ingotIron", "ingotIron")),
    CONTROL(ModuleTracks.class, 2, "control", 16, TrackKitControl.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    COUPLER(ModuleTracks.class, 6, "coupler", 8, TrackKitCoupler.class, () -> recipe(Items.LEAD, Items.REDSTONE)),
    DETECTOR(ModuleTracks.class, 6, "detector", 8, TrackKitDetector.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Items.REDSTONE)),
    DISEMBARK(ModuleTracks.class, 4, "disembarking", 8, TrackKitDisembark.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Items.LEAD, Items.REDSTONE)),
    DUMPING(ModuleTracks.class, 2, "dumping", 8, TrackKitDumping.class, () -> recipe(RailcraftItems.PLATE, Metal.STEEL, Items.REDSTONE)),
    EMBARKING(ModuleTracks.class, 2, "embarking", 8, TrackKitEmbarking.class, () -> recipe(Items.ENDER_PEARL, Items.LEAD, Items.REDSTONE)),
    GATED(ModuleTracks.class, 4, "gated", 4, TrackKitGated.class, () -> recipe("gateWood", RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE)),
    //    GATED_ONE_WAY(ModuleTracks.class, 2, "gated_one_way", 4, TrackKitGatedOneWay.class),
    HIGH_SPEED_TRANSITION(ModuleTracksHighSpeed.class, 4, "transition", 8, TrackKitSpeedTransition.class, () -> recipe(RailcraftItems.RAIL, EnumRail.ADVANCED, RailcraftItems.RAIL, EnumRail.ADVANCED, Items.REDSTONE, Items.REDSTONE)),
    LAUNCHER(ModuleExtras.class, 2, "launcher", 1, TrackKitLauncher.class, () -> recipe(Blocks.PISTON, "blockSteel", "blockSteel", Items.REDSTONE)),
    THROTTLE(ModuleLocomotives.class, 14, "throttle", 8, TrackKitThrottle.class, () -> recipe("dyeYellow", "dyeBlack", Items.REDSTONE)),
    LOCKING(ModuleTracks.class, 16, "locking", 8, TrackKitLocking.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Blocks.STICKY_PISTON, Items.REDSTONE)),
    LOCOMOTIVE(ModuleLocomotives.class, 6, "locomotive", 8, TrackKitLocomotive.class, () -> recipe(RailcraftItems.SIGNAL_LAMP, Items.REDSTONE)),
    ONE_WAY(ModuleTracks.class, 4, "one_way", 8, TrackKitOneWay.class, () -> recipe(Blocks.STONE_PRESSURE_PLATE, Blocks.PISTON, Items.REDSTONE)),
    PRIMING(ModuleExtras.class, 2, "priming", 8, TrackKitPriming.class, () -> recipe(Items.FLINT_AND_STEEL, Items.REDSTONE)),
    ROUTING(ModuleRouting.class, 2, "routing", 8, TrackKitRouting.class, () -> recipes(craft(RailcraftItems.TICKET, Items.REDSTONE), craft(RailcraftItems.TICKET_GOLD, Items.REDSTONE))),
    WHISTLE(ModuleLocomotives.class, 2, "whistle", 8, TrackKitWhistle.class, () -> recipe("dyeYellow", "dyeBlack", Blocks.NOTEBLOCK, Items.REDSTONE)),
//    JUNCTION(ModuleTracks.class, 1, 0, "junction", 8, TrackJunction.class),
//    SWITCH(ModuleSignals.class, 4, 0, "switch", 8, TrackSwitch.class),
//    WYE(ModuleTracks.class, 2, 0, "wye", 8, TrackKitWye.class),
    ;
    public static final TrackKits[] VALUES = values();
    private static final List<TrackKits> creativeList = new ArrayList<TrackKits>(50);
    private static final Set<TrackKit> TRACK_KITS = new HashSet<TrackKit>(50);
    private static final Predicate<TrackType> IS_HIGH_SPEED = trackType -> trackType.getName().contains("high_speed");
    private static final Predicate<TrackType> NOT_HIGH_SPEED = IS_HIGH_SPEED.negate();

    static {
        TRACK_KITS.add(TrackRegistry.getMissingTrackKit());

        DETECTOR.requiresTicks = true;
        LOCKING.requiresTicks = true;

        BUFFER_STOP.allowedOnSlopes = false;
        DISEMBARK.allowedOnSlopes = false;
        DUMPING.allowedOnSlopes = false;
        EMBARKING.allowedOnSlopes = false;
        GATED.allowedOnSlopes = false;
//        GATED_ONE_WAY.allowedOnSlopes = false;
        LAUNCHER.allowedOnSlopes = false;
        LOCKING.allowedOnSlopes = false;

        DUMPING.trackTypeFilter = NOT_HIGH_SPEED;
        GATED.trackTypeFilter = NOT_HIGH_SPEED;
        ONE_WAY.trackTypeFilter = NOT_HIGH_SPEED;
        LAUNCHER.trackTypeFilter = NOT_HIGH_SPEED;
        COUPLER.trackTypeFilter = NOT_HIGH_SPEED;
        CONTROL.trackTypeFilter = NOT_HIGH_SPEED;
        BUFFER_STOP.trackTypeFilter = NOT_HIGH_SPEED;
        HIGH_SPEED_TRANSITION.trackTypeFilter = IS_HIGH_SPEED;

        DUMPING.maxSupportDistance = 2;
    }

    public final int recipeOutput;
    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final int numIcons;
    private final int states;
    private final Class<? extends TrackKitRailcraft> trackInstance;
    private final Supplier<List<Object[]>> recipeSupplier;
    private TrackKit trackKit;
    private boolean depreciated;
    private boolean allowedOnSlopes = true;
    private boolean requiresTicks;
    private int maxSupportDistance;
    private Predicate<TrackType> trackTypeFilter = (t) -> true;

    TrackKits(Class<? extends IRailcraftModule> module, int states, String tag, int recipeOutput, Class<? extends TrackKitRailcraft> trackInstance) {
        this(module, states, tag, recipeOutput, trackInstance, Collections::emptyList);
    }

    TrackKits(Class<? extends IRailcraftModule> module, int states, String tag, int recipeOutput, Class<? extends TrackKitRailcraft> trackInstance, Supplier<List<Object[]>> recipeSupplier) {
        this.module = module;
        this.numIcons = states;
        this.states = states;
        this.tag = tag;
        this.recipeOutput = recipeOutput;
        this.trackInstance = trackInstance;
        this.recipeSupplier = recipeSupplier;
    }

    public static TrackKits fromId(int id) {
        if (id < 0 || id >= TrackKits.values().length)
            id = 0;
        return TrackKits.values()[id];
    }

    public static Object[] craft(Object... recipe) {
        return recipe;
    }

    public static List<Object[]> recipes(Object[]... recipes) {
        return Arrays.asList(recipes);
    }

    public static List<Object[]> recipe(Object... recipe) {
        List<Object[]> list = new ArrayList<>();
        list.add(recipe);
        return list;
    }

    public static Collection<TrackKit> getRailcraftTrackKits() {
        return TRACK_KITS;
    }

    @Override
    public void register() {
        if (!RailcraftItems.TRACK_KIT.isLoaded() || !RailcraftModuleManager.isModuleEnabled(ModuleTracks.class))
            return;
        //TODO: Add way to disable track kits
        if (trackKit == null) {
            TrackKit.Builder builder = new TrackKit.Builder(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, tag), trackInstance);
            builder.setRequiresTicks(requiresTicks);
            builder.setRenderStates(states);
            builder.setAllowedOnSlopes(allowedOnSlopes);
            builder.setTrackTypeFilter(trackTypeFilter);
            builder.setMaxSupportDistance(maxSupportDistance);
            trackKit = builder.build();
            try {
                TrackRegistry.TRACK_KIT.register(trackKit);
                TRACK_KITS.add(trackKit);
//                registerRecipe();
            } catch (Error error) {
                Game.logErrorAPI(Railcraft.MOD_ID, error, TrackRegistry.class, TrackKit.class);
                return;
            }
            List<Object[]> recipes = recipeSupplier.get();
            if (recipes != null) {
                recipes.stream().filter(ArrayUtils::isNotEmpty).forEach(recipe -> {
                    Object[] commonIngredients = {"plankWood", RailcraftItems.TRACK_PARTS};
                    Object[] finalRecipe = ObjectArrays.concat(commonIngredients, recipe, Object.class);
                    CraftingPlugin.addShapelessRecipe(trackKit.getTrackKitItem(), finalRecipe);
                });
            }
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return stack.getItem() instanceof ItemTrackKit && TrackRegistry.TRACK_KIT.get(stack) == getTrackKit();
    }

    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(module) && RailcraftBlocks.TRACK_OUTFITTED.isEnabled() && RailcraftItems.TRACK_KIT.isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag()) && !isDepreciated();
    }

    @Override
    public boolean isLoaded() {
        return trackKit != null && isEnabled() && RailcraftBlocks.TRACK_OUTFITTED.isLoaded() && RailcraftItems.TRACK_KIT.isLoaded();
    }

    public boolean isDepreciated() {
        return depreciated;
    }

    @Override
    @Nullable
    public ItemStack getStack() {
        return getStack(1);
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty) {
        if (trackKit != null)
            return RailcraftItems.TRACK_KIT.getStack(qty, getTrackKit());
        return null;
    }

    @Nullable
    @Override
    public Optional<IRailcraftObject<TrackKit>> getObject() {
        return null;
    }

    public TrackKit getTrackKit() {
        return trackKit;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    public String getTag() {
        return RailcraftConstants.RESOURCE_DOMAIN + ":" + getBaseTag();
    }

    public int getNumIcons() {
        return numIcons;
    }

    @Nullable
    private ItemStack registerRecipe() {
        if (getStack() == null)
            return null;
        ItemStack output = getStack(recipeOutput * 2);
        Object railWood = RailcraftConfig.vanillaTrackRecipes() ? "slabWood" : RailcraftItems.RAIL.getRecipeObject(EnumRail.WOOD);
        Object railStandard = RailcraftConfig.vanillaTrackRecipes() ? new ItemStack(Items.IRON_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.STANDARD);
        Object railAdvanced = RailcraftConfig.vanillaTrackRecipes() ? new ItemStack(Items.GOLD_INGOT) : RailcraftItems.RAIL.getRecipeObject(EnumRail.ADVANCED);
        Object railSpeed = RailcraftConfig.vanillaTrackRecipes() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.SPEED);
        Object railReinforced = RailcraftConfig.vanillaTrackRecipes() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(EnumRail.REINFORCED);
        Object railElectric = RailcraftConfig.vanillaTrackRecipes() ? "ingotCopper" : RailcraftItems.RAIL.getRecipeObject(EnumRail.ELECTRIC);
        Object woodTie = RailcraftItems.TIE.getRecipeObject(EnumTie.WOOD);
        Object woodRailbed = RailcraftConfig.vanillaTrackRecipes() ? "stickWood" : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.WOOD);
        Object stoneRailbed = RailcraftConfig.vanillaTrackRecipes() ? Blocks.STONE_SLAB : RailcraftItems.RAILBED.getRecipeObject(EnumRailbed.STONE);
        Object reinforcedRailbed = RailcraftConfig.vanillaTrackRecipes() || !RailcraftItems.RAIL.isEnabled() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? new ItemStack(Blocks.OBSIDIAN) : stoneRailbed;
        Object crowbar = IToolCrowbar.ORE_TAG;

        switch (this) {
            case LOCKING:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IbI",
                        "IsI",
                        'I', railAdvanced,
                        's', woodRailbed,
                        'r', "dustRedstone",
                        'b', Blocks.STONE_PRESSURE_PLATE);
                break;
            case ONE_WAY:
                CraftingPlugin.addRecipe(output,
                        "IbI",
                        "IsI",
                        "IpI",
                        'I', railStandard,
                        's', woodRailbed,
                        'b', Blocks.STONE_PRESSURE_PLATE,
                        'p', Blocks.PISTON);
                break;
            case CONTROL:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "GsG",
                        "IrI",
                        'I', railStandard,
                        'G', railAdvanced,
                        's', woodRailbed,
                        'r', "dustRedstone");
                break;
//            case SPEED:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "IsI",
//                        "I I",
//                        'I', railSpeed,
//                        's', stoneRailbed);
//                break;
//            case SPEED_BOOST:
//                CraftingPlugin.addRecipe(output,
//                        "IrI",
//                        "IsI",
//                        "IrI",
//                        'I', railSpeed,
//                        's', stoneRailbed,
//                        'r', "dustRedstone");
//                break;
            case HIGH_SPEED_TRANSITION:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "IrI",
                        "IsI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "IrI",
                        "IrI",
                        'I', railSpeed,
                        's', stoneRailbed,
                        'r', "dustRedstone");
                break;
//            case SPEED_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "IsI",
//                        "III",
//                        "III",
//                        'I', railSpeed,
//                        's', stoneRailbed);
//                break;
            case LAUNCHER:
                CraftingPlugin.addRecipe(output,
                        "IsI",
                        "BPB",
                        "IsI",
                        'I', railReinforced,
                        'B', "blockSteel",
                        's', stoneRailbed,
                        'P', Blocks.PISTON);
                break;
            case PRIMING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "IsI",
                        "IfI",
                        'I', railReinforced,
                        's', stoneRailbed,
                        'p', Blocks.STONE_PRESSURE_PLATE,
                        'f', Items.FLINT_AND_STEEL);
                break;
//            case JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
//            case SLOW:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
            case BOOSTER:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "G#G",
                        "IrI",
                        'G', Items.GOLD_INGOT,
                        'I', railWood,
                        '#', woodRailbed,
                        'r', "dustRedstone");
                break;
//            case SLOW_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case SLOW_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case ELECTRIC:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case ELECTRIC_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railElectric,
//                        '#', stoneRailbed);
//                break;
//            case SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
//            case WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railStandard,
//                        '#', woodRailbed);
//                break;
//            case SLOW_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railWood,
//                        '#', woodRailbed);
//                break;
//            case REINFORCED_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case SPEED_WYE:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "II#",
//                        "III",
//                        'I', railSpeed,
//                        '#', stoneRailbed);
//                break;
            case DISEMBARK:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IrI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        'p', Blocks.STONE_PRESSURE_PLATE);
                break;
            case EMBARKING:
                CraftingPlugin.addRecipe(output,
                        "IpI",
                        "I#I",
                        "IpI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'p', Items.ENDER_PEARL);
                break;
//            case SUSPENDED:
//                CraftingPlugin.addRecipe(output,
//                        "ItI",
//                        "ItI",
//                        "ItI",
//                        'I', railStandard,
//                        't', woodTie);
//                break;
            case DUMPING:
                CraftingPlugin.addRecipe(output,
                        "ItI",
                        "IPI",
                        "ItI",
                        'I', railStandard,
                        'P', RailcraftItems.PLATE, Metal.STEEL,
                        't', woodTie);
                break;
            case BUFFER_STOP:
                CraftingPlugin.addRecipe(output,
                        "I I",
                        "I#I",
                        "IbI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'b', "blockIron");
                break;
//            case DETECTOR_TRAVEL:
//                CraftingPlugin.addRecipe(output,
//                        "IrI",
//                        "I#I",
//                        "IsI",
//                        'I', railStandard,
//                        '#', woodRailbed,
//                        'r', "dustRedstone",
//                        's', Blocks.STONE_PRESSURE_PLATE);
//                break;
            case GATED:
                CraftingPlugin.addRecipe(output,
                        "IgI",
                        "I#I",
                        "IgI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'g', Blocks.OAK_FENCE_GATE);
                break;
//            case GATED_ONE_WAY:
//                CraftingPlugin.addRecipe(output,
//                        "IgI",
//                        "G#G",
//                        "IgI",
//                        'I', railStandard,
//                        '#', woodRailbed,
//                        'g', Blocks.OAK_FENCE_GATE,
//                        'G', railAdvanced);
//                break;
            case COUPLER:
                CraftingPlugin.addRecipe(output,
                        "IcI",
                        "I#I",
                        "IcI",
                        'I', railAdvanced,
                        '#', woodRailbed,
                        'c', crowbar);
                break;
            case WHISTLE:
                CraftingPlugin.addRecipe(output,
                        "IyI",
                        "I#I",
                        "IbI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'y', "dyeYellow",
                        'b', "dyeBlack");
                break;
            case LOCOMOTIVE:
                CraftingPlugin.addRecipe(output,
                        "ILI",
                        "I#I",
                        "ILI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'L', RailcraftItems.SIGNAL_LAMP.getRecipeObject());
                break;
            case THROTTLE:
                CraftingPlugin.addRecipe(output,
                        "IlI",
                        "I#I",
                        "IlI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'l', Items.REPEATER);
                break;
            case ROUTING:
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.TICKET);
                CraftingPlugin.addRecipe(output,
                        "IrI",
                        "I#I",
                        "ItI",
                        'I', railStandard,
                        '#', woodRailbed,
                        'r', "dustRedstone",
                        't', RailcraftItems.TICKET_GOLD);
                break;
//            case REINFORCED:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "I I",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_BOOSTER:
//                CraftingPlugin.addRecipe(output,
//                        "I I",
//                        "I#I",
//                        "IrI",
//                        'I', railReinforced,
//                        'r', "dustRedstone",
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_JUNCTION:
//                CraftingPlugin.addRecipe(output,
//                        "III",
//                        "I#I",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
//            case REINFORCED_SWITCH:
//                CraftingPlugin.addRecipe(output,
//                        "I#I",
//                        "III",
//                        "III",
//                        'I', railReinforced,
//                        '#', reinforcedRailbed);
//                break;
        }
        return output;
    }

}
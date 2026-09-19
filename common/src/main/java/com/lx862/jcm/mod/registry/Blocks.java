package com.lx862.jcm.mod.registry;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.loader.JCMRegistryClient;
import com.lx862.jcm.mod.block.*;
import com.lx862.jcm.mod.data.BlockProperties;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.BrandNewEpicRegistryObject;
import mtr.RegistryObject;
import mtr.block.IBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.lx862.jcm.mod.block.SpotLampBlock.LIT;

public final class Blocks {
    public static final BrandNewEpicRegistryObject<Block> APG_DOOR_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new APGDoorDRL(createProperties(resourceKey)));
    public static final BrandNewEpicRegistryObject<Block> APG_GLASS_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new APGGlassDRL(createProperties(resourceKey)));
    public static final BrandNewEpicRegistryObject<Block> APG_GLASS_END_DRL = new BrandNewEpicRegistryObject<>(resourceKey -> new APGGlassEndDRL(createProperties(resourceKey)));
    public static final BrandNewEpicRegistryObject<Block> AUTO_IRON_DOOR = new BrandNewEpicRegistryObject<>(resourceKey -> new AutoIronDoorBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> BUTTERFLY_LIGHT = new BrandNewEpicRegistryObject<>(resourceKey -> new ButterflyLightBlock(createProperties(resourceKey).strength(3.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> BUFFER_STOP = new BrandNewEpicRegistryObject<>(resourceKey -> new BufferStopBlock(createProperties(resourceKey).lightLevel(state -> 8).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> CEILING_SLANTED = new BrandNewEpicRegistryObject<>(resourceKey -> new CeilingSlantedBlock(createProperties(resourceKey).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_1 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_2 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_3 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_4 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_5 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_6 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f)));
    public static final BrandNewEpicRegistryObject<Block> CIRCLE_WALL_7 = new BrandNewEpicRegistryObject<>(resourceKey -> new CircleWallBlock(createProperties(resourceKey).strength(8.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> DEPARTURE_POLE = new BrandNewEpicRegistryObject<>(resourceKey -> new DeparturePoleBlock(createProperties(resourceKey).strength(3.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> DEPARTURE_TIMER = new BrandNewEpicRegistryObject<>(resourceKey -> new DepartureTimerBlock(createProperties(resourceKey).lightLevel(state -> 4).strength(3.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> EXIT_SIGN_ODD = new BrandNewEpicRegistryObject<>(resourceKey -> new ExitSignOdd(createProperties(resourceKey).lightLevel(state -> 15).strength(1.5f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> EXIT_SIGN_EVEN = new BrandNewEpicRegistryObject<>(resourceKey -> new ExitSignEven(createProperties(resourceKey).lightLevel(state -> 15).strength(1.5f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> FIRE_ALARM = new BrandNewEpicRegistryObject<>(resourceKey -> new FireAlarmWall(createProperties(resourceKey).strength(1.5f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> FARE_SAVER = new BrandNewEpicRegistryObject<>(resourceKey -> new FareSaverBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> HELPLINE_1 = new BrandNewEpicRegistryObject<>(resourceKey -> new WallAttachedHelpLineBlock(createProperties(resourceKey).strength(1.5f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> HELPLINE_2 = new BrandNewEpicRegistryObject<>(resourceKey -> new WallAttachedHelpLineBlock(createProperties(resourceKey).strength(1.5f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> HELPLINE_HKWK = new BrandNewEpicRegistryObject<>(resourceKey -> new WallAttachedHelpLineBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> HELPLINE_STANDING = new BrandNewEpicRegistryObject<>(resourceKey -> new HelpLineStandingBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> HELPLINE_STANDING_EAL = new BrandNewEpicRegistryObject<>(resourceKey -> new HelpLineStandingEALBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> KCR_EMG_STOP_SIGN = new BrandNewEpicRegistryObject<>(resourceKey -> new KCREmergencyStopSign(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> KCR_ENQUIRY_MACHINE = new BrandNewEpicRegistryObject<>(resourceKey -> new KCREnquiryMachineWall(createProperties(resourceKey).lightLevel(state -> 4).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> KCR_STATION_NAME_SIGN = new BrandNewEpicRegistryObject<>(resourceKey -> new KCRStationNameSignBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion(), false));
    public static final BrandNewEpicRegistryObject<Block> KCR_STATION_NAME_SIGN_STATION_COLOR = new BrandNewEpicRegistryObject<>(resourceKey -> new KCRStationNameSignBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion(), true));
    public static final BrandNewEpicRegistryObject<Block> KCR_TRESPASS_SIGN = new BrandNewEpicRegistryObject<>(resourceKey -> new KCRTrespassSignageBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LCD_PIDS = new BrandNewEpicRegistryObject<>(resourceKey -> new LCDPIDSBlock(createProperties(resourceKey).lightLevel(state -> 8).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LIGHT_BLOCK = new BrandNewEpicRegistryObject<>(resourceKey -> new LightBlock(createProperties(resourceKey).lightLevel(state -> IBlock.getStatePropertySafe(state, BlockProperties.LIGHT_LEVEL)).strength(1.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LIGHT_LANTERN = new BrandNewEpicRegistryObject<>(resourceKey -> new LightLanternBlock(createProperties(resourceKey).lightLevel(state -> IBlock.getStatePropertySafe(state, LIT) ? 15 : 0).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LRT_TRESPASS_SIGN = new BrandNewEpicRegistryObject<>(resourceKey -> new LRTTrespassSignageBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LRT_INTER_CAR_BARRIER_LEFT = new BrandNewEpicRegistryObject<>(resourceKey -> new LRTInterCarBarrierBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LRT_INTER_CAR_BARRIER_MIDDLE = new BrandNewEpicRegistryObject<>(resourceKey -> new LRTInterCarBarrierBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> LRT_INTER_CAR_BARRIER_RIGHT = new BrandNewEpicRegistryObject<>(resourceKey -> new LRTInterCarBarrierBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> MTR_ENQUIRY_MACHINE = new BrandNewEpicRegistryObject<>(resourceKey -> new MTREnquiryMachine(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> MTR_ENQUIRY_MACHINE_WALL = new BrandNewEpicRegistryObject<>(resourceKey -> new MTREnquiryMachineWall(createProperties(resourceKey).lightLevel(state -> 4).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> MTR_STAIRS = new BrandNewEpicRegistryObject<>(resourceKey -> new MTRStairsBlock(createProperties(resourceKey).strength(4.0f)));
    public static final BrandNewEpicRegistryObject<Block> MTR_TRESPASS_SIGN = new BrandNewEpicRegistryObject<>(resourceKey -> new MTRTrespassSignageBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> OPERATOR_BUTTON = new BrandNewEpicRegistryObject<>(resourceKey -> new OperatorButtonBlock(createProperties(resourceKey).lightLevel(state -> 5).strength(1.0f).noOcclusion(), 40));
    public static final BrandNewEpicRegistryObject<Block> PIDS_PROJECTOR = new BrandNewEpicRegistryObject<>(resourceKey -> new PIDSProjectorBlock(createProperties(resourceKey).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> PIDS_1A = new BrandNewEpicRegistryObject<>(resourceKey -> new PIDS1ABlock(createProperties(resourceKey)));
    public static final BrandNewEpicRegistryObject<Block> RV_PIDS = new BrandNewEpicRegistryObject<>(resourceKey -> new RVPIDSBlock(createProperties(resourceKey).lightLevel(state -> 8).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> RV_PIDS_SIL_1 = new BrandNewEpicRegistryObject<>(resourceKey -> new RVPIDSSIL1Block(createProperties(resourceKey).lightLevel(state -> 8).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> RV_PIDS_SIL_2 = new BrandNewEpicRegistryObject<>(resourceKey -> new RVPIDSSIL2Block(createProperties(resourceKey).lightLevel(state -> 8).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> RV_PIDS_POLE = new BrandNewEpicRegistryObject<>(resourceKey -> new RVPIDSPole(createProperties(resourceKey).strength(2.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> RV_ENQUIRY_MACHINE = new BrandNewEpicRegistryObject<>(resourceKey -> new RVEnquiryMachine(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> SIL_EMG_STOP_BUTTON = new BrandNewEpicRegistryObject<>(resourceKey -> new SILEmergencyButtonBlock(createProperties(resourceKey).lightLevel(state -> 10).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> SIGNAL_LIGHT_INVERTED_RED_ABOVE = new BrandNewEpicRegistryObject<>(resourceKey -> new InvertedSignalBlockRedAbove(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> SIGNAL_LIGHT_INVERTED_RED_BOTTOM = new BrandNewEpicRegistryObject<>(resourceKey -> new InvertedSignalBlockRedBelow(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> STATIC_SIGNAL_LIGHT_RED_BELOW = new BrandNewEpicRegistryObject<>(resourceKey -> new StaticSignalLightBlockRedBelow(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> STATIC_SIGNAL_LIGHT_RED_TOP = new BrandNewEpicRegistryObject<>(resourceKey -> new StaticSignalLightBlockRedTop(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> STATIC_SIGNAL_LIGHT_GREEN = new BrandNewEpicRegistryObject<>(resourceKey -> new StaticSignalLightBlockGreen(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> STATIC_SIGNAL_LIGHT_BLUE = new BrandNewEpicRegistryObject<>(resourceKey -> new StaticSignalLightBlockBlue(createProperties(resourceKey).strength(1.0f)));
    public static final BrandNewEpicRegistryObject<Block> SPOT_LAMP = new BrandNewEpicRegistryObject<>(resourceKey -> new SpotLampBlock(createProperties(resourceKey).lightLevel(state -> IBlock.getStatePropertySafe(state, LIT) ? 15 : 0).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_NAME_STANDING = new BrandNewEpicRegistryObject<>(resourceKey -> new StationNameStandingBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> SUBSIDY_MACHINE = new BrandNewEpicRegistryObject<>(resourceKey -> new SubsidyMachineBlock(createProperties(resourceKey).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> SOUND_LOOPER = new BrandNewEpicRegistryObject<>(resourceKey -> new SoundLooperBlock(createProperties(resourceKey)));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRL2Block(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL_SINGLE = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRLBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL_STATION_COLOR = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRL2Block(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL_SINGLE_STATION_COLOR = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRLBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL_POLE = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRL2Pole(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> STATION_CEILING_WRL_POLE_SINGLE = new BrandNewEpicRegistryObject<>(resourceKey -> new StationCeilingWRLPole(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> TCL_EMG_STOP_BUTTON = new BrandNewEpicRegistryObject<>(resourceKey -> new TCLEmergencyButtonBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> THALES_TICKET_BARRIER_ENTRANCE = new BrandNewEpicRegistryObject<>(resourceKey -> new ThalesTicketBarrier(createProperties(resourceKey), true));
    public static final BrandNewEpicRegistryObject<Block> THALES_TICKET_BARRIER_EXIT = new BrandNewEpicRegistryObject<>(resourceKey -> new ThalesTicketBarrier(createProperties(resourceKey), false));
    public static final BrandNewEpicRegistryObject<Block> THALES_TICKET_BARRIER_BARE = new BrandNewEpicRegistryObject<>(resourceKey -> new ThalesTicketBarrierBareBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> TML_EMG_STOP_BUTTON = new BrandNewEpicRegistryObject<>(resourceKey -> new TMLEmergencyButtonBlock(createProperties(resourceKey).lightLevel(state -> 15).strength(4.0f).noOcclusion()));
    public static final BrandNewEpicRegistryObject<Block> TRAIN_MODEL_E44 = new BrandNewEpicRegistryObject<>(resourceKey -> new MTRTrainModelBlock(createProperties(resourceKey).strength(0.5f)));
    public static final BrandNewEpicRegistryObject<Block> WATER_MACHINE = new BrandNewEpicRegistryObject<>(resourceKey -> new WaterMachineBlock(createProperties(resourceKey).strength(4.0f).noOcclusion()));

    public static void register() {
        JCMLogger.debug("Registering blocks...");

        JCMRegistry.registerBlock("apg_door_drl", APG_DOOR_DRL);
        JCMRegistry.registerBlock("apg_glass_drl", APG_GLASS_DRL);
        JCMRegistry.registerBlock("apg_glass_end_drl", APG_GLASS_END_DRL);
        JCMRegistry.registerBlockAndItem("auto_iron_door", AUTO_IRON_DOOR, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("butterfly_light", BUTTERFLY_LIGHT, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("buffer_stop", BUFFER_STOP, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("ceiling_slanted", CEILING_SLANTED, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_1", CIRCLE_WALL_1, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_2", CIRCLE_WALL_2, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_3", CIRCLE_WALL_3, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_4", CIRCLE_WALL_4, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_5", CIRCLE_WALL_5, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_6", CIRCLE_WALL_6, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("circle_wall_7", CIRCLE_WALL_7, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("departure_pole", DEPARTURE_POLE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("departure_timer", DEPARTURE_TIMER, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("exit_sign_odd", EXIT_SIGN_ODD, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("exit_sign_even", EXIT_SIGN_EVEN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("fire_alarm", FIRE_ALARM, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("fare_saver", FARE_SAVER, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("helpline_1", HELPLINE_1, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("helpline_2", HELPLINE_2, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("helpline_hkwk", HELPLINE_HKWK, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("helpline_standing", HELPLINE_STANDING, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("helpline_standing_eal", HELPLINE_STANDING_EAL, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("kcr_emg_stop_sign", KCR_EMG_STOP_SIGN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("kcr_enquiry_machine", KCR_ENQUIRY_MACHINE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("kcr_name_sign", KCR_STATION_NAME_SIGN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("kcr_name_sign_station_color", KCR_STATION_NAME_SIGN_STATION_COLOR, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("kcr_trespass_sign", KCR_TRESPASS_SIGN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("lcd_pids", LCD_PIDS, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("light_block", LIGHT_BLOCK, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("light_lantern", LIGHT_LANTERN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("lrt_trespass_sign", LRT_TRESPASS_SIGN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("lrt_inter_car_barrier_left", LRT_INTER_CAR_BARRIER_LEFT, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("lrt_inter_car_barrier_middle", LRT_INTER_CAR_BARRIER_MIDDLE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("lrt_inter_car_barrier_right", LRT_INTER_CAR_BARRIER_RIGHT, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("mtr_enquiry_machine", MTR_ENQUIRY_MACHINE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("mtr_enquiry_machine_wall", MTR_ENQUIRY_MACHINE_WALL, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("mtr_stairs", MTR_STAIRS, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("mtr_trespass_sign", MTR_TRESPASS_SIGN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("operator_button", OPERATOR_BUTTON, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("pids_projector", PIDS_PROJECTOR, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("pids_1a", PIDS_1A, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("rv_pids", RV_PIDS, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("rv_pids_sil_1", RV_PIDS_SIL_1, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("rv_pids_sil_2", RV_PIDS_SIL_2, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("rv_pids_pole", RV_PIDS_POLE, ItemGroups.PIDS);
        JCMRegistry.registerBlockAndItem("rv_enquiry_machine", RV_ENQUIRY_MACHINE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("sil_emg_stop_button", SIL_EMG_STOP_BUTTON, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_inverted_1", SIGNAL_LIGHT_INVERTED_RED_ABOVE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_inverted_2", SIGNAL_LIGHT_INVERTED_RED_BOTTOM, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_red_1", STATIC_SIGNAL_LIGHT_RED_BELOW, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_red_2", STATIC_SIGNAL_LIGHT_RED_TOP, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_green", STATIC_SIGNAL_LIGHT_GREEN, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("signal_light_blue", STATIC_SIGNAL_LIGHT_BLUE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("spot_lamp", SPOT_LAMP, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_name_standing", STATION_NAME_STANDING, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("subsidy_machine", SUBSIDY_MACHINE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("sound_looper", SOUND_LOOPER, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl", STATION_CEILING_WRL, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl_single", STATION_CEILING_WRL_SINGLE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl_station_color", STATION_CEILING_WRL_STATION_COLOR, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl_single_station_color", STATION_CEILING_WRL_SINGLE_STATION_COLOR, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl_pole", STATION_CEILING_WRL_POLE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("station_ceiling_wrl_single_pole", STATION_CEILING_WRL_POLE_SINGLE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("tcl_emg_stop_button", TCL_EMG_STOP_BUTTON, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("thales_ticket_barrier_entrance", THALES_TICKET_BARRIER_ENTRANCE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("thales_ticket_barrier_exit", THALES_TICKET_BARRIER_EXIT, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("thales_ticket_barrier_bare", THALES_TICKET_BARRIER_BARE, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("tml_emg_stop_button", TML_EMG_STOP_BUTTON, ItemGroups.MAIN);
        JCMRegistry.registerBlockAndItem("train_model_e44", TRAIN_MODEL_E44, ItemGroups.MAIN);

        JCMRegistry.registerBlockAndItem("water_machine", WATER_MACHINE, ItemGroups.MAIN);
        // Calling this method cause the static class to be loaded, in turn registering the content.
    }

    public static void registerClient() {
        /* Station Colored Blocks */
        JCMRegistryClient.registerStationColoredBlock(
            KCR_STATION_NAME_SIGN_STATION_COLOR,
            STATION_NAME_STANDING,
            STATION_CEILING_WRL_STATION_COLOR,
            STATION_CEILING_WRL_SINGLE_STATION_COLOR
        );
    }

    static BlockBehaviour.Properties createProperties(ResourceKey<Block> resourceKey) {
        return BlockBehaviour.Properties.of().setId(resourceKey).forceSolidOn();
    }
}

package me.sebastian420.PandaAC.data;

public class SpeedLimits {


    public static final double FUDGE = 1.0;
    public static final double CRAWLING = 1.0;
    public static final double SNEAKING = 1.6;
    public static final double WALKING = 4.317;
    public static final double SPRINT = 5.612;
    public static final double SPRINT_AND_JUMP = 7.127;
    public static final double SPRINT_AND_JUMP_PASSAGE = 9.346;
    public static final double SPRINT_ON_ICE = 10;
    public static final double SPRINT_ON_BLUE_ICE = 13;
    public static final double SPRINT_AND_JUMP_PASSAGE_ICE = 14;
    public static final double SPRINT_AND_JUMP_PASSAGE_BLUE_ICE = 18;


    public static final double UP_SPEED_CLIMB = 3;
    public static final double UP_SPEED = 8;
    public static final double UP_SPEED_STAIRS = 12;

    //Probably should have a seperate/vertical/hor
    public static final double SWIM_SPEED_HORIZONTAL_WATER = 6;
    public static final double SWIM_SPEED_HORIZONTAL_LAVA = 3;

    //This has to be quite high for when you slam into liquid from high up
    //Better to use potential that gained from velocity while in air
    //Or even simpler, only check water vertical speed after you have been in it for x amount of time
    public static final double SWIM_SPEED_VERTICAL_WATER_DOWN = 150;
    public static final double SWIM_SPEED_VERTICAL_LAVA_DOWN= 100;

    public static final double SWIM_SPEED_VERTICAL_WATER_UP = 16;
    public static final double SWIM_SPEED_VERTICAL_LAVA_UP= 8;


    //Boat sped fucky when smahsing into others
    public static final double BOAT_LAND = 3.5;
    public static final double BOAT_WATER = 7.0;
    public static final double BOAT_AIR = 8.0;
    public static final double BOAT_ICE = 40.0;
    public static final double BOAT_BLUE_ICE = 80.0;

    public static final double BOAT_YAW_LAND = 75;
    public static final double BOAT_YAW_WATER = 200;
    public static final double BOAT_YAW_ICE = 9999999;


}

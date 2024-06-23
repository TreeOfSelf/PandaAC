package me.sebastian420.PandaAC.modules.movement.entity;

import me.sebastian420.PandaAC.events.*;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static java.lang.Math.abs;
import static net.minecraft.world.Heightmap.Type.WORLD_SURFACE;

public class BoatFlyCheck extends PAModule implements VehicleMoveListener, PlayerStartRidingListener {

    public boolean voidCheck(PlayerEntity player) {
        World world = player.getWorld();

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        for (int i = (int) (x - 32); i <= (int) (x + 32); i++) {
            for (int k = (int) (z - 32); k <= (int) (z + 32); k++) {
                WorldChunk chunk = (WorldChunk) world.getChunk(new BlockPos((int) i, 0, (int) k));
                int highestBlock = chunk.sampleHeightmap(WORLD_SURFACE, (int) i, (int) k);



                if (highestBlock != -65) {
                    return false; // non-empty block found, return false
                }
            }

        }
        return true; // no non-empty blocks found, player is flying in empty space
    }

    public static boolean hasIceBlockNearby(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        World world = player.getWorld();
        for (int x = playerPos.getX() - 2; x <= playerPos.getX() + 2; x++) {
            for (int y = playerPos.getY() - 2; y <= playerPos.getY() + 2; y++) {
                for (int z = playerPos.getZ() - 2; z <= playerPos.getZ() + 2; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public BoatFlyCheck() {
        super("boat_fly_check");
        VehicleMoveListener.EVENT.register(this);
        PlayerStartRidingListener.EVENT.register(this);
    }

    @Override
    public long getFlagCoolDownMs() {
        return 100;
    }

    @Override
    public void onVehicleMove(PAPlayer player, PAEntity vehicle, PlayerMoveC2SPacketView playerLook, PlayerInputC2SPacket playerInput, VehicleMoveC2SPacket vehicleMoveC2SPacket, @Nullable VehicleMoveC2SPacket lastVehicleMoveC2SPacket) {
        if (vehicle != null && lastVehicleMoveC2SPacket != null) {


            boatData data = player.getOrCreateData(boatData.class, boatData::new);

            if(System.currentTimeMillis() - data.lastInVehicle<500) {
                return;
            }

            data.lastInVehicle = System.currentTimeMillis();

            if(System.currentTimeMillis() - data.time<300) {
                return;
            }



            data.time = System.currentTimeMillis();


            double ydelta = vehicleMoveC2SPacket.getY() - lastVehicleMoveC2SPacket.getY() - 0.001;
            double xdelta = abs(vehicleMoveC2SPacket.getX() - lastVehicleMoveC2SPacket.getX());
            double zdelta = abs(vehicleMoveC2SPacket.getZ() - lastVehicleMoveC2SPacket.getZ());
            Box box = vehicle.getBoxForPosition(vehicleMoveC2SPacket.getX(), vehicleMoveC2SPacket.getY(), vehicleMoveC2SPacket.getZ());


            double speed = Math.sqrt(Math.pow(xdelta,2)+Math.pow(zdelta,2));

            String name = vehicle.asMcEntity().getName().getString();

            double maxSpeed = 0.8;

            //LoggerThread.info(name);

            if (name.contains("Boat")) {
                maxSpeed = 0.12;
                if(hasIceBlockNearby(player.asMcPlayer())) {
                    data.lastIce = System.currentTimeMillis();

                }

                if(System.currentTimeMillis() - data.lastIce < 5000) {
                    maxSpeed = 3;
                }
            }

            if (name.contains("Donkey") || name.contains("Mule")){
                maxSpeed = 0.4;
            }

            if (name.contains("Pig")){
                maxSpeed = 0.2;
            }

            if (name.contains("Camel")){
                maxSpeed = 0.2;
            }

            if (name.contains("Llama")){
                maxSpeed = 0.2;
            }

            if (name.contains("Strider")){
                maxSpeed = 0.1;
            }



            if(vehicle.getUuid() != data.boatuuid){
                data.boatuuid = vehicle.getUuid();
                return;
            }

            //LoggerThread.info("Entity Speed: "+(speed)+"/"+(maxSpeed));

            if(voidCheck(player.asMcPlayer())){
                vehicle.asMcEntity().damage(vehicle.getWorld().getDamageSources().generic(), 1000);
                player.asMcPlayer().kill();
            }

            boolean hit = false;
            if(speed > maxSpeed){
                hit = true;
                data.violations+=1;
                if(data.violations>=5) {
                    synchronized (this) {
                        vehicle.asMcEntity().damage(vehicle.getWorld().getDamageSources().generic(), 1000);
                    }
                    data.violations=0;
                }
            }else {
                //Flying
                if (((vehicle.getStepHeight() == 0 && ydelta > 0 && vehicle.getVelocity().getY() <= 0))) {
                    if(!(player.getWorld().getTime() - vehicle.getPistonMovementTick() < 1000 || BlockCollisionUtil.isTouching(box, player.getWorld(), BlockCollisionUtil.LIQUID))){
                        vehicle.asMcEntity().damage(vehicle.getWorld().getDamageSources().generic(), 1000);
                        hit = true;
                    }

                }else{
                    data.violations-=1;
                    if(data.violations<0){
                        data.violations=0;
                    }
                }
            }

            if(!hit){
                data.lastX = vehicleMoveC2SPacket.getX();
                data.lastY = vehicleMoveC2SPacket.getY();
                data.lastZ = vehicleMoveC2SPacket.getZ();
            }


        }

    }
    @Override
    public void onStartRiding(PAPlayer player, PAEntity vehicle) {
        boatData data = player.getOrCreateData(boatData.class, boatData::new);
        data.lastInVehicle = System.currentTimeMillis();
    }

    public static class boatData {
        long time = System.currentTimeMillis();
        public long lastIce = System.currentTimeMillis();
        double lastX,lastY,lastZ;

        double violations = 0;

        long lastInVehicle;

        UUID boatuuid;

    }
    
}

package me.sebastian420.PandaAC.util;

import com.mojang.datafixers.util.Pair;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CollisionUtil {
    private CollisionUtil() { }

    /**
     * @return If box2 intersects the top of box1
     */
    public static boolean intersectsTop(@Nullable Box box1, @Nullable Box box2) {
        if (box1 == null || box2 == null) return false;
        return box1.maxY <= box2.maxY && box1.maxY >= box2.minY;
    }

    public static Pair<BiPredicate<World, BlockPos>, Predicate<PAEntity>> touchingRigidTopPredicates(Box box) {
        return new Pair<>(BlockCollisionUtil.touchingPredicate(box), EntityCollisionUtil.touchingRigidTopPredicate(box));
    }

    public static Pair<BiPredicate<World, BlockPos>, Predicate<PAEntity>> steppablePredicates(float stepheight) {
        return new Pair<>(BlockCollisionUtil.steppablePredicate(stepheight), EntityCollisionUtil.steppablePredicate(stepheight));
    }

    public static boolean isNearby(PAPlayer player, double posx, double posy, double posz, double expandHorizontal, double expandVertical, Pair<BiPredicate<World, BlockPos>, Predicate<PAEntity>> predicates) {
        return isTouching(player, player.getBoxForPosition(posx, posy, posz).expand(expandHorizontal, expandVertical, expandHorizontal), player.getWorld(), predicates);
    }

    public static boolean isTouching(@Nullable PAEntity excludeEntity, Box box, World world, Pair<BiPredicate<World, BlockPos>, Predicate<PAEntity>> predicates) {
        return BlockCollisionUtil.isTouching(box, world, predicates.getFirst()) || EntityCollisionUtil.isTouching(excludeEntity, box, world, predicates.getSecond());
    }

    public static boolean isTouching(@NotNull PAEntity[] excludeEntites, Box box, World world, Pair<BiPredicate<World, BlockPos>, Predicate<PAEntity>> predicates) {
        return BlockCollisionUtil.isTouching(box, world, predicates.getFirst()) || EntityCollisionUtil.isTouching(excludeEntites, box, world, predicates.getSecond());
    }
}

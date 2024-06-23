package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.objects.entity.PAEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class EntityCollisionUtil {
    private EntityCollisionUtil() { }

    public static Predicate<PAEntity> touchingRigidTopPredicate(Box box) {
        return entity -> CollisionUtil.intersectsTop(entity.getRigidCollision(), box);
    }

    public static Predicate<PAEntity> steppablePredicate(float stepheight) {
        return entity -> {
            Box box = entity.getRigidCollision();
            if (box == null) return false;
            return box.maxY - entity.getY() <= stepheight;
        };
    }

    public static boolean isTouching(@Nullable PAEntity excludeEntity, Box box, World world, Predicate<PAEntity> predicate) {
        if(excludeEntity == null || predicate == null) return false;


        for (Entity e : world.getOtherEntities(excludeEntity == null ? null : excludeEntity.asMcEntity(), box, null)) {
            if(e != null) {
                if (predicate.test(PAEntity.of(e))) return true;
            }
        }
        return false;
    }

    public static boolean isTouching(PAEntity[] excludeEntites, Box box, World world, Predicate<PAEntity> predicate) {
        for (Entity e : world.getOtherEntities(null, box, null)) {
            PAEntity e1 = PAEntity.of(e);
            if (ArrayUtils.contains(excludeEntites, e1)) continue;

            if(e1 == null || predicate == null || excludeEntites == null) return false;

            if (predicate.test(e1)) return true;
        }
        return false;
    }
}

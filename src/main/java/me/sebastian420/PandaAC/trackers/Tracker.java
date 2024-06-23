package me.sebastian420.PandaAC.trackers;

import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.trackers.data.Data;
import org.jetbrains.annotations.NotNull;

public abstract class Tracker<T extends Data> {
    private Class<T> clazz;

    protected Tracker(Class<T> clazz) {
        this.clazz = clazz;
    }

    public final Class<T> getClazz() {
        return clazz;
    }

    @NotNull
    public abstract T get(PAEntity entity);
}

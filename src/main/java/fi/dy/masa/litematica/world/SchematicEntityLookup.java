package fi.dy.masa.litematica.world;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;

public class SchematicEntityLookup<T extends EntityLike> implements EntityLookup<T>, AutoCloseable
{
    private final List<T> list;

    protected SchematicEntityLookup()
    {
        this.list = new ArrayList<>();
    }

    protected void put(T entity)
    {
        T tmp = this.get(entity.getUuid());

        if (tmp != null)
        {
            this.remove(entity.getUuid());
        }

        synchronized (this.list)
        {
            this.list.add(entity);
        }
    }

    protected int size()
    {
        return this.list.size();
    }

    protected void remove(UUID uuid)
    {
        synchronized (this.list)
        {
            this.list.removeIf(e -> e.getUuid().equals(uuid));
        }
    }

    @Override
    public @Nullable T get(int id)
    {
        for (T e : this.list)
        {
            if (e.getId() == id)
            {
                return e;
            }
        }

        return null;
    }

    @Override
    public @Nullable T get(UUID uuid)
    {
        for (T e : this.list)
        {
            if (e.getUuid().equals(uuid))
            {
                return e;
            }
        }

        return null;
    }

    @Override
    public Iterable<T> iterate()
    {
        return Iterables.concat(this.list);
    }

    @Override
    public void forEachIntersects(Box box, Consumer action)
    {
        // NO-OP
    }

    @Override
    public void forEachIntersects(TypeFilter filter, Box box, LazyIterationConsumer consumer)
    {
        // NO-OP
    }

    @Override
    public void forEach(TypeFilter filter, LazyIterationConsumer consumer)
    {
        // NO-OP
    }

    @Override
    public void close() throws Exception
    {
        this.list.clear();
    }
}

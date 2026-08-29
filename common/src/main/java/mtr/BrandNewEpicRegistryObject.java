package mtr;

import net.minecraft.resources.ResourceKey;

import java.util.function.Function;
import java.util.function.Supplier;

public class BrandNewEpicRegistryObject<T> implements Supplier<T> {

    private T object;
    private ResourceKey<T> resourceKey;
    private final Function<ResourceKey<T>, T> function;

    public BrandNewEpicRegistryObject(Function<ResourceKey<T>, T> function) {
        this.function = function;
    }

    public void setResourceKey(ResourceKey<T> resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public T get() {
        if(this.resourceKey == null) throw new IllegalStateException("MTR Registry: ResourceKey not set!");

        if (object == null) {
            object = function.apply(this.resourceKey);
        }
        return object;
    }
}
package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.render.Element;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.function.Function;

public class EntitiesSetting extends DictionarySetting<EntityType<?>> {

    public EntitiesSetting(String name, List<EntityType<?>> values, Function<EntityType<?>, String> definition, Function<String, EntityType<?>> lookup) {
        super(name, values, definition, lookup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends DictionarySetting.Builder<EntityType<?>> {
        @Override
        protected DictionarySetting<EntityType<?>> buildSetting() {
            return new DictionarySetting<>(name, Registries.ENTITY_TYPE.stream().toList(), e -> e.getName().getString(), key -> {
                for (var bl : Registries.ENTITY_TYPE)
                    if (bl.getName().getString().equals(key))
                        return bl;
                return null;
            });
        }
    }
}

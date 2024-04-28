package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.render.Element;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.function.Function;

public class ItemsSetting extends DictionarySetting<Item> {

    public ItemsSetting(String name, List<Item> values, Function<Item, String> definition, Function<String, Item> lookup) {
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

    public static class Builder extends DictionarySetting.Builder<Item> {
        @Override
        protected DictionarySetting<Item> buildSetting() {
            return new DictionarySetting<>(name, Registries.ITEM.stream().toList(), v -> v.getName().getString(), key -> {
                for (var bl : Registries.ITEM)
                    if (bl.getName().getString().equals(key))
                        return bl;
                return null;
            });
        }
    }
}

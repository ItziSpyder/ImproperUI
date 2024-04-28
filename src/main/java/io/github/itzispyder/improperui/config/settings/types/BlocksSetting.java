package io.github.itzispyder.improperui.config.settings.types;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.function.Function;

public class BlocksSetting extends DictionarySetting<Block> {

    public BlocksSetting(String name, List<Block> values, Function<Block, String> definition, Function<String, Block> lookup) {
        super(name, values, definition, lookup);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends DictionarySetting.Builder<Block> {
        @Override
        protected DictionarySetting<Block> buildSetting() {
            return new DictionarySetting<>(name, Registries.BLOCK.stream().toList(), v -> v.getName().getString(), key -> {
                for (var bl : Registries.BLOCK)
                    if (bl.getName().getString().equals(key))
                        return bl;
                return null;
            });
        }
    }
}

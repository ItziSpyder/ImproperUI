package io.github.itzispyder.improperui.config.settings;

import io.github.itzispyder.improperui.config.settings.types.*;

public interface SettingContainer {

    default IntegerSetting.Builder createIntSetting() {
        return IntegerSetting.create();
    }

    default DoubleSetting.Builder createDoubleSetting() {
        return DoubleSetting.create();
    }

    default BooleanSetting.Builder createBoolSetting() {
        return BooleanSetting.create();
    }

    default KeybindSetting.Builder createBindSetting() {
        return KeybindSetting.create();
    }

    default StringSetting.Builder createStringSetting() {
        return StringSetting.create();
    }

    default ColorSetting.Builder createColorSetting() {
        return ColorSetting.create();
    }

    default BlocksSetting.Builder createBlocksSetting() {
        return BlocksSetting.create();
    }

    default EntitiesSetting.Builder createEntitiesSetting() {
        return EntitiesSetting.create();
    }

    default ItemsSetting.Builder createItemsSetting() {
        return ItemsSetting.create();
    }

    default <T> DictionarySetting.Builder<T> createDictionarySetting(Class<T> keyType) {
        return DictionarySetting.create(keyType);
    }

    default <T extends Enum<?>> EnumSetting.Builder<T> createEnumSetting(Class<T> type) {
        return EnumSetting.create(type);
    }
}

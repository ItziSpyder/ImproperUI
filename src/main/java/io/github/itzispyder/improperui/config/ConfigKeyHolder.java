package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.render.Element;

import java.util.function.Function;

public interface ConfigKeyHolder {

    Function<Element, ConfigKey> ELEMENT_KEY_HOLDER = element -> {
        String regex = "[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+:[a-zA-Z0-9.-]+";
        for (var s : element.classList)
            if (s.matches(regex))
                return new ConfigKey(s);
        return null;
    };

    ConfigKey getConfigKey();
}

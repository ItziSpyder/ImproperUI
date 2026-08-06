package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.render.Element;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ConfigKeyHolder {

    Function<Element, ConfigKey> ELEMENT_KEY_HOLDER = element -> {
        String regex = "((?<modId>\\w+):)?(?<path>.+):(?<key>.+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        for (String attr: element.classList) {
            matcher = pattern.matcher(attr);
            if (!matcher.matches())
                continue;

            return new ConfigKey(matcher.group("modId"),
                    matcher.group("path"),
                    matcher.group("key"));
        }
        return null;
    };

    ConfigKey getConfigKey();
}

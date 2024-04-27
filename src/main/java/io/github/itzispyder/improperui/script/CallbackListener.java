package io.github.itzispyder.improperui.script;

public interface CallbackListener {

    default <E extends Event> void runCallbacks(String methodName, E target) {
        if (methodName == null || methodName.trim().isEmpty())
            return;

        for (var method : this.getClass().getDeclaredMethods()) {
            if (!methodName.equals(method.getName()))
                continue;
            try {
                if (method.getAnnotation(CallbackHandler.class) == null)
                    error("specified callback method must have annotation: @io.github.itzispyder.improperui.script.CallbackHandler");
                if (method.getParameterCount() == 0)
                    error("specified callback method must have one Event parameter");

                var params = method.getParameters();

                if (params[0].getType() != target.getClass())
                    continue;

                method.setAccessible(true);
                method.invoke(this, target);
            }
            catch (Exception ex) {
                error("encountered error invoking method: %s", ex.getMessage());
            }
            return;
        }
        error("method \"%s.%s()\" not found!", this.getClass().getSimpleName(), methodName);
    }

    default void error(String message, Object... args) {
        throw new IllegalArgumentException(message.formatted(args));
    }
}

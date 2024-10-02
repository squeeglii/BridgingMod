package me.cg360.mod.bridging.config.helper;

import me.cg360.mod.bridging.BridgingMod;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;

public class DefaultValueTracker {

    private HashMap<String, Object> defaultValues = null;

    public void saveDefaults() {
        if(this.defaultValues != null) {
            BridgingMod.getLogger().warn("Tried to re-save the defaults variables for object. These are locked!");
            return;
        }

        this.defaultValues = new HashMap<>();

        // Try to get the value of every field and store it.
        for(Field field: this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                this.defaultValues.put(field.getName(), value);
            } catch (Exception err) {
                BridgingMod.getLogger().warn("Unable to get value when saving defaults! Unexpected! [%s]".formatted(err.getMessage()));
                return;
            }
        }
    }

    public Optional<Object> getDefaultForField(Field field) {
        if(this.defaultValues == null) return Optional.empty();

        Object val = this.defaultValues.get(field.getName());
        return Optional.ofNullable(val);
    }

}

package me.cg360.mod.bridging.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.helper.*;
import me.cg360.mod.bridging.util.ReflectSupport;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class BridgingConfigUI {

    public static String DEFAULT_CATEGORY_NAME = "other".trim().toLowerCase(); // enforce lowercase.


    public static Optional<YetAnotherConfigLib> buildConfig() {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder();

        Map<String, List<Field>> sortedCategories = sortConfigIntoCategories(BridgingConfig.class);

        for(String categoryName : sortedCategories.keySet()) {
            List<Field> categoryOptions = sortedCategories.get(categoryName);
            ConfigCategory category = createCategory(categoryName, categoryOptions);

            builder.category(category);
        }

        return Optional.ofNullable(builder.build());
    }

    @SuppressWarnings("unchecked")
    private static <T> Option<T> createOption(Field field, Function<Option<T>, ControllerBuilder<T>> controllerBuilder) {
        String id = field.getName();

        String nameTranslation = ConfigUtil.TRANSLATION_OPTION_NAME.formatted(id);

        BridgingConfig instance = BridgingConfig.HANDLER.instance();
        Object defaultValue = instance.getDefaultForField(field);
        T castedDefault = defaultValue == null ? null : (T) defaultValue; // trust.

        Option.Builder<T> option = Option.<T>createBuilder()
                .name(Component.translatable(nameTranslation))
                .binding(castedDefault,
                        () -> {
                            try {
                                Object val = field.get(instance);
                                return val == null
                                        ? castedDefault
                                        : (T) val;
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        val -> {
                            try {
                                field.set(instance, val);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .controller(controllerBuilder);

        IncludeDescription[] descriptionNotation = field.getAnnotationsByType(IncludeDescription.class);
        IncludeImage[] imageNotation = field.getAnnotationsByType(IncludeImage.class);

        boolean hasDescription = descriptionNotation.length > 0;
        boolean hasImage = imageNotation.length > 0;

        if(hasDescription || hasImage) {
            OptionDescription.Builder desc = OptionDescription.createBuilder();

            if(hasDescription) {
                String descTranslation = ConfigUtil.TRANSLATION_OPTION_DESCRIPTION.formatted(id);
                desc.text(Component.translatable(descTranslation));
            }

            if(hasImage) {
                String path = imageNotation[0].value();
                Path checkedPath = Path.of(path);
                ResourceLocation uid = BridgingMod.id("%s_%s".formatted(
                        id, path
                ));

                desc.image(checkedPath, uid);
            }

            option.description(desc.build());
        }

        return option.build();
    }

    private static ConfigCategory createCategory(String categoryName, List<Field> categoryOptions) {
        ConfigCategory.Builder category = ConfigCategory.createBuilder();
        String translatedName = ConfigUtil.TRANSLATION_CATEGORY_NAME.formatted(categoryName);
        category.name(Component.translatable(translatedName));

        for(Field field : categoryOptions) {

            // If it's a primitive, box it in its object type to make isAssignableFrom more reliable.
            Class<?> type = ReflectSupport.boxPrimitive(field.getType());

            if(Boolean.class.isAssignableFrom(type)) {
                category.option(createOption(field, TickBoxControllerBuilder::create));
                continue;
            }

            //TODO: Support more types.

        }

        return category.build();
    }

    /** Get all the valid config fields in BridgingConfig*/
    private static Map<String, List<Field>> sortConfigIntoCategories(Class<?> configClass) {
        Field[] fields = configClass.getDeclaredFields();
        Map<String, List<Field>> sortedCategories = new HashMap<>(fields.length);

        for(Field field : fields) {

            // check if field is actually a config entry that should be visible
            int modifiers = field.getModifiers();

            if(Modifier.isFinal(modifiers)) continue;
            if(Modifier.isStatic(modifiers)) continue;
            if(field.getAnnotationsByType(HideInConfigUI.class).length > 0) continue;

            // Now we do some sorting!
            Category[] foundCategories = field.getAnnotationsByType(Category.class);

            // If a category tag is found sort the field into that category (or use the default name if value is null)
            // Else just chuck it into the default category.
            // Categories are only really used for tabs.
            String categoryName;

            if(foundCategories.length > 0) {
                String firstCatName = foundCategories[0].value();
                categoryName = firstCatName == null
                        ? DEFAULT_CATEGORY_NAME
                        : firstCatName.trim().toLowerCase();
            } else {
                categoryName = DEFAULT_CATEGORY_NAME;
            }

            // If this is the first time the category has came up, make a new list to sort into.
            if(!sortedCategories.containsKey(categoryName)) {
                sortedCategories.put(categoryName, new LinkedList<>());
            }

            sortedCategories.get(categoryName).add(field);
        }

        return sortedCategories;
    }

}

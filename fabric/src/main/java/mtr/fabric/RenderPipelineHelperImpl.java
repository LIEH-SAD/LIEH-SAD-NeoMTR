package mtr.fabric;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class RenderPipelineHelperImpl {

    public static RenderPipeline.Builder asBuilder(RenderPipeline pipeline) {
        try {
            Constructor<RenderPipeline.Builder> constructor = RenderPipeline.Builder.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            RenderPipeline.Builder builder = constructor.newInstance();

            for (Field pipelineField : RenderPipeline.class.getDeclaredFields()) {
                pipelineField.setAccessible(true);
                String fieldName = pipelineField.getName();
                Object value = pipelineField.get(pipeline);

                try {
                    Field builderField = RenderPipeline.Builder.class.getDeclaredField(fieldName);
                    builderField.setAccessible(true);

                    if (builderField.getType().equals(Optional.class) && !(value instanceof Optional)) {
                        builderField.set(builder, Optional.ofNullable(value));
                    } else {
                        builderField.set(builder, value);
                    }
                } catch (NoSuchFieldException e) {
                    // Fallback to setter method if direct field access fails
                    try {
                        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                        Method setter = RenderPipeline.Builder.class.getMethod(setterName, pipelineField.getType());
                        setter.invoke(builder, value);
                    } catch (Exception ex) {
                        // Ignore if no field or setter is found
                    }
                } catch (Exception e) {
                    // Ignore other reflection errors
                }
            }

            return builder;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create and populate RenderPipeline.Builder via reflection", e);
        }
    }
}
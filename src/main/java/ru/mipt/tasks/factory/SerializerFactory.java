package ru.mipt.tasks.factory;

import net.openhft.compiler.CompilerUtils;
import ru.mipt.tasks.factory.interfaces.Serializer;

import java.lang.reflect.Field;

public class SerializerFactory {

    public static <T> Serializer<T> createSerializer(Class<T> clazz, String format) throws Exception {
        String packageName = clazz.getPackage().getName();
        String serializerClassName = clazz.getSimpleName() + format.toUpperCase() + "Serializer";
        String sourceCode = generateSourceCode(clazz, format);

        Class<?> serializerClass = CompilerUtils.CACHED_COMPILER.loadFromJava(packageName + "." + serializerClassName, sourceCode);

        if (Serializer.class.isAssignableFrom(serializerClass)) {
            return (Serializer<T>) serializerClass.getDeclaredConstructor().newInstance();
        } else {
            throw new RuntimeException("Сгенерированный класс не реализует интерфейс сериализатора");
        }
    }

    private static String generateSourceCode(Class<?> clazz, String format) {
        StringBuilder sourceCode = new StringBuilder();

        sourceCode.append("package ")
                .append(clazz.getPackage().getName())
                .append(";\n")
                .append("public class ")
                .append(clazz.getSimpleName())
                .append(format.toUpperCase())
                .append("Serializer implements ru.mipt.tasks.factory.interfaces.Serializer<")
                .append(clazz.getName())
                .append(">{\n");

        sourceCode.append("public String serialize(")
                .append(clazz.getName())
                .append(" object) {\n");

        if ("json".equalsIgnoreCase(format)) {
            if ("json".equalsIgnoreCase(format)) {
                sourceCode.append("return \"{\\n\" +\n");
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    String getterName = "get" + Character.toUpperCase(fields[i].getName().charAt(0)) + fields[i].getName().substring(1);
                    sourceCode.append("\"    \\\"")
                            .append(fields[i].getName())
                            .append("\\\": \\\"\" + object.")
                            .append(getterName)
                            .append("() + \"\\\"");
                    if (i != fields.length - 1) {
                        sourceCode.append(",\\n\" +\n");
                    } else {
                        sourceCode.append("\\n\" + \"}\";\n");
                    }
                }
            }
        } else if ("xml".equalsIgnoreCase(format)) {
            if ("xml".equalsIgnoreCase(format)) {
                sourceCode.append("return \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?>\\n<")
                        .append(clazz.getSimpleName())
                        .append(">\\n\" +\n");
                for (Field field : clazz.getDeclaredFields()) {
                    String getterName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    sourceCode.append("\"    <")
                            .append(field.getName())
                            .append(">\" + object.")
                            .append(getterName)
                            .append("() + \"</")
                            .append(field.getName())
                            .append(">\\n\" +\n");
                }
                sourceCode.append("\"</")
                        .append(clazz.getSimpleName())
                        .append(">\";\n");
            }
        }

        sourceCode.append("}\n");
        sourceCode.append("}\n");

        return sourceCode.toString();
    }
}


package ru.mipt.tasks.factory;

import net.openhft.compiler.CompilerUtils;
import ru.mipt.tasks.factory.interfaces.Serializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

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

    private static String generateSourceCode(Class<?> clazz, String format) throws Exception {
        StringBuilder sourceCode = new StringBuilder();

        sourceCode.append("package ")
                .append(clazz.getPackage().getName())
                .append(";\n")
                .append("import java.util.List;\n")
                .append("public class ")
                .append(clazz.getSimpleName())
                .append(format.toUpperCase())
                .append("Serializer implements ru.mipt.tasks.factory.interfaces.Serializer<")
                .append(clazz.getName())
                .append(">{\n");

        sourceCode.append("public String serialize(")
                .append(clazz.getName())
                .append(" object) {\n");

        Field[] fields = clazz.getDeclaredFields();

        if ("json".equalsIgnoreCase(format)) {
            jsonGenerator(format, sourceCode, fields);
        } else if ("xml".equalsIgnoreCase(format)) {
            xmlGenerator(clazz, format, sourceCode, fields);
        }

        sourceCode.append("}\n");
        sourceCode.append("}\n");

        return sourceCode.toString();
    }

    private static void xmlGenerator(Class<?> clazz, String format, StringBuilder sourceCode, Field[] fields) throws Exception {
        sourceCode.append("StringBuilder sb = new StringBuilder();\n")
                .append("sb.append(\"<").append(clazz.getSimpleName()).append(">\");\n");
        for (Field field : fields) {
            String getterName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            sourceCode.append("sb.append(\"<").append(field.getName()).append(">\");\n");
            if (field.getType().equals(List.class)) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                createSerializerIfNotExists(listClass, format);
                sourceCode.append("for (Object item : object.")
                        .append(getterName)
                        .append("()) {\n")
                        .append("sb.append(new ")
                        .append(listClass.getSimpleName())
                        .append(format.toUpperCase())
                        .append("Serializer().serialize((")
                        .append(listClass.getSimpleName())
                        .append(") item));\n")
                        .append("}\n");
            } else if (field.getType().isPrimitive() || field.getType().equals(String.class) || field.getType().equals(Long.class) || field.getType().equals(Integer.class)) {
                sourceCode.append("sb.append(object.")
                        .append(getterName)
                        .append("());\n");
            } else {
                createSerializerIfNotExists(field.getType(), format);
                sourceCode.append("sb.append(new ")
                        .append(field.getType().getSimpleName())
                        .append(format.toUpperCase())
                        .append("Serializer().serialize(object.")
                        .append(getterName)
                        .append("()));\n");
            }
            sourceCode.append("sb.append(\"</").append(field.getName()).append(">\");\n");
        }
        sourceCode.append("sb.append(\"</").append(clazz.getSimpleName()).append(">\");\n")
                .append("return sb.toString();\n");
    }

    private static void jsonGenerator(String format, StringBuilder sourceCode, Field[] fields) throws Exception {
        sourceCode.append("StringBuilder sb = new StringBuilder();\n")
                .append("sb.append(\"{\");\n");
        for (int i = 0; i < fields.length; i++) {
            String getterName = "get" + Character.toUpperCase(fields[i].getName().charAt(0)) + fields[i].getName().substring(1);
            sourceCode.append("sb.append(\"\\\"")
                    .append(fields[i].getName())
                    .append("\\\": \");\n");
            if (fields[i].getType().equals(List.class)) {
                ParameterizedType listType = (ParameterizedType) fields[i].getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                createSerializerIfNotExists(listClass, format);
                sourceCode.append("sb.append(\"[\");\n")
                        .append("for (Object item : object.")
                        .append(getterName)
                        .append("()) {\n")
                        .append("sb.append(new ")
                        .append(listClass.getSimpleName())
                        .append(format.toUpperCase())
                        .append("Serializer().serialize((")
                        .append(listClass.getSimpleName())
                        .append(") item));\n")
                        .append("sb.append(\",\");\n")
                        .append("}\n")
                        .append("if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);\n")
                        .append("sb.append(\"]\");\n");
            } else if (fields[i].getType().isPrimitive() || fields[i].getType().equals(String.class) || fields[i].getType().equals(Long.class) || fields[i].getType().equals(Integer.class)) {
                sourceCode.append("sb.append(\"\\\"\" + object.")
                        .append(getterName)
                        .append("() + \"\\\"\");\n");
            } else {
                createSerializerIfNotExists(fields[i].getType(), format);
                sourceCode.append("sb.append(new ")
                        .append(fields[i].getType().getSimpleName())
                        .append(format.toUpperCase())
                        .append("Serializer().serialize(object.")
                        .append(getterName)
                        .append("()));\n");
            }
            if (i != fields.length - 1) {
                sourceCode.append("sb.append(\",\");\n");
            }
        }
        sourceCode.append("sb.append(\"}\");\n")
                .append("return sb.toString();\n");
    }

    private static void createSerializerIfNotExists(Class<?> clazz, String format) throws Exception {
        String serializerClassName = clazz.getSimpleName() + format.toUpperCase() + "Serializer";
        try {
            Class.forName(clazz.getPackage().getName() + "." + serializerClassName);
        } catch (ClassNotFoundException e) {
            createSerializer(clazz, format);
        }
    }
}
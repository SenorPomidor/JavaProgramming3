package ru.mipt.tasks.factory.interfaces;

public interface Serializer<T> {

    String serialize(T object);
}


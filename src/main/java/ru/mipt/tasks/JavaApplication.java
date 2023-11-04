package ru.mipt.tasks;

import ru.mipt.tasks.entity.Person;
import ru.mipt.tasks.entity.Task;
import ru.mipt.tasks.factory.SerializerFactory;
import ru.mipt.tasks.factory.interfaces.Serializer;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class JavaApplication {
    public static void main(String[] args) {
        try {
            Task task = new Task(1L, "Hello World!");
            Task task2 = new Task(2L, "Hello World2!");
            Task task3 = new Task(3L, "Hello World3!");
            Person person = new Person(1L, "Petrovich", "Ivanov", Arrays.asList(task, task2, task3));

            Serializer<Person> jsonSerializer = SerializerFactory.createSerializer(Person.class, "json");
            String jsonOutput = jsonSerializer.serialize(person);
            try (FileWriter jsonWriter = new FileWriter("person.json")) {
                jsonWriter.write(jsonOutput);
            }

            Serializer<Person> xmlSerializer = SerializerFactory.createSerializer(Person.class, "xml");
            String xmlOutput = xmlSerializer.serialize(person);
            try (FileWriter xmlWriter = new FileWriter("person.xml")) {
                xmlWriter.write(xmlOutput);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


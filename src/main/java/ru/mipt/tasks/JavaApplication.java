package ru.mipt.tasks;

import ru.mipt.tasks.entity.Person;
import ru.mipt.tasks.factory.SerializerFactory;
import ru.mipt.tasks.factory.interfaces.Serializer;

import java.io.FileWriter;

public class JavaApplication {
    public static void main(String[] args) {
        try {
            Person person = new Person(1L, "Petrovich", "Ivanov");

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


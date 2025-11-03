package org.example.hibernate.mongofiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConexionMongo {

    private static MongoClient cliente;
    private static MongoDatabase database;

    private static String mongoUri;
    private static String mongoDatabase;
    private static String jsonFile;

    static {

        try (InputStream input = ConexionMongo.class.getResourceAsStream("/mongo.properties")) {
            if (input == null) {
                throw new IOException("No se encontró mongo.properties en resources.");
            }

            Properties props = new Properties();
            props.load(input);

            mongoUri = props.getProperty("mongo.uri");
            mongoDatabase = props.getProperty("mongo.database");
            jsonFile = props.getProperty("mongo.jsonfile");

            if (mongoUri == null || mongoDatabase == null) {
                throw new IllegalArgumentException("Faltan propiedades requeridas en mongo.properties");
            }

        } catch (Exception e) {
            System.err.println("Error cargando configuración de MongoDB:");
            e.printStackTrace();
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            cliente = MongoClients.create(mongoUri);
            database = cliente.getDatabase(mongoDatabase);
            cargarJsonSiVacio();
        }
        return database;
    }

    private static void cargarJsonSiVacio() {
        try (InputStream is = ConexionMongo.class.getResourceAsStream(jsonFile)) {
            if (is == null) {
                System.out.println("No se encontró el archivo JSON configurado: " + jsonFile);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            cargarColeccion("empleados", root.get("empleados"));
            cargarColeccion("pacientes", root.get("pacientes"));
            cargarColeccion("especialidades", root.get("especialidades"));
            cargarColeccion("citas", root.get("citas"));

            System.out.println("Datos cargados desde " + jsonFile);
        } catch (Exception e) {
            System.err.println("Error al leer o cargar JSON:");
            e.printStackTrace();
        }
    }

    private static void cargarColeccion(String nombre, JsonNode datos) {
        MongoCollection<Document> col = database.getCollection(nombre);
        if (col.countDocuments() > 0) {
            System.out.println("Colección " + nombre + " ya tiene datos, no se sobrescribe.");
            return;
        }

        for (JsonNode nodo : datos) {
            Document doc = Document.parse(nodo.toString());
            col.insertOne(doc);
        }

        System.out.println("Insertados " + datos.size() + " documentos en " + nombre);
    }
}

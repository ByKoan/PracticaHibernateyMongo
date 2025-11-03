package org.example.hibernate.mongofiles;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;

import static com.mongodb.client.model.Filters.eq;

public class SecondController {

    // Controlador del programa principal
    // Aqui se encuentran todos los elementos y funcionalidades

    // Elementos del FXML
    @FXML private TextField txtPaciente;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private DatePicker dateCita;
    @FXML private ListView<String> listEspecialidades;
    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, Integer> colId;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colEspecialidades;
    @FXML private TextField numCitas;
    @FXML private TextField txtDni;
    @FXML private Button recargarCitas;


    // Parte de gestion de la lista y tabla
    private ObservableList<Cita> citas = FXCollections.observableArrayList();

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> pacientesCol;
    private MongoCollection<Document> especialidadesCol;
    private MongoCollection<Document> citasCol;

    private void configurarTabla() {
        // Funcion que configurara la tabla de las citas
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());
        colEspecialidades.setCellValueFactory(data -> data.getValue().especialidadesProperty());
        tablaCitas.setItems(citas);
    }

    private void cargarEspecialidades() {
        // Funcion que carga las especialidades en la listView
        listEspecialidades.getItems().clear();
        listEspecialidades.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        MongoCollection<Document> col = ConexionMongo.getDatabase().getCollection("especialidades");
        for (Document doc : col.find()) {
            String nombre = doc.getString("nombre"); // El JSON tiene id y nombre
            if (nombre != null) {
                listEspecialidades.getItems().add(nombre);
            }
        }
    }

    private void cargarCitas() {
        // Funcion que cargar las citas en la tabla
        citas.clear();

        MongoCollection<Document> citasCol = ConexionMongo.getDatabase().getCollection("citas");
        MongoCollection<Document> pacientesCol = ConexionMongo.getDatabase().getCollection("pacientes");
        MongoCollection<Document> especialidadesCol = ConexionMongo.getDatabase().getCollection("especialidades");

        for (Document citaDoc : citasCol.find()) {
            Integer idPaciente = citaDoc.getInteger("id_paciente");
            Integer idEspecialidad = citaDoc.getInteger("id_especialidad");

            Document pacienteDoc = pacientesCol.find(eq("_id", idPaciente)).first();
            Document espDoc = especialidadesCol.find(eq("_id", idEspecialidad)).first();

            if (pacienteDoc != null && espDoc != null) {
                citas.add(new Cita(
                        citaDoc.getInteger("_id"),               // id_cita
                        pacienteDoc.getString("nombre"),
                        pacienteDoc.getString("dni"),
                        citaDoc.getString("fecha_cita"),
                        espDoc.getString("nombre")
                ));
            }
        }
    }

    private int generarId(String coleccion) {
        // Funcion que genera una id auto incremental para la BD
        MongoCollection<Document> col = ConexionMongo.getDatabase().getCollection(coleccion);
        Document doc = col.find().sort(new Document("_id", -1)).first(); // obtener el mayor id
        if (doc == null) return 1;
        return doc.getInteger("_id") + 1;
    }

    private void limpiarCampos() {
        // Funcion que limpia los campos del FXML
        numCitas.clear();
        txtDni.clear();
        txtPaciente.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        dateCita.setValue(null);
        listEspecialidades.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        // Funcion que desplegara una ventana de alerta con un mensaje de error
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /*private int generarIdCita() {
        // Funcion que genera una id auto incremental para las citas
        Document last = citasCol.find().sort(new Document("id_cita", -1)).first();
        return last == null ? 1 : last.getInteger("id_cita") + 1;
    }*/

    @FXML
    public void initialize() {
        // Funcion para inicializar la Base de datos
        // Conectar a MongoDB
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("clinica");
        pacientesCol = database.getCollection("pacientes");
        especialidadesCol = database.getCollection("especialidades");
        citasCol = database.getCollection("citas");

        cargarEspecialidades();
        configurarTabla();
        cargarCitas();
    }

    @FXML
    private void nuevaCita() {
        // Funcion que crea una nueva cita
        if (txtPaciente.getText().isEmpty() || dateCita.getValue() == null ||
                listEspecialidades.getSelectionModel().getSelectedItems().isEmpty()) {
            mostrarAlerta("Completa todos los campos.");
            return;
        }

        String paciente = txtPaciente.getText();
        String fecha = dateCita.getValue().toString();

        MongoCollection<Document> pacientesCol = ConexionMongo.getDatabase().getCollection("pacientes");
        MongoCollection<Document> citasCol = ConexionMongo.getDatabase().getCollection("citas");
        MongoCollection<Document> especialidadesCol = ConexionMongo.getDatabase().getCollection("especialidades");

        // Buscar paciente
        Document pacienteDoc = pacientesCol.find(eq("nombre", paciente)).first();
        Integer idPaciente;
        if (pacienteDoc != null) {
            idPaciente = pacienteDoc.getInteger("_id");
        } else {
            // Crear paciente si no existe
            Document nuevoPaciente = new Document("_id", generarId("pacientes"))
                    .append("nombre", paciente);
            pacientesCol.insertOne(nuevoPaciente);
            idPaciente = nuevoPaciente.getInteger("_id");
        }

        // Insertar una cita por cada especialidad seleccionada
        for (String esp : listEspecialidades.getSelectionModel().getSelectedItems()) {
            Document espDoc = especialidadesCol.find(eq("nombre", esp)).first();
            if (espDoc != null) {
                Document cita = new Document("_id", generarId("citas"))
                        .append("id_paciente", idPaciente)
                        .append("id_empleado", 1) // Si siempre es 1
                        .append("id_especialidad", espDoc.getInteger("_id"))
                        .append("fecha_cita", fecha)
                        .append("observaciones", "");
                citasCol.insertOne(cita);
            }
        }

        cargarCitas();
        limpiarCampos();
    }

    @FXML
    private void borrarCita() {
        // Funcion que borra la cita que hayamos pinchado
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita para borrar.");
            return;
        }

        MongoCollection<Document> citasCol = ConexionMongo.getDatabase().getCollection("citas");
        citasCol.deleteOne(eq("_id", seleccionada.getId()));
        cargarCitas();
    }

    @FXML
    private void modificarCita() {
        // Funcion para modificar cita
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita para modificar.");
            return;
        }

        String fecha = dateCita.getValue() != null ? dateCita.getValue().toString() : seleccionada.getFecha();
        MongoCollection<Document> citasCol = ConexionMongo.getDatabase().getCollection("citas");

        citasCol.updateOne(
                eq("_id", seleccionada.getId()),
                new Document("$set", new Document("fecha_cita", fecha))
        );

        cargarCitas();
    }

    @FXML
    private void verCitasPaciente() {
        // Funcion que nos mostrara las citas de un paciente determinado
        String nombrePaciente = txtPaciente.getText().trim();
        String dni = txtDni.getText().trim();
        String numCitaTexto = numCitas.getText().trim();

        if (nombrePaciente.isEmpty() && dni.isEmpty() && numCitaTexto.isEmpty()) {
            mostrarAlerta("Introduce al menos un criterio de búsqueda.");
            return;
        }

        citas.clear();

        MongoCollection<Document> citasCol = ConexionMongo.getDatabase().getCollection("citas");
        MongoCollection<Document> pacientesCol = ConexionMongo.getDatabase().getCollection("pacientes");
        MongoCollection<Document> especialidadesCol = ConexionMongo.getDatabase().getCollection("especialidades");

        for (Document doc : citasCol.find()) {
            Integer idPaciente = doc.getInteger("id_paciente");
            Integer idEspecialidad = doc.getInteger("id_especialidad");

            Document paciente = pacientesCol.find(eq("_id", idPaciente)).first();
            Document especialidad = especialidadesCol.find(eq("_id", idEspecialidad)).first();

            if (paciente != null && especialidad != null) {
                boolean matches = true;

                if (!nombrePaciente.isEmpty() && !paciente.getString("nombre").toLowerCase().contains(nombrePaciente.toLowerCase()))
                    matches = false;

                if (!dni.isEmpty() && (paciente.getString("dni") == null ||
                        !paciente.getString("dni").toLowerCase().contains(dni.toLowerCase())))
                    matches = false;

                if (!numCitaTexto.isEmpty() && !doc.getInteger("_id").equals(Integer.parseInt(numCitaTexto)))
                    matches = false;

                if (matches) {
                    citas.add(new Cita(
                            doc.getInteger("_id"),
                            paciente.getString("nombre"),
                            paciente.getString("dni"),
                            doc.getString("fecha_cita"),
                            especialidad.getString("nombre")
                    ));
                }
            }
        }
    }

    @FXML
    private void recargarCitas() {
        // Funcion que nos recargara las citas para ver todas
        // Si buscamos citas y queremos volver a ver todas
        cargarCitas();
        txtPaciente.clear();
        txtDni.clear();
        numCitas.clear();
    }
}

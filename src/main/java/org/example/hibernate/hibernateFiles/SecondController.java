package org.example.hibernate.hibernateFiles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.hibernate.hibernateFiles.model.Cita;
import org.example.hibernate.hibernateFiles.model.Especialidad;
import org.example.hibernate.hibernateFiles.model.Paciente;
import org.example.hibernate.hibernateFiles.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class SecondController {

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

    private ObservableList<Cita> citas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarEspecialidades();
        cargarCitas();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEspecialidades.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEspecialidad() != null
                                ? cellData.getValue().getEspecialidad().getNombre()
                                : ""
                )
        );
        tablaCitas.setItems(citas);
    }

    private void cargarEspecialidades() {
        listEspecialidades.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Especialidad> lista = session.createQuery("FROM Especialidad", Especialidad.class).list();
            for (Especialidad e : lista) {
                listEspecialidades.getItems().add(e.getNombre());
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar especialidades.");
            e.printStackTrace();
        }
    }

    private void cargarCitas() {
        citas.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Cita> lista = session.createQuery("FROM Cita", Cita.class).list();
            citas.addAll(lista);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar citas.");
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevaCita() {
        String pacienteNombre = txtPaciente.getText().trim();
        LocalDate fecha = dateCita.getValue();
        List<String> especialidadesSeleccionadas = listEspecialidades.getSelectionModel().getSelectedItems();

        if (pacienteNombre.isEmpty() || fecha == null || especialidadesSeleccionadas.isEmpty()) {
            mostrarAlerta("Completa todos los campos.");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Buscar o crear paciente
            Paciente paciente = session.createQuery(
                            "FROM Paciente p WHERE p.nombre = :nombre", Paciente.class)
                    .setParameter("nombre", pacienteNombre)
                    .uniqueResult();

            if (paciente == null) {
                paciente = new Paciente();
                paciente.setNombre(pacienteNombre);
                paciente.setDni(txtDni.getText());
                paciente.setDireccion(txtDireccion.getText());
                paciente.setTelefono(txtTelefono.getText());
                session.persist(paciente);
            }

            // Crear citas por cada especialidad seleccionada
            for (String espNombre : especialidadesSeleccionadas) {
                Especialidad esp = session.createQuery(
                                "FROM Especialidad e WHERE e.nombre = :nombre", Especialidad.class)
                        .setParameter("nombre", espNombre)
                        .uniqueResult();
                if (esp != null) {
                    Cita cita = new Cita();
                    cita.setPaciente(paciente);
                    cita.setEspecialidad(esp);
                    cita.setFecha(fecha); // directamente
                    session.persist(cita);
                }
            }

            tx.commit();
            cargarCitas();
            limpiarCampos();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            mostrarAlerta("Error al guardar la cita.");
            e.printStackTrace();
        }
    }

    @FXML
    private void borrarCita() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita para borrar.");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(seleccionada);
            tx.commit();
            cargarCitas();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            mostrarAlerta("Error al borrar cita.");
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarCita() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita para modificar.");
            return;
        }

        LocalDate fecha = dateCita.getValue();

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (fecha != null) {
                seleccionada.setFecha(fecha);
            }
            session.merge(seleccionada);

            tx.commit();
            cargarCitas();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            mostrarAlerta("Error al modificar la cita.");
            e.printStackTrace();
        }
    }

    @FXML
    private void verCitasPaciente() {
        String nombrePaciente = txtPaciente.getText().trim();
        String dni = txtDni.getText().trim();
        String numCitaTexto = numCitas.getText().trim();

        if (nombrePaciente.isEmpty() && dni.isEmpty() && numCitaTexto.isEmpty()) {
            mostrarAlerta("Introduce al menos un criterio de búsqueda.");
            return;
        }

        citas.clear();

        StringBuilder hql = new StringBuilder("FROM Cita c WHERE 1=1 ");
        if (!nombrePaciente.isEmpty()) hql.append("AND c.paciente.nombre LIKE :nombre ");
        if (!dni.isEmpty()) hql.append("AND c.paciente.dni LIKE :dni ");
        if (!numCitaTexto.isEmpty()) hql.append("AND c.id = :idCita ");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var query = session.createQuery(hql.toString(), Cita.class);

            if (!nombrePaciente.isEmpty()) query.setParameter("nombre", "%" + nombrePaciente + "%");
            if (!dni.isEmpty()) query.setParameter("dni", "%" + dni + "%");
            if (!numCitaTexto.isEmpty()) query.setParameter("idCita", Integer.parseInt(numCitaTexto));

            List<Cita> result = query.list();
            citas.addAll(result);

        } catch (Exception e) {
            mostrarAlerta("Error al buscar citas.");
            e.printStackTrace();
        }
    }

    @FXML
    private void recargarCitas() {
        txtPaciente.clear();
        txtDni.clear();
        numCitas.clear();
        cargarCitas();
    }

    private void limpiarCampos() {
        numCitas.clear();
        txtDni.clear();
        txtPaciente.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        dateCita.setValue(null);
        listEspecialidades.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

package org.example.hibernate.mongofiles;

import javafx.beans.property.*;

    // Clase "modelo" de donde sacaremos los objetos para el funcionamiento del programa

public class Cita {
    private IntegerProperty id;
    private StringProperty paciente;
    private StringProperty dni;
    private StringProperty fecha;
    private StringProperty especialidades;

    public Cita(int id, String paciente, String dni, String fecha, String especialidades) {
        this.id = new SimpleIntegerProperty(id);
        this.paciente = new SimpleStringProperty(paciente);
        this.dni = new SimpleStringProperty(dni);
        this.fecha = new SimpleStringProperty(fecha);
        this.especialidades = new SimpleStringProperty(especialidades);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getPaciente() { return paciente.get(); }
    public StringProperty pacienteProperty() { return paciente; }

    public String getDni() { return dni.get(); }
    public StringProperty dniProperty() { return dni; }

    public String getFecha() { return fecha.get(); }
    public StringProperty fechaProperty() { return fecha; }

    public String getEspecialidades() { return especialidades.get(); }
    public StringProperty especialidadesProperty() { return especialidades; }
}

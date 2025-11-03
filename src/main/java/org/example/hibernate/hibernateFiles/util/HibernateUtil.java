package org.example.hibernate.hibernateFiles.util;

import org.example.hibernate.hibernateFiles.model.Cita;
import org.example.hibernate.hibernateFiles.model.Empleado;
import org.example.hibernate.hibernateFiles.model.Especialidad;
import org.example.hibernate.hibernateFiles.model.Paciente;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Cargar propiedades externas
            Properties props = new Properties();
            try (InputStream input = HibernateUtil.class.getResourceAsStream("/bd.properties")) {
                props.load(input);
            }

            // Configuración de Hibernate
            Configuration configuration = new Configuration();
            configuration.addProperties(props);

            // Registrar entidades
            configuration.addAnnotatedClass(Cita.class);
            configuration.addAnnotatedClass(Paciente.class);
            configuration.addAnnotatedClass(Especialidad.class);
            configuration.addAnnotatedClass(Empleado.class);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Error creando SessionFactory", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

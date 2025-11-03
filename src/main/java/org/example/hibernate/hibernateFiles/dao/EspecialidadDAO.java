package org.example.hibernate.hibernateFiles.dao;

import org.example.hibernate.hibernateFiles.model.Especialidad;
import org.example.hibernate.hibernateFiles.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class EspecialidadDAO {

    public List<Especialidad> listar() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Especialidad", Especialidad.class).list();
        }
    }

    public Especialidad buscarPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Especialidad e WHERE e.nombre = :nombre", Especialidad.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult();
        }
    }
}

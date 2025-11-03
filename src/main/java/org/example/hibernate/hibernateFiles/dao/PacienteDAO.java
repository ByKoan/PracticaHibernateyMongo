package org.example.hibernate.hibernateFiles.dao;

import org.example.hibernate.hibernateFiles.model.Paciente;
import org.example.hibernate.hibernateFiles.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PacienteDAO {

    public Paciente buscarPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Paciente p WHERE p.nombre = :nombre", Paciente.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult();
        }
    }

    public void guardar(Paciente paciente) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(paciente);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }
}

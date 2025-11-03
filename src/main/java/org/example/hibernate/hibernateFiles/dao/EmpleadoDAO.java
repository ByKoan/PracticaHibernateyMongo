package org.example.hibernate.hibernateFiles.dao;

import org.example.hibernate.hibernateFiles.model.Empleado;
import org.example.hibernate.hibernateFiles.util.HibernateUtil;
import org.hibernate.Session;
import jakarta.persistence.NoResultException;

public class EmpleadoDAO {
    public Empleado buscarPorUsuarioYContrasena(String usuario, String contrasena) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Empleado e WHERE e.usuario = :usuario AND e.contrasena = :contrasena", Empleado.class)
                    .setParameter("usuario", usuario)
                    .setParameter("contrasena", contrasena)
                    .uniqueResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

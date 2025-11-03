package org.example.hibernate.hibernateFiles;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.example.hibernate.hibernateFiles.model.Empleado;
import org.example.hibernate.hibernateFiles.util.HibernateUtil;
import org.hibernate.Session;

import java.security.MessageDigest;

public class MainController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblMensaje;

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = sha256(txtContrasena.getText());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Empleado empleado = session.createQuery(
                            "FROM Empleado e WHERE e.usuario = :usuario AND e.contrasena = :contrasena",
                            Empleado.class)
                    .setParameter("usuario", usuario)
                    .setParameter("contrasena", contrasena)
                    .uniqueResult();

            if (empleado != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SecondViewHibernate.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Gestión de Citas - " + empleado.getNombre());
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            lblMensaje.setText("Error al conectar con la BD.");
            e.printStackTrace();
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

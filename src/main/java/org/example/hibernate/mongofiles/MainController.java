package org.example.hibernate.mongofiles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;

public class MainController {

    // Controlador de la primera vista "login"

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblMensaje;

    @FXML
    private void iniciarSesion() {
        // Funcion para iniciar sesion
        // Nos comprueba los datos frente a la base de datos Mongo DB
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        // Validación rápida de si los campos estan vacios
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Introduce usuario y contraseña.");
            return;
        }

        // Se cifra la contraseña con SHA-256 (igual que en la BD)
        String hash = HashUtil.sha256(contrasena);

        try {
            var db = ConexionMongo.getDatabase();
            var empleados = db.getCollection("empleados");

            // Filtramos por usuario y contraseña (hash)
            Document filtro = new Document("usuario", usuario)
                    .append("contraseña", hash);

            Document empleado = empleados.find(filtro).first();

            if (empleado != null) {
                String nombre = empleado.getString("nombre");
                String rol = empleado.getString("rol");

                // Carga la siguiente vista, el programa principal
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/hibernate/SecondViewMongo.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Gestión de Citas - " + nombre + " (" + rol + ")");
                stage.show();
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos."); // Mensaje de alerta por si esta mal introducidos los datos
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error al conectar con la base de datos.");
        }
    }
}

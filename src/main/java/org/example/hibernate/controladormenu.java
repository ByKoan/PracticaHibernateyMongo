package org.example.hibernate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class controladormenu {

    @FXML
    private void hibernateClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainViewHibernate.fxml"));
        Scene scene = new Scene(loader.load());

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Centro Medico San Mateo");
        newStage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void mongoClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainViewMongo.fxml"));
        Scene scene = new Scene(loader.load());

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Centro Medico San Mateo");
        newStage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

}

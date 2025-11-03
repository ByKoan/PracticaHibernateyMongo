module org.example.hibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires jakarta.annotation;
    requires org.mongodb.driver.sync.client;
    requires com.fasterxml.jackson.databind;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens org.example.hibernate.hibernateFiles.model to org.hibernate.orm.core, javafx.base, javafx.fxml;
    opens org.example.hibernate to javafx.fxml;
    opens org.example.hibernate.hibernateFiles to javafx.fxml;
    opens org.example.hibernate.mongofiles to javafx.fxml;

    exports org.example.hibernate;
    exports org.example.hibernate.hibernateFiles;
}

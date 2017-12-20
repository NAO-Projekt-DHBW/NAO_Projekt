package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Ellipse;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Label labelname;
    public TextField textname;
    public Button buttonname;
    public Ellipse ellipse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void Message(ActionEvent actionEvent) {
        System.out.println("Test");
    }
}

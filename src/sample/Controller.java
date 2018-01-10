package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button buttonwiedergabe;
    public Label labelsound;
    public Label labelsoundinfo;
    public TextField fieldsound;
    public ComboBox comboboxsprache;
    public ComboBox comboboxsprachgeschwindigkeit;
    public Slider reglertonhoehe;
    public Label labeltonhoehe;
    public ImageView bildnao;
    public ImageView bildsound;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void Message(ActionEvent actionEvent) {
        System.out.println("Test Test");
    }
}

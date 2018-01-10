package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button btnwiedergabe;
    public Label labelsound;
    public Label labelsoundinfo;
    public TextField fieldsound;
    public ComboBox comboboxsprache;
    public ComboBox comboboxsprachgeschwindigkeit;
    public Slider reglertonhoehe;
    public Label labeltonhoehe;
    public ImageView bildnao;
    public ImageView bildsound;
    public Button btnW;
    public Button btnS;
    public Button btnA;
    public Button btnD;
    public Button btnQ;
    public Button btnE;
    public Button btnOben;
    public Button btnRechts;
    public Button btnLinks;
    public Button btnUnten;
    public Button btnAufstehen;
    public Button btnHinsetzen;
    public Button btnBauch;
    public Button btnRuecken;
    public Button btnTaichi;
    public Button btnWinken;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }


    public void btnwiedergabe(ActionEvent actionEvent) {
        System.out.println("Test Test");
    }
}

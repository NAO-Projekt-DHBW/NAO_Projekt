package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    public Button buttonwiedergabe;
    public ComboBox ComboBoxAugenLEDFarbe;
    public ComboBox ComboBoxOhrenLEDFarbe;
    public Button btnBlinken;
    public Button btnZwinkern;
    public Button btnRuecken;
    public ComboBox ComboBoxNAOWaehlen;
    public Button btnVerbinden;
    public TextField FieldIPAdresse;
    public TextField FieldPortAnpassen;
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

    public Button btnTaichi;
    public Button btnWinken;
    public Button btnLEDRuecken;

    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausfrüht.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ComboBoxNAOWaehlen.setItems(FXCollections.observableArrayList(
                "192.168.178.1 (rot)", "192.168.178.2 (blau)", "192.168.178.3 (grün)")
        );
    }


    public void testAusgabe(ActionEvent actionEvent) {
        fieldsound.appendText("Das ist ein Test");
    }
}

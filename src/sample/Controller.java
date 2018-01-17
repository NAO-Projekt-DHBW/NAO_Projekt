package sample;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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


    public Button btnBauch;


    public Button btnWinken;
    public Button btnLEDRuecken;

    public Button btnHocken;
    public Button btnStuhlsitzen;
    public Button btnRelaxen;
    public Label labelLaufen;
    public ToggleButton toggleAufstehen;
    public ToggleButton ToggleSitzen;
    public Button btnTaiChi;
    public Button btnStandInit;
    public Button btnStandZero;
    public TextField fieldBattery;
    public TextField fieldTemperature;

    private static String ipAdress;
    private static String defaultPort = "9559";
    private static Application app;

    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausfrüht.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> supplierNames1 = new ArrayList<String>();
        supplierNames1.add(0, "192.168.178.1 (blau)");
        supplierNames1.add(1, "192.168.178.2 (rot)");
        supplierNames1.add(2, "192.168.178.3 (grün)");
        ComboBoxNAOWaehlen.setItems(FXCollections.observableArrayList(supplierNames1));
    }

    public void setFieldIPAdresse(ActionEvent actionEvent){
        FieldIPAdresse.clear();
        FieldPortAnpassen.clear();
        ipAdress = extractStringBefore(ComboBoxNAOWaehlen.getValue().toString()," ");
        FieldIPAdresse.appendText(ipAdress);
        FieldPortAnpassen.appendText(defaultPort);
    }

    //Verbindung zum NAO aufbauen
    public void startConnection(ActionEvent actionEvent) throws Exception {
        //String robotUrl = "tcp://127.0.0.1:39513";
        String robotUrl = "tcp://" + FieldIPAdresse.getText().toString() + ":" + FieldPortAnpassen.getText().toString();
        try {
            app = new Application(new String[]{}, robotUrl);
            app.start();
            if (app.session().isConnected()) {
                fieldsound.appendText("Verbunden");
            } else {
                fieldsound.appendText("Verbindungsaufbau fehlgeschlagen.");
            }
        } catch(Exception ex){
            fieldsound.appendText("Verbindungsaufbau fehlgeschlagen./n" + robotUrl + ex);
        }
    }

    //Sprechen
    public void sayBubble(ActionEvent actionEvent) throws Exception {
        ALTextToSpeech tts = new ALTextToSpeech(app.session());
        tts.say(fieldsound.getText().toString());
    }


    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("Sit", 1f);
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("SitRelax", 1f);
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("SitOnChair", 1f);
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("Stand", 1f);
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("LyingBack", 1f);
    }

    public void test(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("StandInit", 1f);
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("LyingBelly", 1f);
    }

    //Rouch
    public void crouch(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(app.session());
        Boolean success = rp.goToPosture("Crouch", 1f);
    }

    //Methode um einen String aus einem String zu extrahieren
    public String extractStringBefore(String originalString, String stringToExtractBefore) {
        int posA = originalString.indexOf(stringToExtractBefore);
        if (posA == -1) {
            return "";
        }
        return originalString.substring(0, posA);
    }
}

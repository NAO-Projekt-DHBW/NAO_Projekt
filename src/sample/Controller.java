package sample;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.*;
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
import java.util.*;

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
    public Button btnUp;
    public Button btnRight;
    public Button btnLeft;
    public Button btnDown;
    public Button btnBauch;
    public Button btnWinken;
    public Button btnLEDRuecken;
    public Button btnHocken;
    public Button btnStuhlsitzen;
    public Button btnRelaxen;
    public Label labelLaufen;
    public ToggleButton toggleRest;
    public ToggleButton toggleWakeUp;
    public Button btnTaiChi;
    public Button btnStandInit;
    public Button btnStandZero;
    public TextField fieldBattery;
    public TextField fieldTemperature;
    public Slider sliderPace;
    public TextArea fieldConnectionState;
    public Button btnAufstehen;
    public Button btnSitzen;

    private static String ipAdress;
    private static String defaultPort = "9559";
    private static Session session = new Session();


    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausfrüht.
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<String> supplierNames1 = new ArrayList<String>();
        supplierNames1.add(0, "192.168.178.1 (blau)");
        supplierNames1.add(1, "192.168.178.2 (rot)");
        supplierNames1.add(2, "192.168.178.3 (grün)");
        ComboBoxNAOWaehlen.setItems(FXCollections.observableArrayList(supplierNames1));
        comboboxsprache.setItems(FXCollections.observableArrayList("German", "English"));
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
            session.connect(robotUrl).get();
            if (session.isConnected()) {
                fieldConnectionState.clear();
                fieldConnectionState.getStyleClass().add("success");
                fieldConnectionState.appendText("Verbunden");
                fieldBattery.appendText(getBatteryState(actionEvent));
                fieldTemperature.appendText(getTemperature(actionEvent));
                getLanguages(actionEvent);
            } else {
                fieldConnectionState.appendText("Verbindungsaufbau fehlgeschlagen.");
            }
        } catch(Exception ex){
            fieldConnectionState.clear();
            fieldConnectionState.getStyleClass().add("error");
            fieldConnectionState.appendText("Verbindungsaufbau fehlgeschlage. Url: " + robotUrl + ex);
        }
    }

    public String getBatteryState(ActionEvent actionEvent) throws Exception{
        ALBattery battery = new ALBattery(session);
        int state = battery.getBatteryCharge();
        return String.valueOf(state);
    }

    public String getTemperature(ActionEvent actionEvent) throws Exception{
        String result;
        ALBodyTemperature temperature = new ALBodyTemperature(session);
        Object temp = temperature.getTemperatureDiagnosis();
        if(temp instanceof ArrayList){
            ArrayList tempList = (ArrayList)temp;
            result = tempList.get(0).toString();
        }
        else{
            result = "N/A";
        }
        return result;
    }

    public void wakeUp(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.wakeUp();
    }

    public void rest (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.rest();
    }


    public void movePace (ActionEvent actionEvent) throws Exception {

    }

    public void lookRight (ActionEvent actionEvent) throws Exception {

    }

    public void lookLeft (ActionEvent actionEvent) throws Exception {

    }

    public void lookUp (ActionEvent actionEvent) throws Exception {

    }

    public void lookDown (ActionEvent actionEvent) throws Exception {

    }

    public void turnRight (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0f, -1f);
    }

    public void turnLeft (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0f, 1f);
    }

    public void moveForward (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0.1f, 0f, 0f);

    }

    public void moveBackwards (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(-0.1f, 0f, 0f);
    }

    public void moveLeft (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0.1f, 0f);
    }

    public void moveRight (ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, -0.1f, 0f);
    }

    //Sprechen
    public void getLanguages(ActionEvent actionEvent) throws Exception {
        ALTextToSpeech tts = new ALTextToSpeech(session);
        String result;
        result = tts.getAvailableLanguages().toString();
        result += tts.getAvailableVoices().toString();
        fieldsound.appendText(result);
    }

    public void sayBubble(ActionEvent actionEvent) throws Exception {
        ALTextToSpeech tts = new ALTextToSpeech(session);

        tts.say(fieldsound.getText().toString());
    }


    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("Sit", 1f);
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("SitRelax", 1f);
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("SitOnChair", 1f);
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("Stand", 1f);
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("LyingBack", 1f);
    }

    public void test(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("StandInit", 1f);
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("LyingBelly", 1f);
    }

    //Rouch
    public void crouch(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
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

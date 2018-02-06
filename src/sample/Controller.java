package sample;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    public Button btnPlay;
    public Label labelSound;
    public TextField fieldSound;
    public ComboBox comboBoxLanguage;
    public Slider sliderPitch;
    public Label labelPitch;
    public ImageView imgNAO;
    public ImageView imgBubble;
    public Button btnLyingBack;
    public ComboBox comboBoxSelectNAO;
    public Button btnConnect;
    public TextField fieldIPAdress;
    public TextField fieldPort;
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
    public Button btnLyingBelly;
    public Button btnWinken;
    public Button btnCrouch;
    public Button btnSitChair;
    public Button btnSitRelax;
    public Label labelWalk;
    public ToggleButton toggleRest;
    public ToggleButton toggleWakeUp;
    public Button btnTaiChi;
    public Button btnStandInit;
    public Button btnStandZero;
    public TextField fieldBattery;
    public TextField fieldTemperature;
    public Slider sliderPace;
    public Button btnStand;
    public Button btnSit;
    public Button btnCloseConnection;
    public Slider sliderVolume;
    public Circle circleConnectionState;
    public ComboBox comboBoxLedGroup;
    public Button btnLedOn;
    public Button btnLedOff;
    public ComboBox comboBoxLedColor;
    private ActionEvent actionEvent;
    private KeyEvent keyEvent;

    private static String[][] arrayNAO = new String[5][3];
    private static String defaultPort = "9559";
    private static String fileLastConnection = "connection.txt";

    private static Session session = new Session();
    private static ALMotion motion;
    private static ALTextToSpeech tts;
    private static ALLeds alLeds;
    private static ALBattery battery;
    private static ALBodyTemperature temperature;
    private static ALRobotPosture posture;

    private static Map<String, List<String>> ledMap = new HashMap<String, List<String>>();
    private static Map<String, String> ledColorMap = new HashMap<String, String>();
    private static String ledGroupName = "ledGroup";
    private static List<String> ledList = new ArrayList<String>();

    private static Float speechPitch = 0f;
    private static float lookSpeed = 0.3f;

    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausgeführt.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        arrayNAO[0][0] = "192.168.1.136";
        arrayNAO[0][1] = "Blau";
        arrayNAO[0][2] = defaultPort;
        arrayNAO[1][0] = "192.168.1.138";
        arrayNAO[1][1] = "Blau Zwei";
        arrayNAO[1][2] = defaultPort;
        arrayNAO[2][0] = "192.168.1.148";
        arrayNAO[2][1] = "Rot";
        arrayNAO[2][2] = defaultPort;
        arrayNAO[3][0] = "192.168.1.3";
        arrayNAO[3][1] = "Grün";
        arrayNAO[3][2] = defaultPort;
        try {
            String temp = getLastConnectionFromFile();
            String[] split = temp.split(";");
            arrayNAO[3][0] = split[0];
            arrayNAO[3][1] = "letzte Verbindung";
            arrayNAO[3][2] = split[1];
        } catch (Exception e) {
        }
        List<String> listNAO = new ArrayList<String>();
        for (int i = 0; i < arrayNAO.length; i++){
            if(arrayNAO[i][0] != null) {
                listNAO.add(i, arrayNAO[i][0] + " (" + arrayNAO[i][1] + ")");
            }
        }
        comboBoxSelectNAO.setItems(FXCollections.observableArrayList(listNAO));
        comboBoxLanguage.setItems(FXCollections.observableArrayList("Deutsch", "Englisch"));
        comboBoxLanguage.getSelectionModel().selectFirst();

        List<String> ledListEyes = new ArrayList<String>();
        ledListEyes.add("FaceLeds");
        ledMap.put("Augen",ledListEyes);
        List<String> ledListEyesLeft = new ArrayList<String>();
        ledListEyesLeft.add("FaceLedsLeftBottom");
        ledListEyesLeft.add("FaceLedsLeftExternal");
        ledListEyesLeft.add("FaceLedsLeftInternal");
        ledListEyesLeft.add("FaceLedsLeftTop");
        ledMap.put("Linkes Auge",ledListEyesLeft);
        List<String> ledListEyesRight = new ArrayList<String>();
        ledListEyesRight.add("FaceLedsRightBottom");
        ledListEyesRight.add("FaceLedsRightExternal");
        ledListEyesRight.add("FaceLedsRightInternal");
        ledListEyesRight.add("FaceLedsRightTop");
        ledMap.put("Rechtes Auge",ledListEyesRight);
        comboBoxLedGroup.setItems(FXCollections.observableArrayList(ledMap.keySet()));

        ledColorMap.put("Weiß", "white");
        ledColorMap.put("Rot", "red");
        ledColorMap.put("Grün", "green");
        ledColorMap.put("Blau", "blue");
        ledColorMap.put("Gelb", "yellow");
        ledColorMap.put("Magenta", "magenta");
        ledColorMap.put("Cyan", "cyan");
        comboBoxLedColor.setItems(FXCollections.observableArrayList(ledColorMap.keySet()));
    }

    public void setConnectionData(ActionEvent actionEvent) {
        fieldIPAdress.clear();
        fieldPort.clear();
        int selected = comboBoxSelectNAO.getSelectionModel().getSelectedIndex();
        fieldIPAdress.appendText(arrayNAO[selected][0]);
        fieldPort.appendText(arrayNAO[selected][2]);
    }

    //Verbindung zum NAO aufbauen
    public void startConnection(ActionEvent actionEvent) throws Exception {
        //String robotUrl = "tcp://127.0.0.1:39513";
        String robotUrl = "tcp://" + fieldIPAdress.getText().toString() + ":" + fieldPort.getText().toString();
        try {
            session.connect(robotUrl).get();
            if (session.isConnected()) {
                //Zuweisen der NAO-Klassen
                motion = new ALMotion(session);
                tts = new ALTextToSpeech(session);
                alLeds = new ALLeds(session);
                battery = new ALBattery(session);
                temperature = new ALBodyTemperature(session);
                posture = new ALRobotPosture(session);

                //Setzen des Verbindungsstatus-Kreises auf Grün
                circleConnectionState.setFill(Color.GREEN);

                //Schreiben der momentan genutzen Verbindung in eine Datei "connection.txt"
                writeConnectionToFile(fieldIPAdress.getText().toString(), fieldPort.getText().toString());

                //Deaktivieren des "Verbinden"-Buttons und Aktivieren des "Trennen"-Buttons
                btnConnect.setDisable(true);
                btnCloseConnection.setDisable(false);

                //Mit "clearFieldsAfterConnecting" werden alle dort eingetragenen Textfelder geleert.
                clearFieldsAfterConnecting();
                fieldBattery.appendText(getBatteryState(actionEvent));
                fieldTemperature.appendText(getTemperature(actionEvent));
            }
        } catch (Exception ex) {
            //Leeren der Textfelder mit Hilfsmethode
            clearFieldsAfterConnecting();
            //Anzeigen der Fehlermeldung in einem kleinen Popup-Fenster
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void stopConnection(ActionEvent actionEvent) throws Exception{
        if(session.isConnected()) {
            session.close();
        }
        if(!session.isConnected()){
            circleConnectionState.setFill(Color.RED);
            clearFieldsAfterConnecting();
            btnCloseConnection.setDisable(true);
            btnConnect.setDisable(false);
        }
    }

    public String getLastConnectionFromFile() throws Exception{
        String line = null;
        File tempFile = new File(fileLastConnection);
        if (tempFile.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(fileLastConnection));
            line = in.readLine();
            in.close();
        }
        return line;
    }

    public void writeConnectionToFile(String ip, String port) throws Exception {
        PrintWriter writer = new PrintWriter(fileLastConnection, "UTF-8");
        writer.println(fieldIPAdress.getText().toString() + ";" + fieldPort.getText().toString());
        writer.close();
    }

    //diese Methode leert alle Textfelder, die beim Neuverbinden direkt wieder mit Text befüllt werden. Die Methode wird beim Aufbauen einer Verbindunge aufgerufen.
    public void clearFieldsAfterConnecting() throws Exception{
        fieldTemperature.clear();
        fieldBattery.clear();
    }

    public String getBatteryState(ActionEvent actionEvent) throws Exception {
        int state = battery.getBatteryCharge();
        return String.valueOf(state);
    }

    public String getTemperature(ActionEvent actionEvent) throws Exception {
        String result;
        Object temp = temperature.getTemperatureDiagnosis();
        if (temp instanceof ArrayList) {
            ArrayList tempList = (ArrayList) temp;
            result = tempList.get(0).toString();
        } else {
            result = "N/A";
        }
        return result;
    }

    //NAO aufwecken
    public void wakeUp(ActionEvent actionEvent) throws Exception {
        motion.wakeUp();
    }

    //NAO auf Standby setzen
    public void rest(ActionEvent actionEvent) throws Exception {
        motion.rest();
    }

    //
    public void lookRight(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadYaw", -2.5f, lookSpeed);
    }

    public void lookLeft(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadYaw", 0.5f, lookSpeed);
    }

    public void lookUp(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, lookSpeed);
    }

    public void lookDown(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadPitch", 0.5f, lookSpeed);
    }

    public void lookReset(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadPitch", 0f, lookSpeed);
        motion.angleInterpolationWithSpeed("HeadYaw", 0f, lookSpeed);
    }

    //NAO solange bewegen lassen, bis die Maus losgelassen wird:
    //Jede Bewegung ist an den entsprechenden Button im Scene Builder verknüpft (unter "Mouse" > "On Mouse Pressed".
    //Alle Buttons haben zusätzlich die Methode "stopMove" (unter "Mouse" > "On Mouse Released" verknüpft.
    //Den Methoden muss das MouseEvent (und nicht ActionEvent) übergeben werden, sonst wird sie in der Laufzeit mit einem Fehler abgebrochen.
    public void moveForward(MouseEvent mouseEvent) throws Exception {
        motion.move(1.0f, 0f ,0f);
    }

    public void moveBackwards(MouseEvent mouseEvent) throws Exception {
        motion.move(-1.0f, 0f ,0f);
    }

    public void moveLeft(MouseEvent mouseEvent) throws Exception {
        motion.move(0f, 1.0f, 0f);
    }

    public void moveRight(MouseEvent mouseEvent) throws Exception {
        motion.move(0f, -1.0f, 0f);
    }

    public void turnRight(MouseEvent mouseEvent) throws Exception {
        motion.move(0f, 0f, -1.0f);
    }

    public void turnLeft(MouseEvent mouseEvent) throws Exception {
        motion.move(0f, 0f, 1.0f);
    }

    public void stopMove(MouseEvent mouseEvent) throws Exception {
        if(motion.moveIsActive())
        {
            motion.stopMove();
        }
    }

    //NAO durch Tastatur steuern:
    //Methode "getKeyPressed" ist mit "On Key Pressed" im Scene Builder verknüpft.
    //Wird eine entsprechende Taste erkannt wird der NAO entsprechend bewegt.
    public void getKeyPressed(javafx.scene.input.KeyEvent keyEvent) throws Exception {
        float angle;
        switch(keyEvent.getCode())
        {
            case I:
                angle = motion.getAngles("HeadPitch", true).get(0);
                if(angle > -0.478025){
                    angle = angle - 0.1f;
                    motion.angleInterpolationWithSpeed("HeadPitch", angle, lookSpeed);
                }
                break;
            case K:
                angle = motion.getAngles("HeadPitch", true).get(0);
                if(angle < 0.478025){
                    angle = angle + 0.1f;
                    motion.angleInterpolationWithSpeed("HeadPitch", angle, lookSpeed);
                }
                break;
            case J:
                angle = motion.getAngles("HeadYaw", true).get(0);
                if(angle < 2.0856686f){
                    angle = angle + 0.1f;
                    motion.angleInterpolationWithSpeed("HeadYaw", angle, lookSpeed);
                }
                break;
            case L:
                angle = motion.getAngles("HeadYaw", true).get(0);
                if(angle > -2.0856686f){
                    angle = angle - 0.1f;
                    motion.angleInterpolationWithSpeed("HeadYaw", angle, lookSpeed);
                }
                break;
            case W:
                motion.move(3.0f, 0f, 0f);
                break;
            case A:
                motion.move(0f, 1.0f, 0f);
                break;
            case S:
                motion.move(-1.0f, 0f, 0f);
                break;
            case D:
                motion.move(0f, -1.0f, 0f);
                break;
            case Q:
                motion.move(0f, 0f, 1.0f);
                break;
            case E:
                motion.move(0f, 0f, 1.0f);
                break;
        }
    }

    //Methode "getKeyReleased" ist mit "On Key Released" im Scene Builder verknüpft.
    //Wenn eine der Tasten WASDQE losgelassen wird, wird die Bewegung des NAOs gestoppt.
    //Ansonsten würde der NAO unendlich lange laufen, auch wenn die Taste losgelassen wird.
    public void getKeyReleased(javafx.scene.input.KeyEvent keyEvent) throws Exception {
        switch(keyEvent.getCode())
        {
            case W:
                motion.stopMove();
                break;
            case A:
                motion.stopMove();
                break;
            case S:
                motion.stopMove();
                break;
            case D:
                motion.stopMove();
                break;
            case Q:
                motion.stopMove();
                break;
            case E:
                motion.stopMove();
                break;
        }
    }

    //NAO sagt, was in die Sprechblase geschrieben wurde. Die Methode wird durch den Play-Button gestartet.
    public void sayText(ActionEvent actionEvent) throws Exception {
        if(fieldSound.getText() != null) {
            if (comboBoxLanguage.getValue().toString() == "Deutsch") {
                tts.setLanguage("German");
            } else {
                tts.setLanguage("English");
            }
            //Der Lautstärke-Wert wird direkt dem Slider aus der GUI entnommen. Der Wertebreich ist entsprechend der API-Dokumentation angepasst.
            tts.setVolume((float) sliderVolume.getValue());

            //Anpassen der Tonhöhe entsprechend dem Slider in der GUI.
            //Der Wertebereich liegt zwischen 0-4
            //Da zwischen 0 und 1 keine Änderungen in der Tonhöhe geschehen, wird das mit einer IF-Abfrage abgefangen.
            float pitch = 0f;
            if (sliderPitch.getValue() >= 1f) {
                pitch = (float) sliderPitch.getValue();
            }
            tts.setParameter("pitchShift", pitch);

            //Eigentlicher Befehl zum Starten der Wiedergabe
            tts.say(fieldSound.getText());
        }
    }

    public void selectLed(ActionEvent actionEvent) throws Exception {
        ledList.clear();
        String selectedItem = comboBoxLedGroup.getValue().toString();
        for (int i=0; i < ledMap.get(selectedItem).size(); i++){
            ledList.add(ledMap.get(selectedItem).get(i));
        }
        if(!ledList.isEmpty()) {
            alLeds.createGroup(ledGroupName, ledList);
            System.out.println(ledList);
        }
    }

    public void selectLedColor(ActionEvent actionEvent) throws Exception{
        alLeds.fadeRGB(ledGroupName, ledColorMap.get(comboBoxLedColor.getValue()).toString(), 0f);
    }

    public void ledsOn(ActionEvent actionEvent) throws Exception {
        alLeds.on(ledGroupName);
    }

    public void ledsOff(ActionEvent actionEvent) throws Exception {
        alLeds.off(ledGroupName);
    }

    //Alle Möglichen Positionen, die der NAO einnehmen kann.
    //Der zweite Wert (float) gibt die Geschwindigkeit an.
    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("Sit", 1f);
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("SitRelax", 1f);
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("SitOnChair", 1f);
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("Stand", 1f);
    }

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("StandInit", 1f);
    }

    //Stand Zero
    public void StandZero(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("StandZero", 1f);
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("LyingBack", 1f);
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("LyingBelly", 1f);
    }


    public void TaiChi(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("TaiChi", 1f);
    }

    //Hocken
    public void crouch(ActionEvent actionEvent) throws Exception {
        posture.goToPosture("Crouch", 1f);
    }
}

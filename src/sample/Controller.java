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
import javafx.scene.input.KeyCode;
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
    public ComboBox comboBoxEyesLEDColour;
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
    public ComboBox comboBoxSelectEyes;
    public Button btnLEDOn;
    public Button btnLEDOff;

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
    private static Float speechPitch = 0f;
    public static float walkingDistance = 0.3f;
    public static float lookSpeed = 0.3f;

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

        List<String> listLED = new ArrayList<String>();
        listLED.add("Rechts");
        listLED.add("Links");
        listLED.add("Links und Rechts");
        comboBoxSelectEyes.setItems(FXCollections.observableArrayList(listLED));

        List<String> listLEDColour = new ArrayList<String>();
        listLEDColour.add("Blau");
        listLEDColour.add("Grün");
        listLEDColour.add("Rot");
        comboBoxEyesLEDColour.setItems(FXCollections.observableArrayList(listLEDColour));
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

    public void wakeUp(ActionEvent actionEvent) throws Exception {
        motion.wakeUp();
    }

    public void rest(ActionEvent actionEvent) throws Exception {
        motion.rest();
    }


    public void lookRight(ActionEvent actionEvent) throws Exception {
        motion.angleInterpolationWithSpeed("HeadYaw", -0.5f, lookSpeed);
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

    public void getKeyPressed(javafx.scene.input.KeyEvent keyEvent) throws Exception {
        switch(keyEvent.getCode())
        {
            /*
            case UP:
                motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, lookSpeed);
            case DOWN:
                motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, lookSpeed);
            case LEFT:
                motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, lookSpeed);
            case RIGHT:
                motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, lookSpeed);
                */
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


    //Sagen, was in die Sprechblase geschrieben wurde.
    public void sayText(ActionEvent actionEvent) throws Exception {
        if(fieldSound.getText() != null) {
            if (comboBoxLanguage.getValue().toString() == "Deutsch") {
                tts.setLanguage("German");
            } else {
                tts.setLanguage("English");
            }
            tts.setVolume((float) sliderVolume.getValue());

            if (sliderPitch.getValue() >= 1f) {
                speechPitch = (float) sliderPitch.getValue();
            }
            tts.setParameter("pitchShift", speechPitch);
            tts.say(fieldSound.getText());
        }
    }

    public void selectLED(ActionEvent actionEvent) throws Exception {
        String selectedLED = comboBoxSelectEyes.getValue().toString();
        List<String> temp = new ArrayList<String>();
        if(selectedLED == "Links"){
            temp.add("FaceLedsLeftBottom");
            temp.add("FaceLedsLeftExternal");
            temp.add("FaceLedsLeftInternal");
            temp.add("FaceLedsLeftTop");
        }
        else if (selectedLED == "Rechts"){
            temp.add("FaceLedsRightBottom");
            temp.add("FaceLedsRightExternal");
            temp.add("FaceLedsRightInternal");
            temp.add("FaceLedsRightTop");
        }
        else if (selectedLED == "Links und Rechts"){
            temp.add("FaceLeds");
        }
        if(!temp.isEmpty()) {
            alLeds.createGroup("leds", temp);
            alLeds.on("leds");
        }
    }

    public void ledsOn(ActionEvent actionEvent) throws Exception {

    }

    public void ledsOff(ActionEvent actionEvent) throws Exception {

    }

    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("Sit", 1f);
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("SitRelax", 1f);
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("SitOnChair", 1f);
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("Stand", 1f);
    }

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("StandInit", 1f);
    }

    //Stand Zero
    public void StandZero(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("StandZero", 1f);
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("LyingBack", 1f);
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("LyingBelly", 1f);
    }


    public void TaiChi(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("TaiChi", 1f);
    }

    //Hocken
    public void crouch(ActionEvent actionEvent) throws Exception {
        Boolean success = posture.goToPosture("Crouch", 1f);
    }
}

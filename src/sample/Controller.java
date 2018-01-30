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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    public Button btnPlay;
    public Label labelSound;
    public Label labelsoundinfo;
    public TextField fieldSound;
    public ComboBox comboBoxLanguage;
    public ComboBox comboboxsprachgeschwindigkeit;
    public Slider sliderPitch;
    public Label labelPitch;
    public ImageView imgNAO;
    public ImageView imgBubble;
    public ComboBox comboBoxEyesLEDColour;
    public ComboBox comboBoxEarsLEDColour;
    public Button btnBlink;
    public Button btnZwinkern;
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
    public Button btnBackLED;
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
    public TextArea fieldConnectionState;
    public Button btnStand;
    public Button btnSit;
    public Button btnCloseConnection;

    private static String[][] arrayNAO = new String[4][3];
    private static String defaultPort = "9559";
    private static String fileLastConnection = "connection.txt";
    private static Session session = new Session();
    private static Float defaultSpeechPitch = 0f;
    public Circle circleConnectionState;

    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausfrüht.
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        arrayNAO[0][0] = "192.168.178.1";
        arrayNAO[0][1] = "blau";
        arrayNAO[0][2] = defaultPort;

        arrayNAO[1][0] = "192.168.178.2";
        arrayNAO[1][1] = "rot";
        arrayNAO[1][2] = defaultPort;

        arrayNAO[2][0] = "192.168.178.3";
        arrayNAO[2][1] = "grün";
        arrayNAO[2][2] = defaultPort;

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
                circleConnectionState.setFill(Color.GREEN);
                writeConnectionToFile(fieldIPAdress.getText().toString(), fieldPort.getText().toString());
                btnConnect.setDisable(true);
                btnCloseConnection.setDisable(false);
                clearFieldsAfterConnecting();
                //fieldConnectionState.appendText("Verbunden");
                fieldBattery.appendText(getBatteryState(actionEvent));
                fieldTemperature.appendText(getTemperature(actionEvent));
                //getLanguages(actionEvent);
            }
        } catch (Exception ex) {
            clearFieldsAfterConnecting();
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
        ALBattery battery = new ALBattery(session);
        int state = battery.getBatteryCharge();
        return String.valueOf(state);
    }

    public String getTemperature(ActionEvent actionEvent) throws Exception {
        String result;
        ALBodyTemperature temperature = new ALBodyTemperature(session);
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
        ALMotion motion = new ALMotion(session);
        motion.wakeUp();
    }

    public void rest(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.rest();
    }

    public void movePace(ActionEvent actionEvent) throws Exception {

    }

    public void lookRight(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.angleInterpolationWithSpeed("HeadYaw", -0.5f, 0.45f);
    }

    public void lookLeft(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.angleInterpolationWithSpeed("HeadYaw", 0.5f, 0.45f);
    }

    public void lookUp(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.angleInterpolationWithSpeed("HeadPitch", -0.5f, 0.45f);
    }

    public void lookDown(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.angleInterpolationWithSpeed("HeadPitch", 0.5f, 0.45f);
    }

    public void turnRight(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0f, 1f);
    }

    public void turnLeft(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0f, -1f);
    }

    public void moveForward(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0.1f, 0f, 0f);
    }

    public void moveBackwards(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(-0.1f, 0f, 0f);
    }

    public void moveLeft(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, 0.1f, 0f);
    }

    public void moveRight(ActionEvent actionEvent) throws Exception {
        ALMotion motion = new ALMotion(session);
        motion.moveTo(0f, -0.1f, 0f);
    }

    public void sayBubble(ActionEvent actionEvent) throws Exception {
        ALTextToSpeech tts = new ALTextToSpeech(session);
        if (comboBoxLanguage.getValue().toString() == "Deutsch") {
            tts.setLanguage("German");
        } else {
            tts.setLanguage("English");
        }
        tts.setVolume(1f);
        if (sliderPitch.getValue() >= 1f) {
            defaultSpeechPitch = (float) sliderPitch.getValue();
        }
        tts.setParameter(" pitchShift", defaultSpeechPitch);
        tts.say(fieldSound.getText());
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

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("StandInit", 1f);
    }

    public void StandZero(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("StandZero", 1f);
    }

    public void TaiChi(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("TaiChi", 1f);
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("LyingBelly", 1f);
    }

    //Crouch
    public void crouch(ActionEvent actionEvent) throws Exception {
        ALRobotPosture rp = new ALRobotPosture(session);
        Boolean success = rp.goToPosture("Crouch", 1f);
    }
}

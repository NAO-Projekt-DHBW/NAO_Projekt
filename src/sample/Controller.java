package sample;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
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
    public Button btnLyingBelly;
    public Button btnCrouch;
    public Button btnSitChair;
    public Button btnSitRelax;
    public Label labelWalk;
    public ToggleButton toggleRest;
    public ToggleButton toggleWakeUp;
    public Button btnStandInit;
    public Button btnStandZero;
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
    public ListView listSoundFiles;
    public Slider sliderTalkingSpeed;
    public AnchorPane aPaneBattery;
    public AnchorPane aPaneBatteryBar;
    public ImageView imgBatteryCharching;
    public Button btnResetAudioSettings;
    public Slider sliderHeadUpDown;
    public Slider sliderHeadLeftRight;
    public TextField fieldBattery;

    private static int maxLastConnections = 10;
    private static String fileLastConnection = "connection.txt";
    private static Map<String, List<String>> ledMap = new HashMap<String, List<String>>();
    private static Map<String, String> ledColorMap = new HashMap<String, String>();
    private static String ledGroupName = "ledGroup";
    private static List<String> ledList = new ArrayList<String>();
    private static Session session = new Session();
    private static ALMotion motion;
    private static ALTextToSpeech tts;
    private static ALLeds alLeds;
    private static ALBattery battery;
    private static ALBodyTemperature temperature;
    private static ALRobotPosture posture;
    private static ALAudioPlayer audio;
    private static ALTouch touch;
    private static ALMemory memory;
    private static ReactToEvents reactor;
    //Klasse um Events vom Nao zu erhalten.
    public class ReactToEvents {

        //Variablen, um ID der Abonnements abzuspeichern. Diese ist nötig, um die Abonnements wieder zu beenden (bspw. bei manueller Trennung vom NAO).
        long frontTactilSubscriptionId, rearTactilSubscriptionId, middleTactilSubscriptionId, postureSubscriptionId, batteryChargeSubscriptionId, batteryPluggedSubscriptionId,
                temperatureChangedSubscriptionId, temperatureStatusSubscriptionId, temperatureDiagnosisSubscriptionId;

        public void run(Session session) throws Exception {
            frontTactilSubscriptionId = 0;
            rearTactilSubscriptionId = 0;
            middleTactilSubscriptionId = 0;
            postureSubscriptionId = 0;
            batteryChargeSubscriptionId = 0;
            batteryPluggedSubscriptionId = 0;
            temperatureChangedSubscriptionId = 0;
            temperatureStatusSubscriptionId = 0;
            temperatureDiagnosisSubscriptionId = 0;

            //Anmelden am Event "FrontTactilTouched" (vorderer Touch-Sensor am Kopf des NAO),
            // erstellen eines EventCallback, welches Daten vom Event erwartet (je nach Event ist das bspw. ein Float oder ein String).
            frontTactilSubscriptionId = memory.subscribeToEvent(
                    "FrontTactilTouched", new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            // Der Sensor wurde berührt.
                            if (arg0 > 0) {
                                tts.say("Nein!");
                                System.out.println("FrontTactilTouched: " + arg0);
                            }
                        }
                    });

            //Weitere Abonnements für die anderen touch-Sensoren am Kopf nach dem gleichen Schema
            rearTactilSubscriptionId = memory.subscribeToEvent("RearTactilTouched",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                tts.say("Aua!");
                                System.out.println("RearTactilTouched: " + arg0);
                            }
                        }
                    });

            middleTactilSubscriptionId = memory.subscribeToEvent("MiddleTactilTouched",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                tts.say("Ouch!");
                                System.out.println("MiddleTactilTouched: " + arg0);
                            }
                        }
                    });

            //Änderung des Status der Batterie
            batteryChargeSubscriptionId = memory.subscribeToEvent("BatteryChargeChanged",
                    new EventCallback<Integer>() {
                        @Override
                        public void onEvent(Integer arg0)
                                throws InterruptedException, CallError {
                            if (arg0 >= 0) {
                                System.out.println("BatteryChargeChanged: " + arg0);
                                try {
                                    writeBatteryStateToTextField(arg0);
                                }catch(Exception ex){
                                }
                            }
                        }
                    });

            //Änderung des Ladestatus der Batterie
            batteryPluggedSubscriptionId = memory.subscribeToEvent("BatteryPowerPluggedChanged",
                    new EventCallback<Boolean>() {
                        @Override
                        public void onEvent(Boolean arg0)
                                throws InterruptedException, CallError {
                            System.out.println("BatteryPowerPlugged: " + arg0);
                            imgBatteryCharching.setVisible(arg0);

                        }
                    });

            //Änderung des Temperaturstatus
            temperatureStatusSubscriptionId = memory.subscribeToEvent("TemperatureStatusChanged",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureStatusChanged: " + arg0);
                            }
                        }
                    });

            //Temperaturstatus
            temperatureStatusSubscriptionId = memory.subscribeToEvent("TemperatureStatus",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureStatus: " + arg0);
                            }
                        }
                    });

            //TemperatureDiagnosisErrorChanged
            temperatureDiagnosisSubscriptionId = memory.subscribeToEvent("TemperatureDiagnosisErrorChanged",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureDiagnosisErrorChanged: " + arg0);
                            }
                        }
                    });

            //Änderung der Haltung (als Test-Szenario)
            postureSubscriptionId = memory.subscribeToEvent(
                    "PostureChanged", new EventCallback<String>() {
                        @Override
                        public void onEvent(String arg0)
                                throws InterruptedException, CallError {
                            // 1 means the sensor has been pressed
                            if (arg0 != null) {
                                System.out.println(arg0);
                            }
                        }
                    });
        }

        public void unsubscribe(Session session) throws Exception {
            if(frontTactilSubscriptionId > 0) memory.unsubscribeToEvent(frontTactilSubscriptionId);
            if(rearTactilSubscriptionId > 0) memory.unsubscribeToEvent(rearTactilSubscriptionId);
            if(middleTactilSubscriptionId > 0) memory.unsubscribeToEvent(middleTactilSubscriptionId);
            if(batteryChargeSubscriptionId > 0) memory.unsubscribeToEvent(batteryChargeSubscriptionId);
            if(batteryPluggedSubscriptionId > 0) memory.unsubscribeToEvent(batteryPluggedSubscriptionId);
            if(temperatureStatusSubscriptionId > 0) memory.unsubscribeToEvent(temperatureStatusSubscriptionId);
            if(temperatureDiagnosisSubscriptionId > 0) memory.unsubscribeToEvent(temperatureDiagnosisSubscriptionId);
            if(temperatureChangedSubscriptionId > 0) memory.unsubscribeToEvent(temperatureChangedSubscriptionId);
            if(postureSubscriptionId > 0) memory.unsubscribeToEvent(postureSubscriptionId);
        }
    }

        //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausgeführt.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //Befüllen der ComboBoxen/Dropdown-Menüs mit Elementen
            comboBoxSelectNAO.setItems(FXCollections.observableArrayList(getLastConnectionsFromFile()));
            comboBoxLanguage.setItems(FXCollections.observableArrayList("Deutsch", "Englisch"));

            ledMap.put("Augen", FXCollections.observableArrayList("FaceLeds"));
            ledMap.put("Linkes Auge", FXCollections.observableArrayList("FaceLedsLeftBottom", "FaceLedsLeftExternal", "FaceLedsLeftInternal", "FaceLedsLeftTop"));
            ledMap.put("Rechtes Auge", FXCollections.observableArrayList("FaceLedsRightBottom", "FaceLedsRightExternal", "FaceLedsRightInternal", "FaceLedsRightTop"));
            comboBoxLedGroup.setItems(FXCollections.observableArrayList(ledMap.keySet()));

            ledColorMap.put("Weiß", "white");
            ledColorMap.put("Rot", "red");
            ledColorMap.put("Grün", "green");
            ledColorMap.put("Blau", "blue");
            ledColorMap.put("Gelb", "yellow");
            ledColorMap.put("Magenta", "magenta");
            ledColorMap.put("Cyan", "cyan");
            comboBoxLedColor.setItems(FXCollections.observableArrayList(ledColorMap.keySet()));
        } catch(Exception ex) {
        }
    }

    /*
    Methoden zum Verwalten der Verbindung mit dem NAO
     */
    //Wenn NAO-Verbindungsdaten aus dem Dropdown-Menü gewählt wird, wird die IP-Adresse und der Port in die beiden entsprechenden Textfelder eingetragen.
    public void setConnectionData(ActionEvent actionEvent) {
        String temp = comboBoxSelectNAO.getValue().toString();
        fieldIPAdress.clear();
        fieldPort.clear();
        fieldIPAdress.appendText(temp.substring( 0, temp.indexOf(":")));
        fieldPort.appendText(temp.substring(temp.indexOf(":") + 1));
    }

    //Verbindung zum NAO aufbauen
    public void startConnection(ActionEvent actionEvent) throws Exception {
        String robotUrl = "tcp://" + fieldIPAdress.getText().toString() + ":" + fieldPort.getText().toString();
        try {
            session.connect(robotUrl).get();
            if(checkConnection()){
                //Zuweisen der NAO-Klassen
                motion = new ALMotion(session);
                tts = new ALTextToSpeech(session);
                alLeds = new ALLeds(session);
                battery = new ALBattery(session);
                temperature = new ALBodyTemperature(session);
                posture = new ALRobotPosture(session);
                audio = new ALAudioPlayer(session);
                touch = new ALTouch(session);
                memory = new ALMemory(session);
                reactor = new ReactToEvents();
                reactor.run(session);

                //Schreiben der momentan genutzen Verbindung in eine Datei "connection.txt"
                writeConnectionToFile(fieldIPAdress.getText().toString(), fieldPort.getText().toString());
                //Außerdem wird direkt die Nao-Auswahlliste neu aus der Datei "connection.txt" eingelesen, um den neusten Eintrag hinzuzufügen.
                //comboBoxSelectNAO.setItems(FXCollections.observableArrayList(getLastConnectionsFromFile()));
                //comboBoxSelectNAO.getSelectionModel().selectLast();

                fieldTemperature.clear();
                writeBatteryStateToTextField(getBatteryState());
                listSoundFiles.setItems(FXCollections.observableArrayList(""));
                fieldTemperature.appendText(getTemperature());

                //AudioFiles laden
                Boolean noAudioFiles = true;
                if(audio.getMethodList().contains("getSoundSetFileNames")) {
                    if(audio.getInstalledSoundSetsList().contains("Aldebaran")) {
                        listSoundFiles.setItems(FXCollections.observableArrayList(audio.getSoundSetFileNames("Aldebaran")));
                        noAudioFiles = false;
                    }
                }
                listSoundFiles.setDisable(noAudioFiles);

                //Audio-Parameter auslesen und setzen
                if(tts.getLanguage().toString().contains("German")){
                    comboBoxLanguage.getSelectionModel().select("Deutsch");
                    tts.say("Du bist verbunden!");
                } else if (tts.getLanguage().toString().contains("English")){
                    comboBoxLanguage.getSelectionModel().select("Englisch");
                    tts.say("You are connected!");
                }
            }
        } catch (Exception ex) {
            //Anzeigen der Fehlermeldung in einem kleinen Popup-Fenster
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void stopConnection(ActionEvent actionEvent) throws Exception{
        if(session.isConnected()) {
            reactor.unsubscribe(session);
            tts.say("Ciao!");
            session.close();
        }
        checkConnection();
    }

    public Boolean checkConnection() throws Exception{
        if(session.isConnected()){

            //Setzen des Verbindungsstatus-Kreises auf Grün
            circleConnectionState.setFill(Color.GREEN);

            //Deaktivieren des "Verbinden"-Buttons und Aktivieren des "Trennen"-Buttons
            btnConnect.setDisable(true);
            btnCloseConnection.setDisable(false);

            return true;

        } else {
            circleConnectionState.setFill(Color.RED);
            btnCloseConnection.setDisable(true);
            btnConnect.setDisable(false);
            listSoundFiles.setItems(FXCollections.observableArrayList(""));
            clearBatteryStateField();
            return false;
        }
    }

    public List<String> getLastConnectionsFromFile() throws Exception{
        List<String> list = new ArrayList<>();
        File file = new File(fileLastConnection);
        if (file.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileLastConnection), "UTF-8"));
                String line = br.readLine();
                if (line != null) {
                    list = Arrays.asList(line.split("\\s*,\\s*"));
                }
        }
        return list;
    }

    public void writeConnectionToFile(String ip, String port) throws Exception {
        String listString = "";
        List<String> oldList = getLastConnectionsFromFile();
        List<String> newList = new ArrayList<>();
        int size = oldList.size();
        if(size >= maxLastConnections - 1){
            for (int i=0; i < size-1; i++){
                newList.add(i, oldList.get(i+1));
            }
            newList.add(maxLastConnections-1 , ip + ":" + port);
        } else {
            for (String s : oldList)
            {
                newList.add(s);
            }
            newList.add(ip + ":" + port);
        }
        for (String s : newList)
        {
            listString += s + ",";
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLastConnection), "UTF-8"));
        bw.write(listString);
        bw.close();
    }

    public int getBatteryState() throws Exception{
        int state = battery.getBatteryCharge();
        return state;
    }

    public void clearBatteryStateField() throws Exception{
        fieldBattery.clear();
        aPaneBatteryBar.getStyleClass().remove("high");
        aPaneBatteryBar.getStyleClass().remove("medium");
        aPaneBatteryBar.getStyleClass().remove("low");
        aPaneBatteryBar.setStyle("-fx-pref-width:100px;");
    }

    public void writeBatteryStateToTextField(int state) throws Exception {
        clearBatteryStateField();
        state = 90;
        fieldBattery.appendText(String.valueOf(state + "%"));
        if (state >= 50) {
            aPaneBatteryBar.getStyleClass().add("high");
        } else if (state >= 25 ) {
            aPaneBatteryBar.getStyleClass().add("medium");
        } else {
            aPaneBatteryBar.getStyleClass().add("low");
        }
        aPaneBatteryBar.setStyle("-fx-pref-width:" + state + "px;");
    }

    public String getTemperature() throws Exception {
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
        if(checkConnection()){
            motion.wakeUp();
        }
    }

    //NAO auf Standby setzen
    public void rest(ActionEvent actionEvent) throws Exception {
        if(checkConnection()) {
            motion.rest();
        }
    }

    //Kopf des NAO drehen und neigen
    public void lookUpOrDown(MouseEvent mouseEvent) throws Exception{
        if(checkConnection()){
            motion.angleInterpolationWithSpeed("HeadPitch", -(float) sliderHeadUpDown.getValue(), (float) sliderPace.getValue());
        }
    }

    public void lookLeftOrRight(MouseEvent mouseEvent) throws Exception{
        if(checkConnection()){
            motion.angleInterpolationWithSpeed("HeadYaw", -(float) sliderHeadLeftRight.getValue(), (float) sliderPace.getValue());
        }
    }

    public void lookReset(ActionEvent actionEvent) throws Exception {
        if(checkConnection()) {
            motion.angleInterpolationWithSpeed("HeadPitch", 0f, (float) sliderPace.getValue());
            motion.angleInterpolationWithSpeed("HeadYaw", 0f, (float) sliderPace.getValue());
            sliderHeadLeftRight.setValue(0);
            sliderHeadUpDown.setValue(0);
        }
    }

    //NAO solange bewegen lassen, bis die Maus losgelassen wird:
    //Jede Bewegung ist an den entsprechenden Button im Scene Builder verknüpft (unter "Mouse" > "On Mouse Pressed".
    //Alle Buttons haben zusätzlich die Methode "stopMove" (unter "Mouse" > "On Mouse Released" verknüpft.
    //Den Methoden muss das MouseEvent (und nicht ActionEvent) übergeben werden, sonst wird sie in der Laufzeit mit einem Fehler abgebrochen.
    public void moveForward(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()) {
            motion.move((float) sliderPace.getValue(), 0f, 0f);
        }
    }

    public void moveBackwards(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()){
            motion.move(-(float) sliderPace.getValue(), 0f ,0f);
        }
    }

    public void moveLeft(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()){
            motion.move(0f, (float) sliderPace.getValue(), 0f);
        }
    }

    public void moveRight(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()){
            motion.move(0f, -(float) sliderPace.getValue(), 0f);
        }
    }

    public void turnRight(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()){
            motion.move(0f, 0f, -(float) sliderPace.getValue());
        }
    }

    public void turnLeft(MouseEvent mouseEvent) throws Exception {
        if(checkConnection()){
            motion.move(0f, 0f, (float) sliderPace.getValue());
        }
    }

    public void stopMove(MouseEvent mouseEvent) throws Exception {
        if(checkConnection() && motion.moveIsActive()) {
            motion.stopMove();
        }
    }

    //NAO durch Tastatur steuern:
    //Methode "getKeyPressed" ist mit "On Key Pressed" im Scene Builder verknüpft.
    //Wird eine entsprechende Taste erkannt wird der NAO entsprechend bewegt.
    public void getKeyPressed(javafx.scene.input.KeyEvent keyEvent) throws Exception {
        if(checkConnection()) {
            switch (keyEvent.getCode()) {
                case W:
                    motion.move((float) sliderPace.getValue(), 0f, 0f);
                    break;
                case A:
                    motion.move(0f, (float) sliderPace.getValue(), 0f);
                    break;
                case S:
                    motion.move(-(float) sliderPace.getValue(), 0f, 0f);
                    break;
                case D:
                    motion.move(0f, -(float) sliderPace.getValue(), 0f);
                    break;
                case Q:
                    motion.move(0f, 0f, (float) sliderPace.getValue());
                    break;
                case E:
                    motion.move(0f, 0f, -(float) sliderPace.getValue());
                    break;
            }
        }
    }

    //Methode "getKeyReleased" ist mit "On Key Released" im Scene Builder verknüpft.
    //Wenn eine der Tasten WASDQE losgelassen wird, wird die Bewegung des NAOs gestoppt.
    //Ansonsten würde der NAO unendlich lange laufen, auch wenn die Taste losgelassen wird.
    public void getKeyReleased(javafx.scene.input.KeyEvent keyEvent) throws Exception {
        if(checkConnection()) {
            switch (keyEvent.getCode()) {
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
    }
    public void setLanguage(ActionEvent actionEvent) throws Exception{
        if(checkConnection()){
            if (comboBoxLanguage.getValue().toString() == "Deutsch") {
                tts.setLanguage("German");
            } else if (comboBoxLanguage.getValue().toString() == "Englisch"){
                tts.setLanguage("English");
            }
        }
    }

    public void resetAudioSettings(ActionEvent actionEvent) throws Exception{
        sliderTalkingSpeed.setValue(100);
        sliderPitch.setValue(100);
        sliderVolume.setValue(80);
    }

    //NAO sagt, was in die Sprechblase geschrieben wurde. Die Methode wird durch den Play-Button gestartet.
    public void sayText(ActionEvent actionEvent) throws Exception {
        if(checkConnection() && fieldSound.getText() != null && comboBoxLanguage.getValue().toString() != null) {
            tts.say("\\vol=" + (float) sliderVolume.getValue() + "\\\\vct=" + (float) sliderPitch.getValue() + "\\\\rspd=" + (float) sliderTalkingSpeed.getValue() + "\\" + fieldSound.getText());
        }
    }

    public void playSoundFile(MouseEvent mouseEvent) throws Exception{
        if(checkConnection()) {
            audio.playSoundSetFile(listSoundFiles.getSelectionModel().getSelectedItem().toString(), 0f, (float) sliderVolume.getValue(), 0f, false);
        }
    }

    public void selectLed(ActionEvent actionEvent) throws Exception {
        ledList.clear();
        String selectedItem = comboBoxLedGroup.getValue().toString();
        for (int i=0; i < ledMap.get(selectedItem).size(); i++){
            ledList.add(ledMap.get(selectedItem).get(i));
        }
        if(checkConnection() && !ledList.isEmpty()) {
            alLeds.createGroup(ledGroupName, ledList);
        }
    }

    public void selectLedColor(ActionEvent actionEvent) throws Exception{
        if(checkConnection()) {
            alLeds.fadeRGB(ledGroupName, ledColorMap.get(comboBoxLedColor.getValue()).toString(), 0f);
        }
    }

    public void ledsOn(ActionEvent actionEvent) throws Exception {
        if(checkConnection()) {
            alLeds.on(ledGroupName);
            if (comboBoxLedColor.getValue() != null) {
                alLeds.fadeRGB(ledGroupName, ledColorMap.get(comboBoxLedColor.getValue()).toString(), 0f);
            }
        }
    }

    public void ledsOff(ActionEvent actionEvent) throws Exception {
        if(checkConnection()) {
            alLeds.off(ledGroupName);
        }
    }

    //Alle Möglichen Positionen, die der NAO einnehmen kann.
    //Der zweite Wert (float) gibt die Geschwindigkeit an.
    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        if(checkConnection()) {
            posture.goToPosture("Sit", (float) sliderPace.getValue());
        }
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("SitRelax", (float) sliderPace.getValue());
        }
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("SitOnChair", (float) sliderPace.getValue());
        }
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("Stand", (float) sliderPace.getValue());
        }
    }

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("StandInit", (float) sliderPace.getValue());
        }
    }

    //Stand Zero
    public void StandZero(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("StandZero", (float) sliderPace.getValue());
        }
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("LyingBack", (float) sliderPace.getValue());
        }
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("LyingBelly", (float) sliderPace.getValue());
        }
    }


    //Hocken
    public void crouch(ActionEvent actionEvent) throws Exception {
        if(checkConnection()){
            posture.goToPosture("Crouch", (float) sliderPace.getValue());
        }
    }
}

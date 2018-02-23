package sample;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public TextField fieldIp;
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
    public Circle circleTemperatureRA;
    public Circle circleTemperatureRL;
    public Circle circleTemperatureLL;
    public Circle circleTemperatureLA;

    private Connection connection;
    private Audio audio;
    private Move move;
    private Temperature temperature;
    private Led led;
    private Battery battery;
    private Reactor reactor;
    private Timer timer;
    private static int timerInSeconds = 15;
    private static Color defaultColor = Color.valueOf("#666666");
    private Boolean allowConnection = false;

    public class RunPeriodically extends TimerTask {
        public void run() {
            try {
                changeControlls(connection.checkConnection());
                showBatteryState(battery.getBatteryState());
                showTemperatureState(temperature.getTemperature());
            }catch(Exception ex){}
        }
    }

    //Alles was unter dieser Methode steht, wird direkt beim Starten des Programms ausgeführt.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            changeControlls(false);
            //btnConnect.disableProperty().bind(bb);
            connection = new Connection();
            //Befüllen der ComboBoxen/Dropdown-Menüs mit Elementen
            comboBoxSelectNAO.setItems(FXCollections.observableArrayList(connection.getLastConnectionsFromFile()));
            comboBoxLanguage.setItems(FXCollections.observableArrayList("Deutsch", "Englisch"));
        } catch(Exception ex){}
    }

    BooleanBinding bb = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return (fieldIp.getText().isEmpty() && fieldPort.getText().isEmpty());
        }
    };


    public Boolean validateIp(String ip){
        Pattern pattern;
        Matcher matcher;
        String IPADDRESS_PATTERN
                = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public Boolean validatePort(String port){
        if(!port.matches("\\d*")){
            return false;
        }else{
            return true;
        }
    }

    //Wenn NAO-Verbindungsdaten aus dem Dropdown-Menü gewählt wird, wird die IP-Adresse und der Port in die beiden entsprechenden Textfelder eingetragen.
    public void setConnectionData(ActionEvent actionEvent) {
        fieldIp.clear();
        fieldPort.clear();
        String temp = comboBoxSelectNAO.getValue().toString();
        fieldIp.appendText(temp.substring( 0, temp.indexOf(":")));
        fieldPort.appendText(temp.substring(temp.indexOf(":") + 1));
    }

    //Verbindung zum NAO aufbauen
    public void startConnection(ActionEvent actionEvent) throws Exception{
        String robotUrl = fieldIp.getText().toString() + ":" + fieldPort.getText().toString();
        connection.startConnection("tcp://" + robotUrl);
        if(connection.checkConnection()){
            connection.writeConnectionToFile(robotUrl);
            audio = new Audio(connection);
            move = new Move(connection);
            battery = new Battery(connection);
            temperature = new Temperature(connection);
            led = new Led(connection);
            reactor = new Reactor(connection, audio);
            reactor.subscribe();
            timer = new Timer();
            timer.schedule(new RunPeriodically(), 0, timerInSeconds * 1000);

            //Dropdown-Felder mit Inhalt füllen
            //Die verschiedenen Leds und Farben werden über die Instanzmethode der Led-Klasse bezogen. Durch "keySet" werden nur die Schlüssel als Werte eingetragen.
            comboBoxLedGroup.setItems(FXCollections.observableArrayList(led.getledMap().keySet()));
            comboBoxLedColor.setItems(FXCollections.observableArrayList(led.getledColorMap().keySet()));
            comboBoxLanguage.getSelectionModel().select(audio.getLanguage());

            //AudioFiles laden
            Boolean noAudioFiles = true;
            if(!audio.getAudioFiles().isEmpty()){
                listSoundFiles.setItems(FXCollections.observableArrayList(audio.getAudioFiles()));
                noAudioFiles = false;
            }
            listSoundFiles.setDisable(noAudioFiles);
        }
    }

    public void changeControlls(Boolean connected) throws Exception{
        btnW.setDisable(!connected);
        btnStandZero.setDisable(!connected);
        btnStand.setDisable(!connected);
        btnS.setDisable(!connected);
        btnStandInit.setDisable(!connected);
        btnSitRelax.setDisable(!connected);
        btnSit.setDisable(!connected);
        btnSitChair.setDisable(!connected);
        btnSitRelax.setDisable(!connected);
        btnQ.setDisable(!connected);
        btnE.setDisable(!connected);
        btnA.setDisable(!connected);
        btnD.setDisable(!connected);
        btnCrouch.setDisable(!connected);
        btnLedOff.setDisable(!connected);
        btnLedOn.setDisable(!connected);
        btnLyingBack.setDisable(!connected);
        btnLyingBelly.setDisable(!connected);
        btnPlay.setDisable(!connected);
        comboBoxLedColor.setDisable(!connected);
        comboBoxLedGroup.setDisable(!connected);
        comboBoxLanguage.setDisable(!connected);
        toggleRest.setDisable(!connected);
        toggleWakeUp.setDisable(!connected);
        btnCloseConnection.setDisable(!connected);
        btnConnect.setDisable(connected);
        if(connected){
            circleConnectionState.setFill(Color.GREEN);
        }else{
            circleConnectionState.setFill(Color.RED);
            fieldBattery.clear();
            aPaneBatteryBar.getStyleClass().remove("high");
            aPaneBatteryBar.getStyleClass().remove("medium");
            aPaneBatteryBar.getStyleClass().remove("low");
            aPaneBatteryBar.setStyle("-fx-pref-width:100px;");
            circleTemperatureRL.setFill(defaultColor);
            circleTemperatureRA.setFill(defaultColor);
            circleTemperatureLL.setFill(defaultColor);
            circleTemperatureLA.setFill(defaultColor);
            listSoundFiles.setItems(FXCollections.observableArrayList(""));
            comboBoxLedGroup.setItems(FXCollections.observableArrayList(""));
            comboBoxLedColor.setItems(FXCollections.observableArrayList(""));
        }
    }

    public void stopConnection(ActionEvent actionEvent) throws Exception{
        reactor.unsubscribe();
        timer.cancel();
        connection.stopConnection();
        changeControlls(connection.checkConnection());
    }

    public void showBatteryState(int state) {
        fieldBattery.clear();
        fieldBattery.appendText(String.valueOf(state + "%"));
        if (state >= 50) {
            aPaneBatteryBar.getStyleClass().add("high");
        } else if (state >= 25 ) {
            aPaneBatteryBar.getStyleClass().add("medium");
        } else {
            aPaneBatteryBar.getStyleClass().add("low");
        }
        aPaneBatteryBar.setStyle("-fx-pref-width:" + state + "px;");
        System.out.println("Batterie: " + state);
    }


    public void showTemperatureState(String state){
        Color color = defaultColor;
        if(state.contains("0")){
            color = Color.GREEN;
        } else if (state.contains("1")){
            color = Color.ORANGE;
        } else if (state.contains("2")){
            color = Color.RED;
        }
        if(state.contains("LArm")) circleTemperatureLA.setFill(color);
        if(state.contains("LLeg")) circleTemperatureLL.setFill(color);
        if(state.contains("RArm")) circleTemperatureRA.setFill(color);
        if(state.contains("RLeg")) circleTemperatureRL.setFill(color);
        System.out.println("Tempereatur: " + state);
    }

    //NAO aufwecken
    public void wakeUp(ActionEvent actionEvent) throws Exception {
        move.wakeUp();
    }

    //NAO auf Standby setzen
    public void rest(ActionEvent actionEvent) throws Exception {
        move.rest();
    }

    //Kopf des NAO drehen und neigen
    public void lookUpOrDown(MouseEvent mouseEvent) throws Exception{
        move.lookUpOrDown(sliderHeadUpDown.getValue(), sliderPace.getValue());
    }

    public void lookLeftOrRight(MouseEvent mouseEvent) throws Exception{
        move.lookLeftOrRight(sliderHeadUpDown.getValue(), sliderPace.getValue());
    }

    public void lookReset(ActionEvent actionEvent) throws Exception {
        move.resetHead(sliderPace.getValue());
        sliderHeadLeftRight.setValue(0);
        sliderHeadUpDown.setValue(0);
    }

    //NAO solange bewegen lassen, bis die Maus losgelassen wird:
    //Jede Bewegung ist an den entsprechenden Button im Scene Builder verknüpft (unter "Mouse" > "On Mouse Pressed".
    //Alle Buttons haben zusätzlich die Methode "stopMove" (unter "Mouse" > "On Mouse Released" verknüpft.
    //Den Methoden muss das MouseEvent (und nicht ActionEvent) übergeben werden, sonst wird sie in der Laufzeit mit einem Fehler abgebrochen.
    public void moveForward(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("W", sliderPace.getValue());
    }

    public void moveBackwards(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("S", sliderPace.getValue());
    }

    public void moveLeft(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("A", sliderPace.getValue());
    }

    public void moveRight(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("D", sliderPace.getValue());
    }

    public void turnRight(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("E", sliderPace.getValue());
    }

    public void turnLeft(MouseEvent mouseEvent) throws Exception {
        move.moveTowards("Q", sliderPace.getValue());
    }

    public void stopMove(MouseEvent mouseEvent) throws Exception {
        move.stopMoving();
    }

    //NAO durch Tastatur steuern:
    //Methode "getKeyPressed" ist mit "On Key Pressed" im Scene Builder verknüpft.
    //Wird eine entsprechende Taste erkannt wird der NAO entsprechend bewegt.
    public void getKeyPressed(KeyEvent keyEvent) throws Exception {
        if(connection.checkConnection()) {
            move.moveTowards(keyEvent.getCode().toString(), sliderPace.getValue());
        }
    }

    //Methode "getKeyReleased" ist mit "On Key Released" im Scene Builder verknüpft.
    //Wenn eine der Tasten WASDQE losgelassen wird, wird die Bewegung des NAOs gestoppt.
    //Ansonsten würde der NAO unendlich lange laufen, auch wenn die Taste losgelassen wird.
    public void getKeyReleased(KeyEvent keyEvent) throws Exception {
        if(connection.checkConnection()) {
            move.stopOnKeyReleased(keyEvent);
        }
    }

    //Alle Möglichen Positionen, die der NAO einnehmen kann.
    //Der zweite Wert (float) gibt die Geschwindigkeit an.
    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        move.changePosture("Sit", (float) sliderPace.getValue());
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        move.changePosture("SitRelax", (float) sliderPace.getValue());
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        move.changePosture("SitOnChair", (float) sliderPace.getValue());
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        move.changePosture("Stand", (float) sliderPace.getValue());
    }

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        move.changePosture("StandInit", (float) sliderPace.getValue());
    }

    //Stand Zero
    public void StandZero(ActionEvent actionEvent) throws Exception {
        move.changePosture("StandZero", (float) sliderPace.getValue());
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        move.changePosture("LyingBack", (float) sliderPace.getValue());
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        move.changePosture("LyingBelly", (float) sliderPace.getValue());
    }

    //Hocken
    public void crouch(ActionEvent actionEvent) throws Exception {
        move.changePosture("Crouch", (float) sliderPace.getValue());
    }

    public void showPosture(String posture){

    }

    public void setLanguage(ActionEvent actionEvent) throws Exception{
        audio.setLanguage(comboBoxLanguage.getValue().toString());
    }
    //NAO sagt, was in die Sprechblase geschrieben wurde. Die Methode wird durch den Play-Button gestartet.
    public void sayText(ActionEvent actionEvent) throws Exception {
        if(fieldSound.getText() != null && comboBoxLanguage.getValue().toString() != null) {
            audio.saySomething(fieldSound.getText(), sliderVolume.getValue(), sliderTalkingSpeed.getValue(), sliderPitch.getValue());
        }
    }

    public void playSoundFile(MouseEvent mouseEvent) throws Exception{
        //System.out.println(listSoundFiles.getSelectionModel().getSelectedItem().toString());
        audio.playSoundFile(listSoundFiles.getSelectionModel().getSelectedItem().toString(), sliderVolume.getValue());
    }

    public void resetAudioSettings(ActionEvent actionEvent) throws Exception{
        sliderTalkingSpeed.setValue(100);
        sliderPitch.setValue(100);
        sliderVolume.setValue(80);
    }

    public void selectLed(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedGroup.getValue() != null) {
            led.createLedGroup(comboBoxLedGroup.getValue().toString());
        }
    }
    public void selectLedColor(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedColor.getValue() != null) {
            led.changeLedColor(comboBoxLedColor.getValue().toString());
        }
    }
    public void ledsOn(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedColor.getValue() != null) {
            led.turnLedsOn(comboBoxLedColor.getValue().toString());
        }
    }
    public void ledsOff(ActionEvent actionEvent) throws Exception{
        led.turnLedsOff();
    }

    /*
    public void startConnection(ActionEvent actionEvent) throws Exception {
        String robotUrl = "tcp://" + fieldIp.getText().toString() + ":" + fieldPort.getText().toString();
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
                alMemory = new ALMemory(session);
                reactor = new ReactToEvents();
                reactor.run(session);
                timer = new Timer();
                timer.schedule(new RunPeriodically(), 0, timerInSeconds * 1000);

                //Audio-Parameter auslesen und setzen
                if(tts.getLanguage().toString().contains("German")){
                    comboBoxLanguage.getSelectionModel().select("Deutsch");
                    tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + "Du bist verbunden!");
                } else if (tts.getLanguage().toString().contains("English")){
                    comboBoxLanguage.getSelectionModel().select("Englisch");
                    tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + "You are connected!");
                }

                //AudioFiles laden
                Boolean noAudioFiles = true;
                if(audio.getMethodList().contains("getSoundSetFileNames")) {
                    if(audio.getInstalledSoundSetsList().contains("Aldebaran")) {
                        listSoundFiles.setItems(FXCollections.observableArrayList(audio.getSoundSetFileNames("Aldebaran")));
                        audio.loadSoundSet("Aldebaran");
                        noAudioFiles = false;
                    }
                }
                listSoundFiles.setDisable(noAudioFiles);

                //Schreiben der momentan genutzen Verbindung in eine Datei "connection.txt"
                writeConnectionToFile(fieldIp.getText().toString(), fieldPort.getText().toString());
                //Außerdem wird direkt die Nao-Auswahlliste neu aus der Datei "connection.txt" eingelesen, um den neusten Eintrag hinzuzufügen.
                //comboBoxSelectNAO.setItems(FXCollections.observableArrayList(getLastConnectionsFromFile()));
                //comboBoxSelectNAO.getSelectionModel().selectLast();
            }
        } catch (Exception ex) {
            //Anzeigen der Fehlermeldung in einem kleinen Popup-Fenster
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void stopConnection(ActionEvent actionEvent) throws Exception{
        if(session.isConnected()) {
            reactor.unsubscribe(session);
            timer.cancel();
            tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + "Ciao!");
            session.close();
        }
        checkConnection();
    }

    public Boolean checkConnection() throws Exception{
        if(session.isConnected()){
            //Setzen des Verbindungsstatus-Kreises auf Grün
            circleConnectionState.setFill(Color.GREEN);
            btnConnect.setDisable(true);
            btnCloseConnection.setDisable(false);
            return true;
        } else {
            circleConnectionState.setFill(Color.RED);
            //circleTemperature.setFill(Color.valueOf("#666666"));
            btnCloseConnection.setDisable(true);
            btnConnect.setDisable(false);
            listSoundFiles.setItems(FXCollections.observableArrayList(""));
            cleanUpAfterDisconnect();
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
        /*
    public void setLanguage(ActionEvent actionEvent) throws Exception{
        if(connection.checkConnection()){
            if (comboBoxLanguage.getValue().toString() == "Deutsch") {
                tts.setLanguage("German");
            } else if (comboBoxLanguage.getValue().toString() == "Englisch"){
                tts.setLanguage("English");
            }
        }
    }


    public void sayText(ActionEvent actionEvent) throws Exception {
        if(connection.checkConnection() && fieldSound.getText() != null && comboBoxLanguage.getValue().toString() != null) {
            tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + fieldSound.getText());
        }
    }


    public void playSoundFile(MouseEvent mouseEvent) throws Exception{
        if(connection.checkConnection()) {
            //System.out.println(listSoundFiles.getSelectionModel().getSelectedItem().toString());
            //audio.playSoundSetFile("Aldebaran", listSoundFiles.getSelectionModel().getSelectedItem().toString(), 0f, (float) sliderVolume.getValue() / 100, 0f, false);
        }
    }
    */


/*
    public void selectLed(ActionEvent actionEvent) throws Exception {
        ledList.clear();
        String selectedItem = comboBoxLedGroup.getValue().toString();
        for (int i=0; i < ledMap.get(selectedItem).size(); i++){
            ledList.add(ledMap.get(selectedItem).get(i));
        }
        if(connection.checkConnection() && !ledList.isEmpty()) {
            alLeds.createGroup(ledGroupName, ledList);
        }
    }

    public void selectLedColor(ActionEvent actionEvent) throws Exception{
        if(connection.checkConnection()) {
            alLeds.fadeRGB(ledGroupName, ledColorMap.get(comboBoxLedColor.getValue()).toString(), 0f);
        }
    }

    public void ledsOn(ActionEvent actionEvent) throws Exception {
        if(connection.checkConnection()) {
            alLeds.on(ledGroupName);
            if (comboBoxLedColor.getValue() != null) {
                alLeds.fadeRGB(ledGroupName, ledColorMap.get(comboBoxLedColor.getValue()).toString(), 0f);
            }
        }
    }

    public void ledsOff(ActionEvent actionEvent) throws Exception {
        if(connection.checkConnection()) {
            alLeds.off(ledGroupName);
        }
    }
                /*
            //Audio-Parameter auslesen und setzen
            if(tts.getLanguage().toString().contains("German")){
                comboBoxLanguage.getSelectionModel().select("Deutsch");
                tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + "Du bist verbunden!");
            } else if (tts.getLanguage().toString().contains("English")){
                comboBoxLanguage.getSelectionModel().select("Englisch");
                tts.say("\\vol=" + (int) sliderVolume.getValue() + "\\\\vct=" + (int) sliderPitch.getValue() + "\\\\rspd=" + (int) sliderTalkingSpeed.getValue() + "\\" + "You are connected!");
            }
            Boolean noAudioFiles = true;
            if(audio.getMethodList().contains("getSoundSetFileNames")) {
                if(audio.getInstalledSoundSetsList().contains("Aldebaran")) {
                    listSoundFiles.setItems(FXCollections.observableArrayList(audio.getSoundSetFileNames("Aldebaran")));
                    audio.loadSoundSet("Aldebaran");
                    noAudioFiles = false;
                }
            }
            listSoundFiles.setDisable(noAudioFiles);
                        /*
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
    */
}

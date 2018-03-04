package sample;

import javafx.beans.binding.Bindings;
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

public class Controller implements Initializable {

    public Button btnPlay;
    public TextField fieldSound;
    public ComboBox comboBoxLanguage;
    public Slider sliderPitch;
    public Label labelPitch;
    public ImageView imgNAO;
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
    public ComboBox comboBoxLedColor;
    public ListView listSoundFiles;
    public Slider sliderTalkingSpeed;
    public AnchorPane aPaneBattery;
    public Button btnResetAudioSettings;
    public Slider sliderHeadUpDown;
    public Slider sliderHeadLeftRight;
    public TextField fieldBattery;
    public Circle circleTemperatureRA;
    public Circle circleTemperatureRL;
    public Circle circleTemperatureLL;
    public Circle circleTemperatureLA;
    public Button btnResetHead;
    public Button btnLedOff;
    public ProgressBar progressBarBattery;
    public ImageView imgCharging;
    public Button btnRest;
    public Button btnWakeUp;
    public Button btnLookUp;
    public Button btnLookLeft;
    public Button btnLookDown;
    public Button btnLookRight;

    private Connection connection;
    private Audio audio;
    private Move move;
    private Temperature temperature;
    private Led led;
    private Battery battery;
    private Reactor reactor;
    private Timer timer;

    //Zeitintervall, in welchem die Aktionen in Methode "run" ausgeführt werden
    private static final int timerInSeconds = 15;

    //"Default"-Farbe der Status-Leuchten für die Tempereaturanzeige (wenn kein Wert vorhanden/zurückgegeben wird)
    private static final Color defaultColor = Color.valueOf("#666666");

    //Namen der CSS-Klassen für Einfärben der Akkustandsanzeige
    private static final String RED_BAR    = "red-bar";
    private static final String YELLOW_BAR = "yellow-bar";
    private static final String ORANGE_BAR = "orange-bar";
    private static final String GREEN_BAR  = "green-bar";
    private static final String[] barColorStyleClasses = { RED_BAR, ORANGE_BAR, YELLOW_BAR, GREEN_BAR };

    //Hier werden die Methoden gelagert, die ab einer bestehenden Verbindung mit dem NAO periodisch ausgeführt werden.
    public class RunPeriodically extends TimerTask {
        public void run() {
            try {
                changeControlls(connection.checkConnection());
                showBatteryState(battery.getBatteryState());
                showTemperatureState(temperature.getTemperature());
            }catch(Exception ex){}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //Befüllen des Dropdown-Menüs mit den letzten Verbindungen
            connection = new Connection();
            comboBoxSelectNAO.setItems(FXCollections.observableArrayList(connection.getLastConnectionsFromFile()));

            //Aktivieren der Validierung des Feldes für IP-Adresse und Port
            enableUrlValidation(true);

            //Befüllen der Dropdown-Liste für die Sprachen
            comboBoxLanguage.setItems(FXCollections.observableArrayList("Deutsch", "Englisch"));
        } catch(Exception ex){}
    }

    //Übertragen von IP-Adresse und des Ports, wenn aus dem Dropdown-Menü der letzten Verbindugen gewählt wurde
    public void setConnectionData(ActionEvent actionEvent) {
        fieldIp.clear();
        fieldPort.clear();
        String temp = comboBoxSelectNAO.getValue().toString();
        fieldIp.appendText(temp.substring( 0, temp.indexOf(":")));
        fieldPort.appendText(temp.substring(temp.indexOf(":") + 1));
    }

    //Verbindungsversuch wird gestartet
    public void startConnection(ActionEvent actionEvent) throws Exception{
        String robotUrl = fieldIp.getText().toString() + ":" + fieldPort.getText().toString();
        connection.startConnection("tcp://" + robotUrl);

        //Überprüfen der Verbindung; wenn vorhanden, wird die Verbindung in die Datei geschrieben
        if(connection.checkConnection()){
            connection.writeConnectionToFile(robotUrl);

            audio = new Audio(connection);
            move = new Move(connection);
            battery = new Battery(connection);
            temperature = new Temperature(connection);
            led = new Led(connection);

            //Subscriben der Events des NAOs
            reactor = new Reactor(connection, audio);
            reactor.subscribe();

            //Dem Timer-Objekt werden über die Klasse RunPeriodically alle Methoden übergeben, welche zyklisch wiederholt werden sollen
            //Zweiter Parameter gibt an, ab wann die erste Ausführung beginnt; der dritte Parameter gibt die Zeitdauer zur nächsten Ausführung an (in Milisekunden)
            timer = new Timer();
            timer.schedule(new RunPeriodically(), 0, timerInSeconds * 1000);

            //Befüllen der Dropdown-Felder mit Inhalt:
            //Die verschiedenen Leds und Farben werden über die Led-Klasse bezogen. Die Methoden geben Maps zurück, wobei durch "keySet" nur die Schlüssel als Werte eingetragen werden.
            comboBoxLedGroup.setItems(FXCollections.observableArrayList(led.getledMap().keySet()));
            comboBoxLedColor.setItems(FXCollections.observableArrayList(led.getledColorMap().keySet()));
            comboBoxLanguage.getSelectionModel().select(audio.getLanguage());

            //AudioFiles (falls vorhanden) vom NAO laden
            //getAudioFiles liefert eine String_liste zurück, welche die einzelnen Soundfiles enthält und an die Listview weitergibt; falls es keine gibt ist die Liste leer.
            if(!audio.getAudioFiles().isEmpty()){
                listSoundFiles.setItems(FXCollections.observableArrayList(audio.getAudioFiles()));
            }
            //Enablen/Disablen der ListView, je nach dem ob Sounds vorhanden sind oder nicht.
            listSoundFiles.setDisable(!audio.getBoolAudioFilesAvailable());
        }
    }

    //Anpassen der Buttons, Textfelder usw. je nachdem ob eine Verbindung vorhanden ist oder nicht. Diese Methode wird periodisch nach erfolgreichem Verbindungsaufbau aufgerufen.
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
        btnLookDown.setDisable(!connected);
        btnLookUp.setDisable(!connected);
        btnLookLeft.setDisable(!connected);
        btnLookRight.setDisable(!connected);
        btnCrouch.setDisable(!connected);
        btnLedOn.setDisable(!connected);
        btnLyingBack.setDisable(!connected);
        btnLyingBelly.setDisable(!connected);
        btnPlay.setDisable(!connected);
        comboBoxLedColor.setDisable(!connected);
        comboBoxLedGroup.setDisable(!connected);
        comboBoxLanguage.setDisable(!connected);
        sliderPace.setDisable(!connected);
        sliderVolume.setDisable(!connected);
        sliderHeadUpDown.setDisable(!connected);
        sliderHeadLeftRight.setDisable(!connected);
        sliderTalkingSpeed.setDisable(!connected);
        sliderPitch.setDisable(!connected);
        btnResetAudioSettings.setDisable(!connected);
        fieldSound.setDisable(!connected);
        btnWakeUp.setDisable(!connected);
        btnRest.setDisable(!connected);
        btnCloseConnection.setDisable(!connected);
        btnResetHead.setDisable(!connected);
        btnLedOff.setDisable(!connected);
        enableUrlValidation(!connected);
        if(connected){
            circleConnectionState.setFill(Color.GREEN);
            btnConnect.setDisable(connected);
        }else{
            //Zürücksetzen von Status-Ampeln und Leeren von Textfeldenr und Listen
            circleConnectionState.setFill(Color.RED);
            fieldBattery.clear();
            progressBarBattery.setProgress(0);
            circleTemperatureRL.setFill(defaultColor);
            circleTemperatureRA.setFill(defaultColor);
            circleTemperatureLL.setFill(defaultColor);
            circleTemperatureLA.setFill(defaultColor);
            listSoundFiles.setItems(FXCollections.observableArrayList(""));
            comboBoxLedGroup.setItems(FXCollections.observableArrayList(""));
            comboBoxLedColor.setItems(FXCollections.observableArrayList(""));
        }
    }

    //Schaltet die Validierung von IP-Adresse und Port ein/aus. Wird initial beim Start des Programms und dann periodisch nach Aufbau einer Verbindung aufgerufen.
    //Wenn keine Verbindung aufgebaut ist, wird die Validierung aktiviert; sobald sie vorhanden ist, wird die Validierung deaktiviert.
    public void enableUrlValidation(Boolean enable){
        if(enable){
            //Klasse Validation liefert nach Prüfung auf IP oder Port einen Boolean-Wert zurück. Ist einer der beiden False (oder eines der beiden Felder leer) wird  der Button deaktiviert
            btnConnect.disableProperty().bind(Bindings.createBooleanBinding(
                    () ->!Validation.validateIp(fieldIp.getText()) || !Validation.validatePort(fieldPort.getText()) || fieldPort.getText().isEmpty(),
                    fieldIp.textProperty(), fieldPort.textProperty()));
        }
        else {
            //Deaktivieren der Überprüfung
            btnConnect.disableProperty().unbind();}
    }

    //Verbindung wird manuell abebrochen
    public void stopConnection(ActionEvent actionEvent) throws Exception{
        reactor.unsubscribe();
        timer.cancel();
        connection.stopConnection();
        changeControlls(connection.checkConnection()); //Damit werden u.a. alle Buttons wieder deaktiviert
    }

    //Anzeigen bzw. Anpassen des Batteriestatus: Über - oder unterschreitet der Wert bestimmte Schwellwerte wird die Farbe geändert.
    public void showBatteryState(int state) {
        ProgressBar bar = progressBarBattery;
        fieldBattery.clear();
        fieldBattery.appendText(String.valueOf(state + "%"));
        if (state < 15){
            setBarStyleClass(bar, RED_BAR);
        } else if(state < 25){
            setBarStyleClass(bar, ORANGE_BAR);
        } else if (state < 50){
            setBarStyleClass(bar, YELLOW_BAR);
        } else {
            setBarStyleClass(bar, GREEN_BAR);
        }
        //Teilen des Werts durch 100: setProgress nimmt Werte zwischen 0 und 1, der NAO liefert prozentuale Werte zurück
        progressBarBattery.setProgress((float) state / 100);
    }

    //Hinzufügen der entsprechenden CSS-Klasse
    public void setBarStyleClass(ProgressBar bar, String barStyleClass) {
        bar.getStyleClass().removeAll(barColorStyleClasses);
        bar.getStyleClass().add(barStyleClass);
    }

    //Zeigen bzw. Verstecken des Bildes, welches Zeigt, dass gerade der Akku geladen wird
    //Wird automatisch in der Klasse Reactor aufgerufen, wenn das entsprechende Evenet vom NAO geworfen wird
    public void showBatteryCharging(Boolean charging){
            imgCharging.setVisible(charging);
    }

    //Zeigen des Tempereaturstatus; wird periodisch aufgerufen
    //Je nachdem welcher Status geliefert wird, wird die Farbe ausgewählt und am betroffenen Körperteil übernommen.
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
        //System.out.println("Tempereatur: " + state);
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
    //Mit Slider neigen
    public void lookUpOrDown(MouseEvent mouseEvent) throws Exception{
        move.lookUpOrDown(sliderHeadUpDown.getValue(), sliderPace.getValue());
    }

    //Mit Slider drehen
    public void lookLeftOrRight(MouseEvent mouseEvent) throws Exception{
        move.lookLeftOrRight(sliderHeadLeftRight.getValue(), sliderPace.getValue());
    }

    //Mit Buttons auf Dashboard steuern
    public void lookUp(ActionEvent actionEvent) throws Exception {
        move.look("up", sliderPace.getValue());
    }

    public void lookDown(ActionEvent actionEvent) throws Exception {
        move.look("down", sliderPace.getValue());
    }

    public void lookLeft(ActionEvent actionEvent) throws Exception {
        move.look("left", sliderPace.getValue());
    }

    public void lookRight(ActionEvent actionEvent) throws Exception {
        move.look("right", sliderPace.getValue());
    }

    //Kopf auf "Ursprungspostion" zurücksetzen; Slider werden auch zurückgesetzt
    public void lookReset(ActionEvent actionEvent) throws Exception {
        move.resetHead(sliderPace.getValue());
        sliderHeadLeftRight.setValue(0);
        sliderHeadUpDown.setValue(0);
    }

    //NAO solange laufen lassen, bis die Maus losgelassen wird:
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
    //Der entsprechende KeyEvent wird als String mit dem Wert der Geschwindigkeit weitergegeben
    public void getKeyPressed(KeyEvent keyEvent) throws Exception {
        if(connection.checkConnection()) {
            move.moveTowards(keyEvent.getCode().toString(), sliderPace.getValue());
        }
    }

    //Methode "getKeyReleased" ist mit "On Key Released" im Scene Builder verknüpft.
    public void getKeyReleased(KeyEvent keyEvent) throws Exception {
        if(connection.checkConnection()) {
            move.stopOnKeyReleased(keyEvent);
        }
    }


    //Alle Möglichen Positionen, die der NAO einnehmen kann.
    //Die Positionen werden ebenfalls über die "Hilfs"-klasse Move verwaltet
    //Die NAO-spezifischen Namen der möglichen Haltungen werden schon hier direkt an die Methode übergeben
    //Sitzen (normal)
    public void sit(ActionEvent actionEvent) throws Exception {
        move.changePosture("Sit");
    }

    //Sitzen (relaxed)
    public void sitRelax(ActionEvent actionEvent) throws Exception {
        move.changePosture("SitRelax");
    }

    //Sitzen (Stuhl)
    public void sitOnChair(ActionEvent actionEvent) throws Exception {
        move.changePosture("SitOnChair");
    }

    //Stehen
    public void stand(ActionEvent actionEvent) throws Exception {
        move.changePosture("Stand");
    }

    // Stand Init
    public void StandInit(ActionEvent actionEvent) throws Exception {
        move.changePosture("StandInit");
    }

    //Stand Zero
    public void StandZero(ActionEvent actionEvent) throws Exception {
        move.changePosture("StandZero");
    }

    //Auf den Rücken legen
    public void lyingBack(ActionEvent actionEvent) throws Exception {
        move.changePosture("LyingBack");
    }

    //Auf den Bauch legen
    public void lyingBelly(ActionEvent actionEvent) throws Exception {
        move.changePosture("LyingBelly");
    }

    //Hocken
    public void crouch(ActionEvent actionEvent) throws Exception {
        move.changePosture("Crouch");
    }

    //Sprache wird im Dropdown-Menü geändert
    public void setLanguage(ActionEvent actionEvent) throws Exception{
        audio.setLanguage(comboBoxLanguage.getValue().toString());
    }

    //NAO Text sprechen lassen
    //Erst wird geprüft, ob das Textfeld auch nicht leer ist und ob eine Sprache ausgewählt wurde
    public void sayText(ActionEvent actionEvent) throws Exception {
        if(fieldSound.getText() != null && comboBoxLanguage.getValue().toString() != null) {
            audio.saySomething(fieldSound.getText(), sliderVolume.getValue(), sliderTalkingSpeed.getValue(), sliderPitch.getValue());
        }
    }

    //NAO Soundfile abspielen lassen
    public void playSoundFile(MouseEvent mouseEvent) throws Exception{
        //System.out.println(listSoundFiles.getSelectionModel().getSelectedItem().toString());
        audio.playSoundFile(listSoundFiles.getSelectionModel().getSelectedItem().toString(), sliderVolume.getValue());
    }

    //Zurücksetzen der Audio-Einstellungen in der GUI (nicht auf dem NAO!)
    public void resetAudioSettings(ActionEvent actionEvent) throws Exception{
        sliderTalkingSpeed.setValue(100);
        sliderPitch.setValue(100);
        sliderVolume.setValue(80);
    }

    //Augen-LED auswählen
    public void selectLed(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedGroup.getValue() != null) {
            led.createLedGroup(comboBoxLedGroup.getValue().toString());
        }
    }

    //LED-Farbe auswählen
    public void selectLedColor(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedColor.getValue() != null) {
            led.changeLedColor(comboBoxLedColor.getValue().toString());
        }
    }

    //LEDs anschalten
    public void ledsOn(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedColor.getValue() != null) {
            led.turnLedsOn(comboBoxLedColor.getValue().toString());
        }
    }

    //LEDs ausschalten
    public void ledsOff(ActionEvent actionEvent) throws Exception{
        if(comboBoxLedColor.getValue() != null) {
            led.turnLedsOff();
        }
    }
}

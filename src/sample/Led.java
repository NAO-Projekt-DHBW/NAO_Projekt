package sample;

import com.aldebaran.qi.helper.proxies.ALLeds;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Led {
    private Connection connection;
    private ALLeds alLeds;
    private Map<String, List<String>> ledMap = new HashMap<String, List<String>>();
    private Map<String, String> ledColorMap = new HashMap<String, String>();
    private String ledGroupName = "ledGroup";
    private List<String> ledList = new ArrayList<String>();

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klasse
    public Led(Connection connection) throws Exception{
        this.connection = connection;
        alLeds = new ALLeds(this.connection.getSession());

        //Befüllen der Maps für die Augen und die Farben
        //Mit dem ersten Wert (Key) wird der Bezeichner festgelegt. Diese Keys werden dann im Dropdown-Menü angezeigt.
        //Der nachfolgende Parameter beinhaltet die Nao-spezifischen Ausdrücke.
        // Somit kann die Map dazu verwendet werden von den Auswahlmöglichkeiten im Dropdown-Menü direkt die NAO-spezifischen Werte parat zu haben

        //Map für die Auswahl der Augen-LEDs
        ledMap.put("Augen", FXCollections.observableArrayList("FaceLeds"));
        ledMap.put("Linkes Auge", FXCollections.observableArrayList("FaceLedsLeftBottom", "FaceLedsLeftExternal", "FaceLedsLeftInternal", "FaceLedsLeftTop"));
        ledMap.put("Rechtes Auge", FXCollections.observableArrayList("FaceLedsRightBottom", "FaceLedsRightExternal", "FaceLedsRightInternal", "FaceLedsRightTop"));

        //Map für die Auswahl der Farben für die LEDs
        ledColorMap.put("Weiß", "white");
        ledColorMap.put("Rot", "red");
        ledColorMap.put("Grün", "green");
        ledColorMap.put("Blau", "blue");
        ledColorMap.put("Gelb", "yellow");
        ledColorMap.put("Magenta", "magenta");
        ledColorMap.put("Cyan", "cyan");
    }

    //Getter-Methoden für beide Maps
    public Map getledMap(){
        return ledMap;
    }

    public Map getledColorMap(){
        return ledColorMap;
    }

    //NAO-LED-Gruppe erstellen (Aufruf bei Auswahl aus Dropdown-Menü für Augen-LEDs)
    //Übermittelter Parameter ist einer der obrigen Keys der ledMap
    //Hilfsliste "ledList" wird verwendet, um die Gruppe zu erstellen
    public void createLedGroup(String selectedLed) throws Exception {
        ledList.clear();
        for (int i=0; i < ledMap.get(selectedLed).size(); i++){
            ledList.add(ledMap.get(selectedLed).get(i));
        }
        //Zur Sicherheit: Überprüfen der Verbindung und ob die Liste auch wirklich nicht leer ist
        if(connection.checkConnection() && !ledList.isEmpty()) {
            alLeds.createGroup(ledGroupName, ledList);
        }
    }

    //LED-Farbe ändern (Aufruf bei Auswahl aus Dropdown-Menü für Farben)
    //Der Float-Wert gibt die Übergangsdauer zwischen den Farben an
    public void changeLedColor(String selectedColor) throws Exception{
        if(connection.checkConnection() && !ledList.isEmpty()) {
            alLeds.fadeRGB(ledGroupName, ledColorMap.get(selectedColor), 0f);
        }
    }

    //LEDs anschalten; nur wenn die ledList nicht leer ist!
    //Außerdem wird die aktuell gewählte Farbe übernommen (falls eine gewählt ist)
    public void turnLedsOn(String selectedColor) throws Exception {
        if(connection.checkConnection() && !ledList.isEmpty()) {
            alLeds.on(ledGroupName);
            //System.out.println(alLeds.listGroup(ledGroupName));
            if (selectedColor != null) {
                alLeds.fadeRGB(ledGroupName, ledColorMap.get(selectedColor), 0f);
            }
        }
    }

    //LEDs ausschalten; nur wenn die ledList nicht leer ist!
    public void turnLedsOff() throws Exception {
        if(connection.checkConnection() && !ledList.isEmpty()){
            alLeds.off(ledGroupName);
        }
    }
}

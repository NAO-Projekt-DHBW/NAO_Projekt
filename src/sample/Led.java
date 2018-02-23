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
    private static Map<String, List<String>> ledMap = new HashMap<String, List<String>>();
    private static Map<String, String> ledColorMap = new HashMap<String, String>();
    private static String ledGroupName = "ledGroup";
    private static List<String> ledList = new ArrayList<String>();

    public Led(Connection connection) throws Exception{
        this.connection = connection;
        alLeds = new ALLeds(this.connection.getSession());
        ledMap.put("Augen", FXCollections.observableArrayList("FaceLeds"));
        ledMap.put("Linkes Auge", FXCollections.observableArrayList("FaceLedsLeftBottom", "FaceLedsLeftExternal", "FaceLedsLeftInternal", "FaceLedsLeftTop"));
        ledMap.put("Rechtes Auge", FXCollections.observableArrayList("FaceLedsRightBottom", "FaceLedsRightExternal", "FaceLedsRightInternal", "FaceLedsRightTop"));
        ledColorMap.put("Weiß", "white");
        ledColorMap.put("Rot", "red");
        ledColorMap.put("Grün", "green");
        ledColorMap.put("Blau", "blue");
        ledColorMap.put("Gelb", "yellow");
        ledColorMap.put("Magenta", "magenta");
        ledColorMap.put("Cyan", "cyan");
    }

    public Map getledMap(){
        return ledMap;
    }

    public Map getledColorMap(){
        return ledColorMap;
    }

    public void createLedGroup(String selectedLed) throws Exception {
        ledList.clear();
        for (int i=0; i < ledMap.get(selectedLed).size(); i++){
            ledList.add(ledMap.get(selectedLed).get(i));
        }
        if(connection.checkConnection() && !ledList.isEmpty()) {
            alLeds.createGroup(ledGroupName, ledList);
        }
    }

    public void changeLedColor(String selectedColor) throws Exception{
        if(connection.checkConnection()) {
            alLeds.fadeRGB(ledGroupName, ledColorMap.get(selectedColor), 0f);
        }
    }

    public void turnLedsOn(String selectedColor) throws Exception {
        if(connection.checkConnection()) {
            alLeds.on(ledGroupName);
            if (selectedColor != null) {
                alLeds.fadeRGB(ledGroupName, ledColorMap.get(selectedColor), 0f);
            }
        }
    }

    public void turnLedsOff() throws Exception {
        if(connection.checkConnection()) {
            alLeds.off(ledGroupName);
        }
    }
}

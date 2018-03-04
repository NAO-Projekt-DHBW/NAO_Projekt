package sample;

import com.aldebaran.qi.helper.proxies.ALBodyTemperature;

import java.util.ArrayList;

public class Temperature {

    private Connection connection;
    private ALBodyTemperature alBodyTemperature;

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klasse
    public Temperature(Connection connection) throws Exception{
        this.connection = connection;
        alBodyTemperature = new ALBodyTemperature(this.connection.getSession());
    }

    //Temperatur abrufen (periodischer Aufruf nach erfolgreich aufgebauter Verbindung mit NAO)
    public String getTemperature() throws Exception {
        String result;
        Object temp = alBodyTemperature.getTemperatureDiagnosis();
        //Prüfen üb zurückgeliefertes Objekt eine ArrayList ist (NAO liefert nicht immer dasselbe zurück)
        if (temp instanceof ArrayList) {
        ArrayList tempList = (ArrayList) temp;

        /*
        //ArrayList tempList = new ArrayList();
        //ArrayList test = new ArrayList();
        //test.add("LArm");
        //test.add("LLeg");
        //test.add("RArm");
        //test.add("RLeg");
        //tempList.add(0, 0);
        //tempList.add(0, test);
            */
        
        //Wert an Stelle 0 liefert Integer von 0-2 zurück
        //Wert an STelle 1 liefert eine weitere ArrayList mit den Strings der betroffenen Körperteile zurück
        //Beide werden kombiniert und zurückgegeben
        result = tempList.get(0).toString() + tempList.get(1).toString();
        } else {
            result = "";
        }
        return result;
    }
}

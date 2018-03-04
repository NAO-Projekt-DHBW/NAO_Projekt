package sample;

import com.aldebaran.qi.helper.proxies.ALBattery;

public class Battery {
    private Connection connection;
    private ALBattery alBattery;

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klasse
    public Battery(Connection connection) throws Exception{
        this.connection = connection;
        alBattery = new ALBattery(this.connection.getSession());
    }

    //Laden des Batteriestatus vom NAO und zurückgeben
    //Zur Sicherheit wird die Verbindung über das Connection-Objekt geprüft
    public int getBatteryState() throws Exception{
        int state = 0;
        if(connection.checkConnection()) {
            state = alBattery.getBatteryCharge();
            //System.out.println("Batteriestatus: " + state);
        }
        return state;
    }
}

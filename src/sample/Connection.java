package sample;

import com.aldebaran.qi.Session;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connection {

    //Anzahl der gespeicherten Verbindungen
    private static int maxLastConnections = 10;
    private static String fileLastConnection = System.getProperty("user.dir") + "/connection.txt";
    private Session session = new Session();

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klasse
    public Session startConnection(String robotUrl) throws Exception {
        try {
            session.connect(robotUrl).get();
        } catch (Exception ex) {
            //Anzeigen der Fehlermeldung in einem kleinen Popup-Fenster
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return session;
    }

    //Getter-Methode für die Session
    public Session getSession(){
            return session;
    }

    public void stopConnection() throws Exception{
        if(session.isConnected()) {
            session.close();
        }
    }

    public Boolean checkConnection() throws Exception{
        Boolean connected = false;
        if(session != null) {
            if (session.isConnected()) {
                connected = true;
            }
        }
        return connected;
    }

    //Laden der Datei und speichern jedes Strings vor einem Komma als Element einer Liste (diese wird zurückgegeben)
    public List<String> getLastConnectionsFromFile() throws Exception{
        List<String> list = new ArrayList<>();
        File file = new File(fileLastConnection);
        if (file.exists()) {
            //Zeile einlsen
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileLastConnection), "UTF-8"));
            String line = br.readLine();
            if (line != null) {
                //Jede Zeichenkette vor einem Komma wird als Element der List übergeben
                // Regulärer Ausdruck: alle Zeichen vor dem Komma
                list = Arrays.asList(line.split("\\s*,\\s*"));
            }
        }
        return list;
    }

    public void writeConnectionToFile(String robotUrl) throws Exception {
        String listString = "";
        //Speichern der vorhandenen Verbindungen mit obriger Methode in eine temporäre Liste
        List<String> oldList = getLastConnectionsFromFile();
        List<String> newList = new ArrayList<>();
        int size = oldList.size();
        //Wenn Limit erreicht: jedes Element der alten Liste außer dem ersten/ältesten Element wird hinzugefügt
        if(size >= maxLastConnections){
            for (int i=0; i < size-1; i++){
                //i+1 damit das erste Element ausgelassen wird und somit wiedr ein Platz frei ist
                newList.add(i, oldList.get(i+1));
            }
            //Hinzufügen des Elements an letzter Stelle
            newList.add(maxLastConnections-1 , robotUrl);
        } else {
            //Wenn noch Platz ist: Alle Elemente der alten Liste in die neue Schreiben und das neue Element hinzufügen
            for (String s : oldList)
            {
                newList.add(s);
            }
            newList.add(robotUrl);
        }
        //Alle Strings in der neuen Liste wird mit Komma hintenangestellt zu einem einzigen String "gewandelt"
        for (String s : newList)
        {
            listString += s + ",";
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLastConnection), "UTF-8"));
        //Schreiben des Strings in die Datei (die alte Datei wird überschrieben, da kein "append"-Mode aktiviert wurde)
        bw.write(listString);
        //Schließen des Writers, damit die Datei auch danach noch zugänglich ist.
        bw.close();
    }
}

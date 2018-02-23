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

    private static int maxLastConnections = 10;
    private static String fileLastConnection = "connection.txt";
    private Session session = new Session();

    public Session startConnection(String robotUrl) throws Exception {
        try {
            session.connect(robotUrl).get();
        } catch (Exception ex) {
            //Anzeigen der Fehlermeldung in einem kleinen Popup-Fenster
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return session;
    }

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

    public void writeConnectionToFile(String robotUrl) throws Exception {
        String listString = "";
        List<String> oldList = getLastConnectionsFromFile();
        List<String> newList = new ArrayList<>();
        int size = oldList.size();
        if(size >= maxLastConnections - 1){
            for (int i=0; i < size-1; i++){
                newList.add(i, oldList.get(i+1));
            }
            newList.add(maxLastConnections-1 , robotUrl);
        } else {
            for (String s : oldList)
            {
                newList.add(s);
            }
            newList.add(robotUrl);
        }
        for (String s : newList)
        {
            listString += s + ",";
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLastConnection), "UTF-8"));
        bw.write(listString);
        bw.close();
    }
}

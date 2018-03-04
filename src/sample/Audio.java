package sample;

import com.aldebaran.qi.helper.proxies.ALAudioPlayer;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import java.util.ArrayList;
import java.util.List;

public class Audio {
    private Connection connection;
    private ALTextToSpeech tts;
    private ALAudioPlayer audio;
    private Boolean audioFilesAvailable;

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klassen
    //Begrüßung wird ausgegeben!
    public Audio(Connection connection) throws Exception{
        this.connection = connection;
        tts = new ALTextToSpeech(this.connection.getSession());
        audio = new ALAudioPlayer(this.connection.getSession());
        if(this.getLanguage().equals("Deutsch")) {
            tts.say("Hallo!");
        }
        if(this.getLanguage().equals("Englisch")){
            tts.say("Hi!");
        }
    }

    //Sprachausgabe: Volume, Geschwindigkeit und Tonhöhe werden direkt im String mitgegeben
    //Zu Beachten: Genannte Werte werden zu Integers umgewandelt, weil der NAO bei englischer Sprachausgabe die Werte sonst vorliest!
    public void saySomething(String text, double volume, double speed, double pitch) throws Exception{
        tts.say("\\vol=" + (int) volume + "\\\\vct=" + (int) pitch + "\\\\rspd=" + (int) speed + "\\" + text);
    }

    //Andere Variante der Methode ohne abgeänderte Einstellungen (wird beim Verbindungsaufbau verwendet)
    public void saySomething(String text) throws Exception{
        tts.say(text);
    }

    //Zurückgeben der aktuell eingestellten Sprache des NAOs
    public String getLanguage() throws Exception{
        String language = tts.getLanguage();
        String result = "";
        if(language.equals("German")){
            result = "Deutsch";
        }
        if(language.equals("English")){
            result = "Englisch";
        }
        return result;
    }

    //Setzen der Sprache (Aufruf bei Ändern des Dropdown-Menüs für Sprachen)
    public void setLanguage(String selectedItem) throws Exception{
        if(connection.checkConnection()){
            if (selectedItem.equals("Deutsch")) {
                tts.setLanguage("German");
            } else if (selectedItem.equals("Englisch")){
                tts.setLanguage("English");
            }
        }
    }

    //Audio/Soundfiles vom NAO laden (wenn vorhanden) und als Liste mit Strings zurückgeben
    public List getAudioFiles() throws Exception{
        List<String> tempList = new ArrayList();
        if(audio.getMethodList().contains("getSoundSetFileNames")) {
            if(audio.getInstalledSoundSetsList().contains("Aldebaran")) {
                tempList = audio.getSoundSetFileNames("Aldebaran");
                audio.loadSoundSet("Aldebaran");
                audioFilesAvailable = true;
                //System.out.println("Es gibt Soundfiles.");
            }
        }else{
            audioFilesAvailable = false;
            //System.out.println("Es gibt keine Soundfiles.");
        }
        return tempList;
    }

    //Getter-Method efür "audioFilesAvailable"; wird beim Verusch soundfiles vom NAO zu laden gesetzt
    public Boolean getBoolAudioFilesAvailable() {
        return audioFilesAvailable;
    }

    //Soundfile abspielen
    //Volume muss von Double in Float gewandelt werden; außerdem sind nur Werte zwischen 0 und 4 erlaubt, deswegen wird durch 100 geteilt
    public void playSoundFile(String selectedItem, double volume) throws Exception{
        audio.playSoundSetFile("Aldebaran", selectedItem, 0f, (float) volume / 100, 0f, false);
    }
}

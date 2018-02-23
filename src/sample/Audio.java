package sample;

import com.aldebaran.qi.helper.proxies.ALAudioPlayer;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import java.util.ArrayList;
import java.util.List;

public class Audio {
    private Connection connection;
    private ALTextToSpeech tts;
    private ALAudioPlayer audio;

    public Audio(Connection connection) throws Exception{
        this.connection = connection;
        tts = new ALTextToSpeech(this.connection.getSession());
        audio = new ALAudioPlayer(this.connection.getSession());
        if(this.getLanguage() == "Deutsch") {
            tts.say("Hallo!");
        }
        if(this.getLanguage() == "Englisch"){
            tts.say("Hi!");
        }
    }
    public void saySomething(String text, double volume, double speed, double pitch) throws Exception{
        tts.say("\\vol=" + (int) volume + "\\\\vct=" + (int) pitch + "\\\\rspd=" + (int) speed + "\\" + text);
    }

    public void saySomething(String text) throws Exception{
        tts.say(text);
    }

    public String getLanguage() throws Exception{
        String language = tts.getLanguage().toString();
        String result = "";
        if(language.contains("German")){
            result = "Deutsch";
        }
        if(language.contains("English")){
            result = "Englisch";
        }
        return result;
    }

    public void setLanguage(String selectedItem) throws Exception{
        if(connection.checkConnection()){
            if (selectedItem == "Deutsch") {
                tts.setLanguage("German");
            } else if (selectedItem == "Englisch"){
                tts.setLanguage("English");
            }
        }
    }

    public List getAudioFiles() throws Exception{
        List<String> tempList = new ArrayList();
        if(audio.getMethodList().contains("getSoundSetFileNames")) {
            if(audio.getInstalledSoundSetsList().contains("Aldebaran")) {
                tempList = audio.getSoundSetFileNames("Aldebaran");
                audio.loadSoundSet("Aldebaran");
                System.out.println("Es gibt Soundfiles.");
            }
        }else{
            System.out.println("Es gibt keine Soundfiles.");
        }
        return tempList;
    }

    public void playSoundFile(String selectedItem, double volume) throws Exception{
        audio.playSoundSetFile("Aldebaran", selectedItem, 0f, (float) volume / 100, 0f, false);
    }
}

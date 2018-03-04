package sample;

import javafx.fxml.FXMLLoader;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.scene.Parent;

public class Reactor {

    private Connection connection;
    private ALMemory alMemory;
    private Audio audio;
    private Controller controller;

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klasse
    //Zusätzlich wird das Objekt der Hilfsklasse "Audio" vom Controller übermittelt und der Controller wird nochmals geladen (notwendig für Aktionen bei bestimmen Events
    public Reactor(Connection connection, Audio audio) throws Exception{
        this.connection = connection;
        alMemory = new ALMemory(this.connection.getSession());
        this.audio = audio;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        controller = loader.getController();
    }

    //Variablen zum Abspeichern der ID's beim Subscriben von Events
    private long frontTactilSubscriptionId, rearTactilSubscriptionId, middleTactilSubscriptionId, postureSubscriptionId, batteryChargeSubscriptionId, batteryPluggedSubscriptionId,
            temperatureChangedSubscriptionId, temperatureStatusSubscriptionId, temperatureDiagnosisSubscriptionId;

    public void subscribe() throws Exception {
        frontTactilSubscriptionId = 0;
        rearTactilSubscriptionId = 0;
        middleTactilSubscriptionId = 0;
        postureSubscriptionId = 0;
        batteryChargeSubscriptionId = 0;
        batteryPluggedSubscriptionId = 0;
        temperatureChangedSubscriptionId = 0;
        temperatureStatusSubscriptionId = 0;
        temperatureDiagnosisSubscriptionId = 0;

        //Anmelden am Event "FrontTactilTouched" (vorderer Touch-Sensor am Kopf des NAO),
        // erstellen eines EventCallback, welches Daten vom Event erwartet (je nach Event ist das bspw. ein Float oder ein String).
        frontTactilSubscriptionId = alMemory.subscribeToEvent(
                "FrontTactilTouched", new EventCallback<Float>() {
                    @Override
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        // Der Sensor wurde berührt.
                        if (arg0 > 0) {
                            try {
                                //System.out.println("FrontTactilTouched: " + arg0);
                                audio.saySomething("Nein!");
                            } catch(Exception ex){}
                        }
                    }
                });

        //Weitere Subscriptions für die anderen touch-Sensoren am Kopf nach dem gleichen Schema
        rearTactilSubscriptionId = alMemory.subscribeToEvent("RearTactilTouched",
                new EventCallback<Float>() {
                    @Override
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        if (arg0 > 0) {
                            try {
                                //System.out.println("RearTactilTouched: " + arg0);
                                audio.saySomething("Aua!");
                            } catch(Exception ex){}
                        }
                    }
                });

        middleTactilSubscriptionId = alMemory.subscribeToEvent("MiddleTactilTouched",
                new EventCallback<Float>() {
                    @Override
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        if (arg0 > 0) {
                            try {
                                //System.out.println("MiddleTactilTouched: " + arg0);
                                audio.saySomething("Ouch!");
                            } catch(Exception ex){}
                        }
                    }
                });

        //Änderung des Akkuladestands
        batteryChargeSubscriptionId = alMemory.subscribeToEvent("BatteryChargeChanged",
                new EventCallback<Integer>() {
                    @Override
                    public void onEvent(Integer arg0)
                            throws InterruptedException, CallError {
                        if (arg0 >= 0) {
                            //Aufruf einer Methode aus dem Controller
                            controller.showBatteryState(arg0);
                            //System.out.println("BatteryChargeChanged: " + arg0);
                        }
                    }
                });

        //Änderung des Ladestatus der Batterie
        //Das Event wird geworfen, wenn der NAO geladen wird
        batteryPluggedSubscriptionId = alMemory.subscribeToEvent("BatteryPowerPluggedChanged",
                new EventCallback<Boolean>() {
                    @Override
                    public void onEvent(Boolean arg0)
                            throws InterruptedException, CallError {
                        if(arg0.booleanValue()) {
                            if(arg0.booleanValue()){
                                try {
                                    audio.saySomething("Charging!");
                                } catch(Exception ex) {}
                            }
                            //Aufrufen einer Methode aus dem Controller
                            controller.showBatteryCharging(arg0);
                            //System.out.println("BatteryPowerPlugged: " + arg0);
                        }
                    }
                });


            /*
            //Änderung des Temperaturstatus
            temperatureStatusSubscriptionId = alMemory.subscribeToEvent("TemperatureStatusChanged",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureStatusChanged: " + arg0);
                            }
                        }
                    });

            //Temperaturstatus
            temperatureStatusSubscriptionId = alMemory.subscribeToEvent("TemperatureStatus",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureStatus: " + arg0);
                            }
                        }
                    });

            //TemperatureDiagnosisErrorChanged
            temperatureDiagnosisSubscriptionId = alMemory.subscribeToEvent("TemperatureDiagnosisErrorChanged",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float arg0)
                                throws InterruptedException, CallError {
                            if (arg0 > 0) {
                                System.out.println("TemperatureDiagnosisErrorChanged: " + arg0);
                            }
                        }
                    });
            */

        //Änderung der Haltung (wird nur für Tests verwendet)
        postureSubscriptionId = alMemory.subscribeToEvent (
                "PostureChanged", new EventCallback<String>() {
                    @Override
                    public void onEvent(String arg0)
                            throws InterruptedException, CallError {
                        if (arg0 != null) {
                                //System.out.println(arg0);
                        }
                    }
                });
    }

    //"Unsubscriben" aller Events; Für das jeweilige Event wird die ID verwendet, die beim Subscriben zurückgegeben wird.
    //Unsubsriben ist notwendig, da der NAO nach ca. 8 maligen erneutem Subscriben (ohne Unsubscriben) ansonsten neugestartet werden muss.
    public void unsubscribe() throws Exception {
        if(frontTactilSubscriptionId > 0) alMemory.unsubscribeToEvent(frontTactilSubscriptionId);
        if(rearTactilSubscriptionId > 0) alMemory.unsubscribeToEvent(rearTactilSubscriptionId);
        if(middleTactilSubscriptionId > 0) alMemory.unsubscribeToEvent(middleTactilSubscriptionId);
        if(batteryChargeSubscriptionId > 0) alMemory.unsubscribeToEvent(batteryChargeSubscriptionId);
        if(batteryPluggedSubscriptionId > 0) alMemory.unsubscribeToEvent(batteryPluggedSubscriptionId);
        if(temperatureStatusSubscriptionId > 0) alMemory.unsubscribeToEvent(temperatureStatusSubscriptionId);
        if(temperatureDiagnosisSubscriptionId > 0) alMemory.unsubscribeToEvent(temperatureDiagnosisSubscriptionId);
        if(temperatureChangedSubscriptionId > 0) alMemory.unsubscribeToEvent(temperatureChangedSubscriptionId);
        if(postureSubscriptionId > 0) alMemory.unsubscribeToEvent(postureSubscriptionId);
    }

}

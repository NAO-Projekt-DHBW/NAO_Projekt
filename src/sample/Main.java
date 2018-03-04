package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image ;

import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {

    //Variable und Methode um Icon zu laden: Wird das Icon direkt in der "start"-Methode geladen funktioniert es nur sehr sporadisch
    //Das liegt anscheinend daran, dass die Stage schon fertig geladen ist und das Laden des Icons ist zu langsam. Deswegen wird das Icon davor in einer extra Methode geladen und später nur übergeben
    //Trotzdem funktioniert es nicht immer...
    private Image icon;

    public Main() {
        icon = new Image(Main.class.getResource("nao_icon.jpg").toExternalForm());
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //Übergeben des Icons, damit in der "Taskleiste" kein Bild von einem Fragezeichen ist.
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("NAO Dashboard");
        primaryStage.setScene(new Scene(root, 1420, 850));

        //setMaximized nutzt die volle Breite und Höhe des Bildschirm im Fenstermodus (bei setFullScreen gibt es kein Fenster).
        primaryStage.setMaximized(true);

        //CSS-Datei verknüpfen
        primaryStage.getScene().getStylesheets().add("sample/style.css");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image ;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("NAO Dashboard");
        primaryStage.setScene(new Scene(root, 1420, 850));
        //setMaximized nutzt die volle Breite und Höhe des Bildschirm im Fenstermodus (bei setFullScreen gibt es kein Fenster).
        primaryStage.setMaximized(true);
        //Laden eines Icons, damit in der "Taskleiste" kein Bild von einem Fragezeichen ist.
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("nao_icon.jpg")));
        //CSS-Datei verknüpfen
        primaryStage.getScene().getStylesheets().add("sample/style.css");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

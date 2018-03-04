
package sample;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import javafx.scene.input.KeyEvent;


public class Move {
    private Connection connection;
    private ALMotion alMotion;
    private ALRobotPosture alRobotPosture;

    //Eiegener Konstruktor; übernehmen des Connection-Objekts und Instanzieren der NAO-Klassen
    public Move(Connection connection) throws Exception{
        this.connection = connection;
        alMotion = new ALMotion(this.connection.getSession());
        alRobotPosture = new ALRobotPosture(this.connection.getSession());
    }

    //NAO aufwecken
    public void wakeUp() throws Exception {
        if(connection.checkConnection()){
            alMotion.wakeUp();
        }
    }

    //NAO auf Standby setzen
    public void rest() throws Exception {
        if(connection.checkConnection()) {
            alMotion.rest();
        }
    }


    public void lookUpOrDown(double dUpDown, double dPace) throws Exception{
        if(connection.checkConnection()){
            alMotion.angleInterpolationWithSpeed("HeadPitch", -(float) dUpDown, (float) dPace);
        }
    }

    public void lookLeftOrRight(double dLeftRight, double dpace) throws Exception{
        if(connection.checkConnection()){
            alMotion.angleInterpolationWithSpeed("HeadYaw", -(float) dLeftRight, (float) dpace);
        }
    }

    //Wird durch Benutzung der Buttons zur Kopfbewegung aufgerufen
    public void look(String direction, double dpace) throws Exception{
        String naoDirection = "HeadYaw";
        float angle = 1.0f;
        float pace = (float) dpace;

        //Je nachdem was der Controller lieftert wird der entsprechende Motor ausgewählt.
        if(direction.equals("up") || direction.equals("down")) naoDirection = "HeadPitch";

        //Wenn erforderlich wird der Wert negiert
        if(direction.equals("up") || direction.equals("right")) angle = -angle;
        if(connection.checkConnection()) alMotion.angleInterpolationWithSpeed(naoDirection, angle, pace);
    }

    public void resetHead(double dpace) throws Exception{
        if(connection.checkConnection()) {
            alMotion.angleInterpolationWithSpeed("HeadPitch", 0f, (float) dpace);
            alMotion.angleInterpolationWithSpeed("HeadYaw", 0f, (float) dpace);
        }
    }

    public void moveTowards(String keyCode, double dpace) throws Exception{
        float pace = (float) dpace;
        float x = 0f;
        float y = 0f;
        float theta = 0f;
        switch(keyCode){
            case "W":
                x = pace;
                break;
            case "S":
                x = -pace;
                break;
            case "A":
                y = pace;
                break;
            case "D":
                y = -pace;
                break;
            case "Q":
                theta = pace;
                break;
            case "E":
                theta = -pace;
                break;
        }
        if(connection.checkConnection()){
            alMotion.moveToward(x, y, theta);
        }
    }

    public void stopMoving() throws Exception{
        if(connection.checkConnection()){
            if(alMotion.moveIsActive()){
                alMotion.stopMove();
            }
        }
    }

    public void moveOnKeyPressed(String keyEvent, double dPace) throws Exception{
        float pace = (float) dPace;
        if(connection.checkConnection()) {
            switch (keyEvent) {
                case "W":
                    alMotion.moveToward(pace, 0f, 0f);
                    break;
                case "A":
                    alMotion.moveToward(0f, pace, 0f);
                    break;
                case "S":
                    alMotion.moveToward(-pace, 0f, 0f);
                    break;
                case "D":
                    alMotion.moveToward(0f, -pace, 0f);
                    break;
                case "Q":
                    alMotion.moveToward(0f, 0f, pace);
                    break;
                case "E":
                    alMotion.moveToward(0f, 0f, -pace);
                    break;
            }
        }
    }

    //Stoppen der Bewegung bei Loslassen der Tasten, mit denen der NAO gesteuert werden kann
    public void stopOnKeyReleased(KeyEvent keyEvent) throws Exception{
        if(connection.checkConnection()) {
            switch (keyEvent.getCode()) {
                case W:
                    alMotion.stopMove();
                    break;
                case A:
                    alMotion.stopMove();
                    break;
                case S:
                    alMotion.stopMove();
                    break;
                case D:
                    alMotion.stopMove();
                    break;
                case Q:
                    alMotion.stopMove();
                    break;
                case E:
                    alMotion.stopMove();
                    break;
            }
        }
    }

    //Alle Möglichen Positionen, die der NAO einnehmen kann.
    //Der Float-Wert für die Geschwindigkeit wurde auf 1 fixiert, da der NAO bei niedrigeren Werten oft hinfältt!
    //Der Nao-spezifische Name der Position wird schon vom Controller als Parameter übermittelt, sodass er hier direkt weitergegeben werden kann.
    public void changePosture(String postureName) throws Exception{
        if(connection.checkConnection()){
            alRobotPosture.goToPosture(postureName, 1f);
        }
    }
}

package sample;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import javafx.scene.input.KeyEvent;


public class Move {
    private Connection connection;
    private ALMotion alMotion;
    private ALRobotPosture alRobotPosture;

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
    /*
    //Kopf des NAO drehen und neigen
    public void lookUpOrDown(Connection connection) throws Exception{
        if(connection.checkConnection()){
            alMotion.angleInterpolationWithSpeed("HeadPitch", -(float) sliderHeadUpDown.getValue(), (float) sliderPace.getValue());
        }
    }

    public void lookLeftOrRight(Connection connection) throws Exception{
        if(connection.checkConnection()){
            alMotion.angleInterpolationWithSpeed("HeadYaw", -(float) sliderHeadLeftRight.getValue(), (float) sliderPace.getValue());
        }
    }

    public void lookReset(Connection connection) throws Exception {
        if(connection.checkConnection()) {
            alMotion.angleInterpolationWithSpeed("HeadPitch", 0f, (float) sliderPace.getValue());
            alMotion.angleInterpolationWithSpeed("HeadYaw", 0f, (float) sliderPace.getValue());
        }
        sliderHeadLeftRight.setValue(0);
        sliderHeadUpDown.setValue(0);
    }

    //NAO solange bewegen lassen, bis die Maus losgelassen wird:
    //Jede Bewegung ist an den entsprechenden Button im Scene Builder verknüpft (unter "Mouse" > "On Mouse Pressed".
    //Alle Buttons haben zusätzlich die Methode "stopMove" (unter "Mouse" > "On Mouse Released" verknüpft.
    //Den Methoden muss das MouseEvent (und nicht ActionEvent) übergeben werden, sonst wird sie in der Laufzeit mit einem Fehler abgebrochen.
    public void moveForward(Connection connection) throws Exception {
        if(connection.checkConnection()) {
            alMotion.moveToward((float) sliderPace.getValue(), 0f, 0f);
        }
    }

    public void moveBackwards(Connection connection) throws Exception {
        if(connection.checkConnection()){
            alMotion.moveToward(-(float) sliderPace.getValue(), 0f ,0f);
        }
    }

    public void moveLeft(Connection connection) throws Exception {
        if(connection.checkConnection()){
            alMotion.moveToward(0f, (float) sliderPace.getValue(), 0f);
        }
    }

    public void moveRight(Connection connection) throws Exception {
        if(connection.checkConnection()){
            alMotion.moveToward(0f, -(float) sliderPace.getValue(), 0f);
        }
    }

    public void turnRight(Connection connection) throws Exception {
        if(connection.checkConnection()){
            alMotion.moveToward(0f, 0f, -(float) sliderPace.getValue());
        }
    }

    public void turnLeft(Connection connection) throws Exception {
        if(connection.checkConnection()){
            alMotion.moveToward(0f, 0f, (float) sliderPace.getValue());
        }
    }

    public void stopMove(Connection connection) throws Exception {
        if(connection.checkConnection() && alMotion.moveIsActive()) {
            alMotion.stopMove();
        }
    }

    //NAO durch Tastatur steuern:
    //Methode "getKeyPressed" ist mit "On Key Pressed" im Scene Builder verknüpft.
    //Wird eine entsprechende Taste erkannt wird der NAO entsprechend bewegt.
    public void getKeyPressed(Connection connection, javafx.scene.input.KeyEvent keyEvent, float pace) throws Exception {
        if(connection.checkConnection()) {
            switch (keyEvent.getCode()) {
                case W:
                    alMotion.moveToward(pace, 0f, 0f);
                    break;
                case A:
                    alMotion.moveToward(0f, pace, 0f);
                    break;
                case S:
                    alMotion.moveToward(-pace, 0f, 0f);
                    break;
                case D:
                    alMotion.moveToward(0f, -pace, 0f);
                    break;
                case Q:
                    alMotion.moveToward(0f, 0f, pace);
                    break;
                case E:
                    alMotion.moveToward(0f, 0f, -pace);
                    break;
            }
        }
    }
  */
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
/*
    //Methode "getKeyReleased" ist mit "On Key Released" im Scene Builder verknüpft.
    //Wenn eine der Tasten WASDQE losgelassen wird, wird die Bewegung des NAOs gestoppt.
    //Ansonsten würde der NAO unendlich lange laufen, auch wenn die Taste losgelassen wird.
    public void getKeyReleased(Connection connection, javafx.scene.input.KeyEvent keyEvent) throws Exception {
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
*/
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
    //Der zweite Wert (float) gibt die Geschwindigkeit an.
    //Sitzen (normal)
    public void changePosture(String postureName, float pace) throws Exception{
        if(connection.checkConnection()){
            alRobotPosture.goToPosture(postureName, pace);
        }
    }
}
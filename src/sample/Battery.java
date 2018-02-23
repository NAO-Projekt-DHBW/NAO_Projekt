package sample;

import com.aldebaran.qi.helper.proxies.ALBattery;

public class Battery {
    private Connection connection;
    private ALBattery alBattery;

    public Battery(Connection connection) throws Exception{
        this.connection = connection;
        alBattery = new ALBattery(this.connection.getSession());
    }

    public int getBatteryState() throws Exception{
        int state = 0;
        if(connection.checkConnection()) {
            state = alBattery.getBatteryCharge();
            System.out.println("Batteriestatus: " + state);
        }
        return state;
    }
}

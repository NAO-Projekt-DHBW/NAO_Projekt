package sample;

import com.aldebaran.qi.helper.proxies.ALBodyTemperature;

import java.util.ArrayList;

public class Temperature {
    private ALBodyTemperature alBodyTemperature;

    public Temperature(Connection connection) throws Exception{
        alBodyTemperature = new ALBodyTemperature(connection.getSession());
    }

    public String getTemperature() throws Exception {
        String result;
        Object temp = alBodyTemperature.getTemperatureDiagnosis();
        //if (temp instanceof ArrayList) {
        //ArrayList tempList = (ArrayList) temp;
        ArrayList tempList = new ArrayList();
        ArrayList test = new ArrayList();
        //test.add("LArm");
        test.add("LLeg");
        test.add("RArm");
        test.add("RLeg");
        tempList.add(0, 0);
        tempList.add(0, test);
        result = tempList.get(0).toString() + tempList.get(1).toString();
        System.out.println(tempList);
        //} else {
        //    result = "";
        //}
        return result;
    }
}

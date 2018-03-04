package sample;

import javafx.beans.binding.BooleanBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    //Überprüfen der IP-Adresse mit RegEx
    public static Boolean validateIp(String ip){
        Pattern pattern;
        Matcher matcher;
        String IPADDRESS_PATTERN
                = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    //Überprüfen des Ports mit RegEx
    public static Boolean validatePort(String port){
        if(!port.matches("\\d*")){
            return false;
        }else{
            return true;
        }
    }
}

package be.isservers.hmb.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class GeneratePassword {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePassword.class);
    private static ArrayList<AccessObject> listPassword = new ArrayList<>();
    private static ArrayList<AccessObject> listToken = new ArrayList<>();

    private final static long TIME_VALIDITY_PASSWORD = 2;//en minutes
    private final static long TIME_VALIDITY_TOKEN = 25;//en minutes

    private static String getGenericValue(String type,ArrayList<AccessObject> list){
        AccessObject pass = new AccessObject();
        pass.date = Calendar.getInstance();
        pass.value = generateRandomValue();
        LOGGER.info(type + " request: " + pass.value);
        list.add(pass);
        return pass.value;
    }

    public static String getPassword(){
        return getGenericValue("Password",listPassword);
    }

    public static String getToken(){
        return getGenericValue("Token", listToken);
    }

    public static boolean checkPassword(String password){
        for (AccessObject obj : listPassword) {
            if(obj.value.equals(password)){
                return (Calendar.getInstance().getTimeInMillis() - obj.date.getTimeInMillis())/1000 < (TIME_VALIDITY_PASSWORD*60);
            }
        }
        return false;
    }

    public static boolean checkToken(String password){
        for (AccessObject obj : listToken) {
            if(obj.value.equals(password)){
                return (Calendar.getInstance().getTimeInMillis() - obj.date.getTimeInMillis())/1000 < (TIME_VALIDITY_TOKEN*60);
            }
        }
        return false;
    }

    private static String generateRandomValue(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static boolean removeUsedPassword(String password){
        for (AccessObject obj : listPassword) {
            if(obj.value.equals(password)){
                return listPassword.remove(obj);
            }
        }
        return false;
    }
}

class AccessObject {
    public String value;
    public Calendar date;
}

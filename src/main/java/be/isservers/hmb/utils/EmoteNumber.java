package be.isservers.hmb.utils;

public class EmoteNumber {
    public static String get(int number){
        switch (number) {
            case 1: return getOne();
            case 2: return getTwo();
            case 3: return getThree();
            case 4: return getFour();
            case 5: return getFive();
            case 6: return getSix();
            case 7: return getSeven();
            case 8: return getEight();
            case 9: return getNine();
            case 10: return getTen();
            default: return Integer.toString(number);
        }
    }

    public static String getOne(){
        return ":one:";
    }

    public static String getTwo(){
        return ":two:";
    }

    public static String getThree(){
        return ":three:";
    }

    public static String getFour(){
        return ":four:";
    }

    public static String getFive(){
        return ":five:";
    }

    public static String getSix(){
        return ":six:";
    }

    public static String getSeven(){
        return ":seven:";
    }

    public static String getEight(){
        return ":eight:";
    }

    public static String getNine(){
        return ":nine:";
    }

    public static String getTen() {
        return ":keycap_ten:";
    }
}

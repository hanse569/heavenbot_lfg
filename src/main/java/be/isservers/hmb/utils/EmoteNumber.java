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

    private static String getOne(){
        return ":one:";
    }

    private static String getTwo(){
        return ":two:";
    }

    private static String getThree(){
        return ":three:";
    }

    private static String getFour(){
        return ":four:";
    }

    private static String getFive(){
        return ":five:";
    }

    private static String getSix(){
        return ":six:";
    }

    private static String getSeven(){
        return ":seven:";
    }

    private static String getEight(){
        return ":eight:";
    }

    private static String getNine(){
        return ":nine:";
    }

    private static String getTen() {
        return ":keycap_ten:";
    }
}

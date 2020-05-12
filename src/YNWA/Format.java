package YNWA;

public class Format {
    public static String formatSpeedString(double speed) {
        return "     Speed: "+ ((int) (speed * 50)+"%");
    }
    public static int formatSpeedInt(double speed) {
        return ((int) (speed * 50));
    }
}

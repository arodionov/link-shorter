package shorter.util;

public class LinkUtil {

    public static void check(String link) {
    }

    public static String getPath(String link) {
        return link.substring(link.indexOf("//") + 2);
    }

}

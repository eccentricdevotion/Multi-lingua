package me.eccentric_nz.plugins.multilingua;

import org.bukkit.ChatColor;

public class MultilinguaConstants {

    public static final String MY_PLUGIN_NAME = ChatColor.GOLD + "[Multi-lingua] " + ChatColor.RESET;
    public static final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,:;!?()0123456789 []@$%&".toCharArray();
    public static final String cipher = "MNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,:;!?()0123456789 []@$%&ABCDEFGHIJKL";

    public static String shuffle(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        double rnd;
        for (char c : string.toCharArray()) {
            rnd = Math.random();
            if (rnd < 0.34) {
                sb.append(c);
            } else if (rnd < 0.67) {
                sb.insert(sb.length() / 2, c);
            } else {
                sb.insert(0, c);
            }
        }
        return sb.toString();
    }
}

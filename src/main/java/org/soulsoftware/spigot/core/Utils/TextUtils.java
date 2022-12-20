package org.soulsoftware.spigot.core.Utils;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class TextUtils {
    private static final Map<Character, Integer> rawRoman = new HashMap<>();
    private static final Map<ChatColor, String> replacements = new EnumMap(ChatColor.class);
    static {
        rawRoman.put('I',1);
        rawRoman.put('V',5);
        rawRoman.put('X',10);
        rawRoman.put('L',50);
        rawRoman.put('C',100);
        rawRoman.put('D',500);
        rawRoman.put('M',1000);
    }

    public static String removeColor(String str) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',str));
    }
    private static String rgbGradient(String str, Color from, Color to, Interpolator interpolator) {
        try {
            String methodName = net.md_5.bungee.api.ChatColor.class.getDeclaredMethod("of", Color.class).getName();
        } catch (NoSuchMethodException e) {
            return str;
        }

        // interpolate each component separately
        final double[] red = interpolator.interpolate(from.getRed(), to.getRed(), str.length());
        final double[] green = interpolator.interpolate(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = interpolator.interpolate(from.getBlue(), to.getBlue(), str.length());

        final StringBuilder builder = new StringBuilder();

        // create a string that matches the input-string but has
        // the different color applied to each char
        for (int i = 0; i < str.length(); i++) {
            builder.append(net.md_5.bungee.api.ChatColor.of(new Color(
                            (int) Math.round(red[i]),
                            (int) Math.round(green[i]),
                            (int) Math.round(blue[i]))))
                    .append(str.charAt(i));
        }

        return builder.toString();
    }
    /**
     * Converts a hex string to a color. If it can't be converted null is returned.
     * @param hex (i.e. #CCCCCCFF or CCCCCC)
     * @return Color
     */
    public static Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16));
        }
        return null;
    }
    /**
     * @param mode T - Quadratic increasing F - Linear
     */
    public static String gradient(String input, Boolean mode, double @Nullable [] portions, Color... colors) {
        Interpolator interpolator;
        if(mode) interpolator = new Interpolator.Quadratic();
        else interpolator = new Interpolator.Linear();

        final double[] p;
        if (portions == null) {
            p = new double[colors.length - 1];
            Arrays.fill(p, 1 / (double) p.length);
        } else {
            p = portions;
        }

        Preconditions.checkArgument(colors.length >= 2);
        Preconditions.checkArgument(p.length == colors.length - 1);

        final StringBuilder builder = new StringBuilder();
        int strIndex = 0;


        for (int i = 0; i < colors.length - 1; i++) {
            builder.append(rgbGradient(
                    input.substring(strIndex, strIndex + (int) (p[i] * input.length())),
                    colors[i],
                    colors[i + 1],
                    interpolator));
            strIndex += p[i] * input.length();
        }
        return builder.toString();
    }
    public static String toAnsiString(String str) {
        String result = convertRGBColors(str);
        ChatColor[] var6;
        int var5 = (var6 = ChatColor.values()).length;

        for(int var4 = 0; var4 < var5; ++var4) {
            ChatColor color = var6[var4];
            if (replacements.containsKey(color)) {
                result = result.replaceAll("(?i)" + color.toString(), (String)replacements.get(color));
            } else {
                result = result.replaceAll("(?i)" + color.toString(), "");
            }
        }

        return result + Ansi.ansi().reset().toString();
    }
    public static String convertRGBColors(String input) {
        Matcher matcher = Pattern.compile('§' + "x(" + '§' + "[A-F0-9]){6}", 2).matcher(input);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            String s = matcher.group().replace("§", "").replace('x', '#');
            java.awt.Color color = java.awt.Color.decode(s);
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            String replacement = String.format('\u001b' + "[38;2;%d;%d;%dm", red, green, blue);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
    public static String colorize(String text) {
        if(text==null) return null;
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(text));
    }
    public static String translateHexColorCodes(String message) {
        if(message==null) return null;
        String startTag = "#";
        String endTag = "";
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
    public static String integerToRoman(Integer num) {

        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder roman = new StringBuilder();

        for(int i=0;i<values.length;i++) {
            while(num >= values[i]) {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }
    public static Integer romanToInteger(String roman) {
        roman = roman.replace("IV","IIII").replace("IX","VIIII").replace("XL","XXXX").
                replace("XC","LXXXX").replace("CD","CCCC").replace("CM","DCCCC");
        Integer total = 0;
        for (int i = 0; i < roman.length(); i++) total += rawRoman.get(roman.charAt(i));
        return total;
    }
}

package me.mrfunny.elementalclient.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlphanumericalComparator implements Comparator<String> {
    private static final Pattern p = Pattern.compile("^\\d+");
    @Override
    public int compare(String object1, String object2) {
        Matcher m = p.matcher(object1);
        Integer number1;
        if(!m.find()) {
            return object1.compareTo(object2);
        } else {
            int number2;
            number1 = Integer.parseInt(m.group());
            m = p.matcher(object2);
            if(!m.find()) {
                return object1.compareTo(object2);
            } else {
                number2 = Integer.parseInt(m.group());
                int comparison = number1.compareTo(number2);
                if(comparison != 0) {
                    return comparison;
                } else {
                    return object1.compareTo(object2);
                }
            }
        }
    }
}

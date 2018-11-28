package me.hekr.sthome.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ryanhsueh on 2018/11/28
 */
public class PasswordPattern {

    // Password (UpperCase, LowerCase, Number/SpecialChar and min 10 Chars)
    private static final String REGEX = "(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
    private static final Pattern MY_PATTERN = Pattern.compile(REGEX);

    public static boolean matchs(String pwd) {
        Matcher matcher = MY_PATTERN.matcher(pwd);
        return matcher.matches();
    }

}

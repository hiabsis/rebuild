package com.translate.rebuild.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ：cyw
 * @version : 1.0
 * @description:
 * @date : 2021/2/25:16:50
 */
public class WordUtil {
    /**
     * 去除字符串中的数字
     */
    public static  String removeNumberFormStr(String word){
        char[] wordsChar = word.toCharArray();
        String result = "";
        for (char c:wordsChar){
            if ((int) c<48||c>57){

                result += c;
            }
        }
        return result;
    }

    /**
     * 检查首字母为英文
     */

    public static boolean isLetterFistChar(String word){
        if (StringUtils.isEmpty(word)){
            return false;
        }
        if ((int) word.charAt(0)>= 97 && (int) word.charAt(0)<=122){
            return  true;
        }
        return false;
    }

    /**
     * 判断是否包括汉字
     * @param word
     * @return
     */
    public static boolean hasChineseChar(String word)
    {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(word);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 移除字段中多余的部分
     */
    public static String deleteWorldAdditional(String word){
        if (word == null || word.equals("")){
            return  "";
        }
        String[] values = word.split(" ");
        if (values.length == 1) {
            return word;
        }else {
            if (values[0].length() >3){
                return values[0];
            }else {
                return word;
            }
        }
    }

    /**
     * 替代单词中的特殊字符
     */
    public static String updateWorldSpecialCharacters(String word){
        if (word == null ) return  "";
        word = word.replace(","," ");
        word = word.replace("("," ");
        word = word.replace(")"," ");
        word = word.replace("2"," ");
        word = word.replace("3"," ");
        word = word.replace("4"," ");
        word = word.replace("5"," ");
        word = word.replace("6"," ");
        word = word.replace("7"," ");
        word = word.replace("8"," ");
        word = word.replace("9"," ");
        word = word.replace("1"," ");
        word = word.replace("'\'"," ");
        word = word.replace("/"," ");
        word = word.replace("."," ");
        word = word.replace(":"," ");
        word = word.replace("\'\'"," ");

        return word;
    }




}

package com.translate.rebuild.utils;

import javax.naming.Name;
import java.io.File;

/**
 * @program: rebuild
 * @description: 记录一些文件常量
 * @author: cyw
 * @create: 2021-04-01 23:06
 **/
public class FileConstant {
    public static String RELATIVE_PATH = "src/main/java/com/translate/rebuild/xslx/";
    private static String SEPARATOR = File.separator;
    public static String getFileRelativePath(String fileName){
        String filePath = RELATIVE_PATH+fileName;
        return filePath;
    }
    private static  String  getSystemPath(String path){
        if (path == null) return null;
        String[] values = path.split("/");
        path = "";
        for (int i=0;i<values.length;i++){
            path += values[i] + SEPARATOR;
        }
        path += values[values.length-1];
        return  path;
    }

   public void setRelativePath(String path){
        RELATIVE_PATH = path;
   }

}

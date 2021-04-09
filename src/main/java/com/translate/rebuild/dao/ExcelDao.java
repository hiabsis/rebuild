package com.translate.rebuild.dao;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.translate.rebuild.config.ExcelFillCellMergeStrategy;
import com.translate.rebuild.config.ExcelListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 *  提供对本地表格数据的读写
 */
@Slf4j
public final class ExcelDao {

    /**
     * 从excel中去取数据
     * @return
     */
    public static <T extends BaseRowModel> List<T> readDataFormLocal(String filePath,final Class<? extends BaseRowModel> clazz){
        List<T> results = null;
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            results = readExcel(new BufferedInputStream(inputStream), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 从Excel中读取文件，读取的文件是一个DTO类，该类必须继承BaseRowModel
     * 具体实例参考 ： MemberMarketDto.java
     * 参考：https://github.com/alibaba+easyexcel
     * 字符流必须支持标记，FileInputStream 不支持标记,可以使用BufferedInputStream 代替
     * BufferedInputStream bis = new BufferedInputStream(new FileInputStream(...));
     */
    private static <T extends BaseRowModel> List<T> readExcel(final InputStream inputStream, final Class<? extends BaseRowModel> clazz) {
        if (null == inputStream) {
            throw new NullPointerException("the inputStream is null!");
        }
        ExcelListener<T> listener = new ExcelListener<>();
        // 这里因为EasyExcel-1.1.1版本的bug，所以需要选用下面这个标记已经过期的版本
        ExcelReader reader = new ExcelReader(inputStream,  valueOf(inputStream), null, listener);
        reader.read(new com.alibaba.excel.metadata.Sheet(1, 1, clazz));

        return listener.getRows();
    }


    /**
     *  将数据保存到本地并合并单元格
     * @param values 数据
     * @param filePath 保存的文件地址
     * @param mergeColumeIndex  合并哪几列
     * @param mergeRowIndex 从哪一行开始
     */
    public static <T extends BaseRowModel>  void saveDateToLocal(List<T> values, String filePath,int[] mergeColumeIndex,int mergeRowIndex,final Class<? extends BaseRowModel> clazz){
        EasyExcel.write(filePath,clazz).sheet("sheet")
                .registerWriteHandler(new ExcelFillCellMergeStrategy(mergeRowIndex,mergeColumeIndex))
                .doWrite(values);
    }

    /**
     *  将数据保存到本地
     * @param values 数据
     * @param filePath 保存的文件地址
     */
    public static  <T extends BaseRowModel>  void saveDateToLocal(List<T> values, String filePath,final Class<? extends BaseRowModel> clazz){
        EasyExcel.write(filePath,clazz).sheet("sheet")
                .doWrite(values);
    }


    /**
     * 根据输入流，判断为xls还是xlsx
     */
    private static ExcelTypeEnum valueOf(InputStream inputStream) {
        try {
            FileMagic fileMagic = FileMagic.valueOf(inputStream);
            if (FileMagic.OLE2.equals(fileMagic)) {
                return ExcelTypeEnum.XLS;
            }
            if (FileMagic.OOXML.equals(fileMagic)) {
                return ExcelTypeEnum.XLSX;
            }
            throw new IllegalArgumentException("excelTypeEnum can not null");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}




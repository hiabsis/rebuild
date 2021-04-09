package com.translate.rebuild.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @program: 1-1-transalte
 * @description:
 * @author: cyw
 * @create: 2021-03-07 23:48
 **/
@Data
public class SubmitDate extends BaseRowModel {
    @ExcelProperty(value = "数据库表", index = 0)
    private String values0;
    @ExcelProperty(value = "翻译表名", index = 1)
    private String values1;
    @ExcelProperty(value = "资产名称", index = 2)
    private String values2;
    @ExcelProperty(value = "资产别名", index = 3)
    private String values3;
    @ExcelProperty(value = "字段翻译", index = 4)
    private String values4;
    @ExcelProperty(value = "数据来源(PDF)", index = 5)
    private String values5;
    @ExcelProperty(value = "从属关系", index = 6)
    private String values6;
    @ExcelProperty(value = "标签", index = 7)
    private String values7;
    @ExcelProperty(value = "修订字段", index = 8)
    private String values8;



}

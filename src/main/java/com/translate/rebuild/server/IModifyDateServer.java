package com.translate.rebuild.server;

import com.translate.rebuild.bean.SubmitDate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: rebuild
 * @description:
 * @author: cyw
 * @create: 2021-04-01 23:24
 **/
public interface IModifyDateServer {
    /**
     * 根据从URL得到正确的表名
     */
    public List<SubmitDate> updateTableNameByURL(List<SubmitDate> dates);

    /**
     * 根据第三列的中文 修正第四列的的翻译
     */
    public List<SubmitDate> updateTableNameByThirdColumns(List<SubmitDate> dates);

    /**
     * 去除翻译中多余的数据一些选项
     */

    public List<SubmitDate> deleteWordAddition(List<SubmitDate> dates);
    /**
     * 把数据转换成Map
     *  Map<URL，Map<表名，表内的数据>>
     */
    public  Map<String, Map<String,SubmitDate>> getTableMap(List<SubmitDate> dates);



    /**
     * 获得数据的字段翻译标签
     */
    public List<SubmitDate> getUniqueRowBySecondColumn(List<SubmitDate> dates);
    /**
     * 根据正确字段修正原始数据
     */
    public List<SubmitDate> updateDateByRightTranslate(List<SubmitDate> oldDates,List<SubmitDate> rightDates);

    /**
     * 分离出正确和错误的字段翻译
     */
    public void saveRightAndErrorDates(List<SubmitDate> dates,String rightDateFilePath,String errorDateFilePath);
    /**
     * 剔除系统表以及一些不符合规定的数据
     */
    public List<SubmitDate> deleteRubbishDate(List<SubmitDate> dates,String saveRubbishDateFilePath,List<SubmitDate> checkPassDate);

    /**
     * 删除多余的数据字段
     */
    public List<SubmitDate> deleteRepeatTableRowDate(List<SubmitDate> dates,String repeatDateFilePath);
    /**
     * 按照表名顺序
     */
    public List<SubmitDate> sortDateByTableName(List<SubmitDate> dates);

    /**
     * 按照表名顺序
     */
    public List<SubmitDate> sortDateByTableUrl(List<SubmitDate> dates);

    /**
     * 数据进行排序
     */
    public List<SubmitDate> sortTableDatesByUrlAndTableName(List<SubmitDate> dates);

    /**
     * 获得单表
     */

    public List<SubmitDate> getSingleDateByTableNameAndValues(List<SubmitDate> dates);


//    public List<SubmitDate> getSingleDateByValues(List<SubmitDate> dates);
    /**
     * 获得重复表
     */
    public List<SubmitDate> getRepeatDateByTableNameAndValues(List<SubmitDate> dates);
//    public List<SubmitDate> getSingleDateByTableNameAndValues(List<SubmitDate> dates);

    /**
     * 修正中文翻译部分
     * @param date
     * @return
     */
    public List<SubmitDate> updateTable(List<SubmitDate> date);
}

package com.translate.rebuild.client;

import com.translate.rebuild.bean.SubmitDate;
import com.translate.rebuild.dao.ExcelDao;
import com.translate.rebuild.server.IModifyDateServer;
import com.translate.rebuild.server.ModifyDateServer;
import com.translate.rebuild.utils.FileConstant;

import java.util.List;

/**
 * @program: rebuild
 * @description: 修正数据
 * @author: cyw
 * @create: 2021-04-02 11:51
 **/
public class ModifyDatesClient {
    public static void main(String[] args) {
        runCheckDateServe();
//        runCheckWordAndLabel();
    }

    public static void runCheckWordAndLabel(){
        String datesFileName = "result_dates.xlsx";
        String saveFileName = "word_lable.xlsx";
        List<SubmitDate> dates = ExcelDao.readDataFormLocal(FileConstant.getFileRelativePath(datesFileName),SubmitDate.class);
        IModifyDateServer dateServer = new ModifyDateServer();
        dates  = dateServer.getUniqueRowBySecondColumn(dates);
        ExcelDao.saveDateToLocal(dates,getLocalFilePath(saveFileName),SubmitDate.class);

    }
    public static void runCheckDateServe(){
        IModifyDateServer dateServer = new ModifyDateServer();
        String datesFileName = "dates.xlsx";
        String checkPassDatesFileName = "check_pass_dates.xlsx";
        String rubbishDatesFileName = "rubbish.xlsx";
        String resultDatesFileName = "result_dates.xlsx";
        String repeatDatesFileName = "repeat_dates.xlsx";
        String tempDateFileName =  "result_dates_temp.xlsx";
        String singleTableDatesFileName =  "single_table_dates.xlsx";
        String multipleTableDatesFileName =  "multiple_table _dates.xlsx";
        List<SubmitDate> dates = ExcelDao.readDataFormLocal(FileConstant.getFileRelativePath(datesFileName),SubmitDate.class);
        List<SubmitDate> checkPassDates = ExcelDao.readDataFormLocal(getLocalFilePath(checkPassDatesFileName),SubmitDate.class);
        List<SubmitDate> singleTableDates = null;
        List<SubmitDate> repeatTableDates = null;
        /**
         * 修正表名
         */
        dates = dateServer.updateTableNameByURL(dates);
        /**
         * 移除不需要的数据
         */
        dates = dateServer.deleteRubbishDate(dates,getLocalFilePath(rubbishDatesFileName),checkPassDates);
        /**
         *  同一URL下一张表张表去掉重复字段
         */
        dates = dateServer.deleteRepeatTableRowDate(dates,getLocalFilePath(repeatDatesFileName));
        dates = dateServer.sortDateByTableUrl(dates);

        /**
         * 修正字段翻译
         */
        dates = dateServer.updateDateByRightTranslate(dates,checkPassDates);

        dates = dateServer.updateTable(dates);

//
//        /**
//         * 获取单表
//         */
        singleTableDates = dateServer.getSingleDateByTableNameAndValues(dates);
        singleTableDates = dateServer.sortDateByTableUrl(singleTableDates);
        /**
         * 获取重复表
         */
        repeatTableDates = dateServer.getRepeatDateByTableNameAndValues(dates);
//        repeatTableDates = dateServer.sortDateByTableUrl(repeatTableDates);



        ExcelDao.saveDateToLocal(dates,getLocalFilePath(resultDatesFileName),SubmitDate.class);
        ExcelDao.saveDateToLocal(singleTableDates,getLocalFilePath(singleTableDatesFileName),SubmitDate.class);
        ExcelDao.saveDateToLocal(repeatTableDates,getLocalFilePath(multipleTableDatesFileName),SubmitDate.class);
    }


    public static String getLocalFilePath(String fileName){
        return FileConstant.getFileRelativePath(fileName);
    }

}

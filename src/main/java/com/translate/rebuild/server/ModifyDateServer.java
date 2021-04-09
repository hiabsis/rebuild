package com.translate.rebuild.server;

import com.translate.rebuild.bean.SubmitDate;
import com.translate.rebuild.dao.ExcelDao;
import com.translate.rebuild.utils.ChineseCharToEnUtil;
import com.translate.rebuild.utils.FileConstant;
import com.translate.rebuild.utils.WordUtil;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @program: rebuild
 * @description:
 * @author: cyw
 * @create: 2021-04-01 23:24
 **/
public class ModifyDateServer implements IModifyDateServer {
    /**
     * 根据从URL得到正确的表名
     * @param dates
     * @return
     */
    @Override
    public List<SubmitDate> updateTableNameByURL(List<SubmitDate> dates) {
        for (int i=0;i<dates.size();i++){
            String url = dates.get(i).getValues6();
            dates.get(i).setValues0(getTableNameFromURL(url));
        }

        return dates;
    }


    /**
     * 根据第三列的中文 修正第四列的的翻译
     * @param dates
     * @return
     */
    @Override
    public List<SubmitDate> updateTableNameByThirdColumns(List<SubmitDate> dates) {
        for (int i=0;i<dates.size();i++){
            String word = dates.get(i).getValues3();
            if (WordUtil.hasChineseChar(word)){
                dates.get(i).setValues4(word);
            }
        }
        return dates;
    }

    /**
     * 去除翻译中多余的数据一些选项
     * @param dates
     * @return
     */
    @Override
    public List<SubmitDate> deleteWordAddition(List<SubmitDate> dates) {
        for (int i=0;i<dates.size();i++){
            String word= dates.get(i).getValues4();
            word = WordUtil.updateWorldSpecialCharacters(word);
            word = WordUtil.deleteWorldAdditional(word);
            dates.get(i).setValues4(word);
        }
        return dates;
    }

    @Override
    public List<SubmitDate> updateTable(List<SubmitDate> dates) {
        for (int i = 0;i<dates.size();i++){
            SubmitDate date = dates.get(i);
            if (date.getValues4() == null && WordUtil.hasChineseChar(date.getValues3())){
                dates.get(i).setValues4(WordUtil.updateWorldSpecialCharacters(date.getValues3()));
            }
        }
        return dates;
    }

    /**
     * 把数据转换成Map
     * @return   Map<URL，Map<表名，表内的数据>>
     */
    @Override
    public Map<String, Map<String, SubmitDate>> getTableMap(List<SubmitDate> dates) {
        Map<String, Map<String, SubmitDate>> tableMap = new HashMap<>();
        for (SubmitDate rowDate : dates){
            Map<String, SubmitDate> map = null;
            if (!tableMap.containsKey(rowDate.getValues6())){
                map = new HashMap<>();
            }else {
                 map = tableMap.remove(rowDate.getValues6());
            }
            map.put(rowDate.getValues2(),rowDate);
            tableMap.put(rowDate.getValues6(),map);
        }
        return tableMap;
    }



    /**
     * 获得数据的字段翻译标签
     */
    @Override
    public List<SubmitDate> getUniqueRowBySecondColumn(List<SubmitDate> dates) {
        Map<String ,SubmitDate> dateMap = getUniqueRowMap(dates);
        List<SubmitDate> result = new LinkedList<>();
        Set<String> keys = getSortSet(dateMap.keySet());
        for (String key:keys){
            result.add(dateMap.get(key));
        }
        return result;
    }

    /**
     * 获取字段 图
     * @param dates
     * @return
     */
    private static Map<String ,SubmitDate> getUniqueRowMap(List<SubmitDate> dates){
        Map<String ,SubmitDate> dateMap = new HashMap<>();
        for (int i=0;i<dates.size();i++){
            SubmitDate submitDate = dates.get(i);
            String values = WordUtil.removeNumberFormStr(submitDate.getValues2());
            if (!dateMap.containsKey(values)){
                dateMap.put(values,submitDate);
            }else if (dateMap.get(values) == null && submitDate.getValues4() != null){
                dateMap.put(values,submitDate);
            }
        }
        return dateMap;
    }

    /**
     * 根据正确字段修正原始数据
     */
    @Override
    public List<SubmitDate> updateDateByRightTranslate(List<SubmitDate> oldDates, List<SubmitDate> rightDates) {
        Map<String,SubmitDate>  rightDateMap =  getUniqueRowMap(rightDates);
        int count = 0;
        for (int i=0;i<oldDates.size();i++){
            String word = WordUtil.removeNumberFormStr(oldDates.get(i).getValues2());
            if (rightDateMap.containsKey(word)){
                SubmitDate rightDate = rightDateMap.get(word);
                oldDates.get(i).setValues4(rightDate.getValues4());
                oldDates.get(i).setValues7(rightDate.getValues7());
                count ++;

            }
        }
        System.out.println(count);
        return oldDates;
    }

    @Override
    public void saveRightAndErrorDates(List<SubmitDate> rightValuesData, String rightDateFilePath, String errorDateFilePath) {

        /**
         * 拼写不正确的去除
         *  获取翻译字段拼音 比较 不正确 -->>
         */
        List<SubmitDate> errorDates = new LinkedList<>();
        List<SubmitDate> rightDates = new LinkedList<>();

        for (int i=0;i<rightValuesData.size();i++){
            SubmitDate date = rightValuesData.get(i);
            String spell = ChineseCharToEnUtil.getAllFirstLetter(date.getValues4());

            String value = WordUtil.removeNumberFormStr(date.getValues2());  //字段
            spell = WordUtil.removeNumberFormStr(spell); //拼音

            if ("".equals(spell)){ //为空跳过
                errorDates.add(date);
                continue;
            }
            if (value.split("_").length>=2){ //检查出单词
                rightDates.add(date);
                continue;
            }

            if (!spell.equals(value)){ //错误单词
                errorDates.add(date);

            }else {        //正确单词
                rightDates.add(date);
            }

        }



        Map<String,String> errorMap =  getSpellMap(errorDates);

        errorDates = insertRightWordToEighthColumn(errorDates,errorMap);
        ExcelDao.saveDateToLocal(rightDates,"src/main/java/com/cyw/transalte/handle/db/check/hascheck/erro-change.xlsx",SubmitDate.class);
        ExcelDao.saveDateToLocal(errorDates,"src/main/java/com/cyw/transalte/handle/db/check/hascheck/erro-change.xlsx",SubmitDate.class);

    }

    /**
     * 剔除系统表以及一些不符合规定的数据
     * @param dates
     * @param saveRubbishDateFilePath
     */
    @Override
    public List<SubmitDate> deleteRubbishDate(List<SubmitDate> dates, String saveRubbishDateFilePath,List<SubmitDate> checkPassDate) {
        dates = updateTableNameByURL(dates);
        List<SubmitDate> rubbishRowDates = new LinkedList<>();
        // 解析表名
        List<SubmitDate> saveRowDates = new LinkedList<>();
        for (int i=0;i<dates.size();i++){
            String word = dates.get(i).getValues0();
            if (hasSpecialPreWord(word)){
                rubbishRowDates.add(dates.get(i));
            }else {
                saveRowDates.add(dates.get(i));
            }
        }
        rubbishRowDates = updateDateByRightTranslate(rubbishRowDates,checkPassDate);
        rubbishRowDates = sortDateByTableUrl(rubbishRowDates);
        ExcelDao.saveDateToLocal(rubbishRowDates,saveRubbishDateFilePath,SubmitDate.class);
        return saveRowDates;
    }

    /**
     * 删除多余的数据字段
     *  有没有URL
     *
     * @param dates
     * @param repeatDateFilePath
     * @return
     */
    @Override
    public List<SubmitDate> deleteRepeatTableRowDate(List<SubmitDate> dates, String repeatDateFilePath) {
        dates = updateTableNameByURL(dates);
        List<SubmitDate> reaPeatDates = new LinkedList<>();
        List<SubmitDate>  singleDates= new LinkedList<>();
        Map<String ,Map<String,SubmitDate>> tableMap = new HashMap<>();
        for (int i=0;i<dates.size();i++){
            SubmitDate rowDate = dates.get(i);
            if (!tableMap.containsKey(rowDate.getValues6())){
                Map<String, SubmitDate> map = new HashMap<>();
                map.put(rowDate.getValues2(),rowDate);
                tableMap.put(rowDate.getValues6(),map);
            }else {
                Map<String, SubmitDate> map = tableMap.remove(rowDate.getValues6());
                if (map.containsKey(rowDate.getValues2())){
                    reaPeatDates.add(rowDate);
                }
                map.put(rowDate.getValues2(),rowDate);
                tableMap.put(rowDate.getValues6(),map);
            }
        }

        singleDates = sortTableByKey(tableMap);
        ExcelDao.saveDateToLocal(reaPeatDates,repeatDateFilePath,SubmitDate.class);
        return singleDates;
    }



    private static String[] PRE_WORD = "dba_ user_ v$ all_ session_ sys_ index_ v$".split(" ");

    /**
     * 检查是非为垃圾数据
     * @param word
     * @return
     */
    private static  boolean hasSpecialPreWord(String word){
        if (word.length() == 1){
            return true;
        }
        if (word == null){
            return  false;
        }
        if (word.contains("$")){
            return true;
        }
        for (String index : PRE_WORD){
            if (StringUtils.startsWithIgnoreCase(word,index)){
                return true;
            }
        }

        return false;
    }
    private static List<SubmitDate> insertRightWordToEighthColumn(List<SubmitDate> errorDates,  Map<String,String> errorMap){
        for (int i=0;i<errorDates.size();i++){
            SubmitDate data = errorDates.get(i);
            String value = WordUtil.removeNumberFormStr(data.getValues2());
            if (data.getValues8() == null && errorMap.containsKey(value)){
                errorDates.get(i).setValues8(errorMap.get(value));
            }

            if (data.getValues8() == null && data.getValues5() != null){
                System.out.println(value +" " +data.getValues4() +"  "+ errorMap.get(value));
                errorDates.get(i).setValues8(data.getValues4());
            }
        }
        return errorDates;
    }
    /**
     * 拼写和翻译对应正确字段
     * @param errorDates
     * @return
     */
    private static   Map<String,String>  getSpellMap(List<SubmitDate> errorDates){
        Map<String,String> errorMap = new HashMap<>();
        for (int i=0;i<errorDates.size();i++){
            SubmitDate data = errorDates.get(i);
            String spell = ChineseCharToEnUtil.getAllFirstLetter(data.getValues4());
            spell = WordUtil.removeNumberFormStr(spell);
            errorMap.put(spell,data.getValues4());
        }
        return errorMap;
    }




    /**
     * 获取表名对 URL的映射
     * @param singleUrlList
     * @return
     */
    private Map<String, List<String>> getTableNameUrlMap(List<String> singleUrlList) {
        Map<String, List<String>> nameUrlMap = new HashMap<>();
        for (String url:singleUrlList){
            String tableName = getTableNameFromURL(url);
            if (!nameUrlMap.containsKey(tableName)){
                List<String> urlList = new LinkedList<>();
                urlList.add(url);
                nameUrlMap.put(tableName,urlList);
            }else {
                List<String> urlList = nameUrlMap.remove(tableName);
                urlList.add(url);
                nameUrlMap.put(tableName,urlList);
            }
        }
        return nameUrlMap;
    }






    /**
     * 统计TableMap中有多少的数据
     */
    private int countTableMapRows(Map<String,Map<String ,SubmitDate>>  tableMap){
        Set<String> keys =  tableMap.keySet();
        int count = 0;
        for (String key :keys){
            count += tableMap.get(key).size();
        }
        return  count;
    }


    private static List<String> getSortList(List<String> dates){
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 返回相反的compare
                return o2.compareTo(o1);
            }
        });
        return dates;
    }




    /**
     * 获取排序后的Set
     * @param set
     * @return
     */
    private static Set<String> getSortSet(Set<String> set){
        Set<String> sortTableKey = new TreeSet<String>(Comparator.reverseOrder());
        sortTableKey.addAll(set);
        return sortTableKey;
    }


    /**
     * 按照表名顺序
     * @param dates
     * @return
     */
    @Override
    public List<SubmitDate> sortDateByTableName(List<SubmitDate> dates) {
        dates = updateTableNameByURL(dates);
        List<SubmitDate> sortDates = new LinkedList<>();
        Map<String ,Map<String ,SubmitDate>> tableMap = getTableMap(dates);
        for (int i=0;i<dates.size();i++){
            SubmitDate date = dates.get(i);
            String tableName = date.getValues0();
            if (tableMap.containsKey(tableName)){
                Map<String ,SubmitDate> table = tableMap.remove(tableName);
                table.put(date.getValues2(),date);
                tableMap.put(date.getValues0(),table);
            }else {
                Map<String ,SubmitDate> table = new HashMap<>();
                table.put(date.getValues2(),date);
                tableMap.put(date.getValues0(),table);
            }
        }
        Set<String> tableNameKeys = getSortSet(tableMap.keySet());
        for (String tableNameKey:tableNameKeys){
            Map<String,SubmitDate> table = tableMap.get(tableNameKey);
            Set<String> valuesKeys = getSortSet(table.keySet());
            for (String valuesKey:valuesKeys){
                sortDates.add(table.get(valuesKey));
            }
        }
        return sortDates;
    }



    /**
     * 数据进行排序
     *  hash -- >  url
     */
    @Override
    public List<SubmitDate> sortTableDatesByUrlAndTableName(List<SubmitDate> dates) {
        dates = sortDateByTableName(dates);
        Map<String,Map<String ,SubmitDate>> tableMap = getTableMap(dates);
        Map<String,List<String>> hashMap =  getHashMap(tableMap);
        List<String> singleUrlList =  getSingleUrl(hashMap);
        List<String> singleTableNameList = getSingleTableName(singleUrlList);
        Map<String,List<String>> nameUrlMap = getTableNameUrlMap(singleUrlList);
        List<SubmitDate> result = getSortSingleTable(nameUrlMap,singleTableNameList,tableMap);

        List<String> repeatUrlList =  getRepeatUrl(hashMap);
        result = insetRepeatTable(repeatUrlList,result,tableMap);
        return result;
    }

    @Override
    public List<SubmitDate> sortDateByTableUrl(List<SubmitDate> dates) {
        List<SubmitDate> resultDate = new LinkedList<>();
        Map<String,Map<String,SubmitDate>>  tableMap = getTableMap(dates);
        resultDate = sortTableByKey(tableMap);
        return resultDate;
    }

    private List<SubmitDate> sortTableByKey( Map<String,Map<String,SubmitDate>> tableMap){
        List<SubmitDate> resultDates = new LinkedList<>();
        Set<String> tableNameKeys = getSortSet(tableMap.keySet());
        for (String tableNameKey:tableNameKeys){
            Map<String,SubmitDate> table = tableMap.get(tableNameKey);
            Set<String> valuesKeys = getSortSet(table.keySet());
            for (String valuesKey:valuesKeys){
                resultDates.add(table.get(valuesKey));
            }
        }
        return resultDates;
    }
    @Override
    public List<SubmitDate> getSingleDateByTableNameAndValues(List<SubmitDate> dates) {
        Map<String,Map<String ,SubmitDate>> tableMap = getTableMap(dates);
        Map<String,List<String>> hashMap =  getHashMap(tableMap);
        // key 字段的和  values对应的和
        List<String> singleUrlList =  getSingleUrl(hashMap);
        List<String> singleTableNameList = getSingleTableName(singleUrlList);
        Map<String,List<String>> nameUrlMap = getTableNameUrlMap(singleUrlList);
        List<SubmitDate> result = getSortSingleTable(nameUrlMap,singleTableNameList,tableMap);
        return result;
    }


    @Override
    public List<SubmitDate> getRepeatDateByTableNameAndValues(List<SubmitDate> dates) {
        Map<String,Map<String ,SubmitDate>> tableMap = getTableMap(dates);
        Map<String,List<String>> hashMap =  getHashMap(tableMap);
        List<String> repeatUrlList =  getRepeatUrl(hashMap);
        List<SubmitDate>  result = new LinkedList<>();
        result = insetRepeatTable(repeatUrlList,result,tableMap);
        return result;
    }


    /**
     * 得到表格的字段hasH和对应的Url
     * @param tableMap
     * @return
     */
    private   Map<String,List<String>> getHashMap( Map<String,Map<String ,SubmitDate>> tableMap){
        Map<String,List<String>> hashMap = new HashMap<>();
        Set<String> keys = tableMap.keySet();
        for (String key : keys){
            String hash = getTableHash(tableMap.get(key));
            if (hash == null){
                hash = "";
            }
            if (!hashMap.containsKey(hash)){
                List<String> urlList = new ArrayList<>();
                urlList.add(key);
                hashMap.put(hash,urlList);
            }else {
                List<String> urlList = hashMap.remove(hash);
                urlList.add(key);
                hashMap.put(hash,urlList);

            }
        }
        return hashMap;
    }


    /**
     *  获取一张表n内容的hash
     */
    public String getTableHash(Map<String,SubmitDate> rowMap){
        if (rowMap.size() == 0) return "";
        Set<String> keys = getSortSet(rowMap.keySet());

        String words = "";
        for (String key:keys){
            words += key;
        }
        return words;
    }


    /**
     * 获取单表对应每一个hash所对应的URL
     * @param hashMap
     * @return
     */
    private List<String> getSingleUrl(Map<String,List<String>> hashMap){
        List<String> singleUrl = new LinkedList<>();
        Set<String> hashKeys = hashMap.keySet();
        for (String hashKey : hashKeys){
            List<String> urls = hashMap.get(hashKey);
            if (hashMap.get(hashKey).size() == 1){
                singleUrl.add(urls.get(0));
            }
        }
        return singleUrl;
    }


    /**
     * 获取重复表表对应每一个hash所对应的URL
     */
    private List<String> getRepeatUrl(Map<String,List<String>> hashMap){
        List<String> singleUrl = new LinkedList<>();
        Set<String> hashKeys = hashMap.keySet();
        for (String hashKey : hashKeys){
            List<String> urls = hashMap.get(hashKey);
            if (hashMap.get(hashKey).size() > 1){
                for (String url:urls){
                    singleUrl.add(url);
                }

            }
        }
        return singleUrl;
    }



    /**
     * 从URL中解析出表名
     */
    private List<String> getSingleTableName( List<String> singleUrl){
        List<String> tableNameList = new LinkedList<>();
        for (String url:singleUrl){
            tableNameList.add(getTableNameFromURL(url));
        }

        return  getSortList(tableNameList);
    }
    /**
     * 从Url中获得表名
     */
    private String getTableNameFromURL(String url){
        if (url == null || url.equals("")) {
            return  "";
        }
        String[] values = url.split("/");
        return values[values.length-1];

    }



    /**
     * 按照顺序获取单表
     */

    private static List<SubmitDate> getSortSingleTable(Map<String,List<String>> nameUrlMap,  List<String> singleTableNameList,Map<String,Map<String ,SubmitDate>> tableMap){
        List<SubmitDate>  result = new LinkedList<>();
        for (String tableName : singleTableNameList){
            List<String> urlsList = nameUrlMap.get(tableName);
            for (String url : urlsList){
                Map<String ,SubmitDate> rowMap = tableMap.get(url);
                Set<String> keys = getSortSet(rowMap.keySet());
                for (String key: keys){
                    result.add(rowMap.get(key));
                }
            }
        }
        return result;
    }



    /**
     * 按照顺序获取重复表
     */

    private List<SubmitDate> insetRepeatTable(List<String> repeatUrlList, List<SubmitDate> result, Map<String, Map<String, SubmitDate>> tableMap) {
        for (String url :repeatUrlList){
            Map<String,SubmitDate> rowMap = tableMap.get(url);
            Set<String> keys = getSortSet(rowMap.keySet());
            for (String key:keys){
                result.add(rowMap.get(key));
            }
        }
        return result;
    }
}

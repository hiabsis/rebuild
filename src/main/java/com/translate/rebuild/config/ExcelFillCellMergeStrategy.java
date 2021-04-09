package com.translate.rebuild.config;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

public class ExcelFillCellMergeStrategy implements CellWriteHandler {

    private int[] mergeColumnIndex;
    private int mergeRowIndex;

    public ExcelFillCellMergeStrategy() {
    }

    public ExcelFillCellMergeStrategy(int mergeRowIndex, int[] mergeColumnIndex) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeColumnIndex = mergeColumnIndex;
    }


    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer integer, Integer integer1, Boolean aBoolean) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer integer, Boolean aBoolean) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
        int curRowIndex = cell.getRowIndex();
        int curColIndex = cell.getColumnIndex();
        if (curRowIndex > mergeRowIndex) {
            for (int i = 0; i < mergeColumnIndex.length; i++) {
                if (curColIndex == mergeColumnIndex[i]) {
//                    System.out.println("1212");
                    mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
                    break;
                }
            }
        }
    }


    /**
     * 当前单元格向上合并
     *
     * @param writeSheetHolder
     * @param cell             当前单元格
     * @param curRowIndex      当前行
     * @param curColIndex      当前列
     */

    private void mergeWithPrevRow(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColIndex) {
//        System.out.println("dddd");

            Object curData = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : cell.getNumericCellValue();
            Cell preCell = cell.getSheet().getRow(curRowIndex - 1).getCell(curColIndex);
//            System.out.println(curData.toString());
            Object preData = preCell.getCellTypeEnum() == CellType.STRING ? preCell.getStringCellValue() : preCell.getNumericCellValue();
            // 将当前单元格数据与上一个单元格数据比较
            Boolean dataBool = preData.equals(curData);
            if (curData instanceof  Double){
                return;
            }

//            System.out.println( "data" + dataBool);
        try{
//            System.out.println(curData.getClass());
            //此处需要注意：因为我是按照序号确定是否需要合并的，所以获取每一行第一列数据和上一行第一列数据进行比较，如果相等合并
//            System.out.println();
            Boolean bool = cell.getRow().getCell(0).getStringCellValue().equals(cell.getSheet().getRow(curRowIndex - 1).getCell(0).getStringCellValue());
            if (bool){
//                System.out.println(preData + " " +curData + " "+dataBool+" "+bool);
            }
            if (dataBool ) {
                Sheet sheet = writeSheetHolder.getSheet();
                List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
                boolean isMerged = false;
                for (int i = 0; i < mergeRegions.size() && !isMerged; i++) {
                    CellRangeAddress cellRangeAddr = mergeRegions.get(i);
                    // 若上一个单元格已经被合并，则先移出原有的合并单元，再重新添加合并单元
                    if (cellRangeAddr.isInRange(curRowIndex - 1, curColIndex)) {
                        sheet.removeMergedRegion(i);
                        cellRangeAddr.setLastRow(curRowIndex);
                        sheet.addMergedRegion(cellRangeAddr);
                        isMerged = true;
                    }
                }
                // 若上一个单元格未被合并，则新增合并单元
                if (!isMerged) {
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex - 1, curRowIndex, curColIndex, curColIndex);
                    sheet.addMergedRegion(cellRangeAddress);
                }
            }
        }catch (Exception e){

        }

    }
}
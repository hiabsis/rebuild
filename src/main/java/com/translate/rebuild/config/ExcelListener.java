package com.translate.rebuild.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 对在读取本地表格时候进行监听
 * @param <T>
 */
@Slf4j
public class ExcelListener<T extends BaseRowModel> extends AnalysisEventListener<T> {
    private final List<T> rows = new ArrayList<>();

    @Override
    public void invoke(T object, AnalysisContext context) {
        rows.add(object);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    public List<T> getRows() {
        return rows;
    }
}
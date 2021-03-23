package com.jz.zeus.excel.write.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.write.handler.AbstractSheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author JZ
 * @Date 2021/3/1 11:13
 */
public class DropDownBoxHandler extends AbstractSheetWriteHandler {

    private List<DropDownBoxInfo> infoList;

    public DropDownBoxHandler() {

    }

    public DropDownBoxHandler(DropDownBoxInfo dropDownBoxInfo) {
        infoList = new ArrayList<>();
        infoList.add(dropDownBoxInfo);
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (CollUtil.isEmpty(infoList)) {
            return;
        }
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        infoList.forEach(info -> {
            DataValidationConstraint constraint = helper.createExplicitListConstraint(info.contents);
            DataValidation dataValidation = helper.createValidation(constraint,
                    new CellRangeAddressList(info.firstRow, info.lastRow, info.firstCol, info.lastCol));
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        });
    }

    public void addDropDownBoxInfo(DropDownBoxInfo info) {
        if (CollUtil.isEmpty(infoList)) {
            infoList = new ArrayList<>();
        }
        infoList.add(info);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropDownBoxInfo {

        /**
         * 起始行
         */
        private int firstRow;

        /**
         * 截止行
         */
        private int lastRow;

        /**
         * 起始列
         */
        private int firstCol;

        /**
         * 截止列
         */
        private int lastCol;

        /**
         * 下拉框中选项
         */
        private String[] contents;

        public DropDownBoxInfo(int row, int column, String... contents) {
            this(row, row, column, column, contents);
        }

        public void setContent(String... contents) {
            this.contents = contents;
        }

    }

}

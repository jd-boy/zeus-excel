package com.jz.zeus.excel.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.jz.zeus.excel.constant.Constants;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.nio.charset.Charset;

/**
 * @Author JZ
 * @Date 2021/3/26 11:52
 */
@UtilityClass
public class ExcelUtils {

    private static final URLEncoder URL_ENCODER = new URLEncoder();

    static {
        URL_ENCODER.addSafeCharacter('.');
        URL_ENCODER.addSafeCharacter('_');
    }

    /**
     * 在 WorkBook 中创建一个 Name，当 Name 已存在则返回原有 Name
     * @param workbook            引用数据所在 workbook
     * @param sheetName           引用数据所在 sheet 的 名称
     * @param nameName            待创建 Name 的名称
     * @param startRowIndex       Name 引用区域的行开始索引
     * @param endRowIndex         Name 引用区域的行结束索引
     * @param startColIndex       Name 引用区域的列开始索引
     * @param endColIndex         Name 引用区域的行结束索引
     * @return                    当 Name 已存在则返回原有 Name，若不存在则新建
     */
    public Name createName(Workbook workbook, String sheetName, String nameName, int startRowIndex, int endRowIndex, int startColIndex, int endColIndex) {
        String encodeName = encodeString(nameName);
        Name name;
        if ((name = workbook.getName(encodeName)) != null) {
            return name;
        }
        name = workbook.createName();
        name.setNameName(encodeName);
        String startColStr = columnIndexToStr(startColIndex);
        String endColStr = columnIndexToStr(endColIndex);
        String refersToFormula = sheetName +
              "!$" + startColStr +
              '$' + startRowIndex +
              ":$" + endColStr +
              '$' + endRowIndex;
        name.setRefersToFormula(refersToFormula);
        return name;
    }

    /**
     * 对字符串进行 url encode
     * @param str  需要 url encode 的字符串
     * @return     encode 后的字符串
     */
    public String encodeString(String str) {
        if (str == null) {
            return null;
        }
        return URL_ENCODER.encode(str, Charset.defaultCharset())
              .replaceAll("%", "_");
    }

    public Sheet getSheet(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet;
        Integer sheetIndex = writeSheetHolder.getSheetNo();
        String sheetName = writeSheetHolder.getSheetName();
        if (workbook instanceof SXSSFWorkbook) {
            if (sheetIndex != null) {
                sheet = ((SXSSFWorkbook) workbook).getXSSFWorkbook().getSheetAt(sheetIndex);
            } else {
                sheet = ((SXSSFWorkbook) workbook).getXSSFWorkbook().getSheet(sheetName);
            }
        } else {
            if (writeSheetHolder.getSheetNo() != null) {
                sheet = writeWorkbookHolder.getWorkbook().getSheetAt(sheetIndex);
            } else {
                sheet = writeWorkbookHolder.getWorkbook().getSheet(sheetName);
            }
        }
        return sheet;
    }

    /**
     * 根据字符长度和字号计算列宽
     */
    public int calColumnWidth(String cellStrValue, int fontSize) {
        String[] strs = cellStrValue.split("\n");
        int result = 0;
        for (String s : strs) {
            int chineseNum = StringUtils.chineseNum(s);
            int englishNum = s.length() - chineseNum;
            int columnWidth = (int) ((englishNum * 1.2 + chineseNum * 2) * 34 * fontSize);
            result = Math.max(result, columnWidth);
        }
        return result > Constants.MAX_COLUMN_WIDTH ? Constants.MAX_COLUMN_WIDTH : result;
    }

    public static int columnToIndex(String column) {
        if (!column.matches("[A-Z]+")) {
            try {
                throw new Exception("Invalid parameter");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            index += ((int) chars[i] - (int) 'A' + 1)
                    * (int) Math.pow(26, chars.length - i - 1);
        }
        return index;
    }

    /**
     * Excel列索引转字母
     * @param columnIndex      列索引,从 0 开始
     */
    public String columnIndexToStr(int columnIndex) {
        Assert.isTrue(columnIndex >= 0);
        StringBuilder column = new StringBuilder();
        do {
            if (column.length() > 0) {
                columnIndex--;
            }
            column.insert(0, ((char) (columnIndex % 26 + (int) 'A')));
            columnIndex = ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return column.toString();
    }

    public void setCommentErrorInfo(Sheet sheet, Integer rowIndex, Integer columnIndex, String... errorMessages) {
        setCommentErrorInfo(sheet, rowIndex, columnIndex, "- ", "", errorMessages);
    }

    /**
     * 给sheet的指定单元格设置错误信息，错误信息将显示在批注里，且单元格背景色为红色
     * @param sheet                 单元格 所在sheet
     * @param rowIndex              单元格 的行索引
     * @param columnIndex           单元格 的列索引
     * @param errorMsgPrefix        错误信息前缀
     * @param errorMsgSuffix        错误信息后缀
     * @param errorMessages         错误信息
     */
    public void setCommentErrorInfo(Sheet sheet, Integer rowIndex, Integer columnIndex,
                                    String errorMsgPrefix, String errorMsgSuffix, String... errorMessages) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.index);
        cell.setCellStyle(cellStyle);

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, columnIndex, rowIndex, columnIndex+2, rowIndex+2));
        comment.setString(new XSSFRichTextString(ArrayUtil.join(errorMessages, "\n", errorMsgPrefix, errorMsgSuffix)));
        cell.setCellComment(comment);
    }

}

package com.jz.zeus.excel.test;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author JZ
 * @Date 2021/2/23 18:00
 */
@Data
public class DemoData {

//    @HeadStyle(fillPatternType = FillPatternType.NO_FILL,
//            borderRight = BorderStyle.NONE, borderLeft = BorderStyle.NONE,
//            borderBottom = BorderStyle.NONE, borderTop = BorderStyle.NONE)
    @HeadFontStyle(fontName = "黑体", color = 10, bold = false)
    @ExcelProperty(value = "媒体CODE")
    private String mateCode;

    @NotNull(message = "src 不能为null")
    @ExcelProperty(value = "SRC")
    private String src;

    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 14)
    @ExcelProperty(value = "DEST")
    private String dest;

    @ExcelProperty(value = "FUNC")
    private String func;

}

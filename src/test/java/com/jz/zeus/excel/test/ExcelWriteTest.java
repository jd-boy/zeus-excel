package com.jz.zeus.excel.test;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import com.alibaba.excel.EasyExcel;
import com.jz.zeus.excel.CellErrorInfo;
import com.jz.zeus.excel.DynamicHead;
import com.jz.zeus.excel.ValidationInfo;
import com.jz.zeus.excel.ZeusExcel;
import com.jz.zeus.excel.context.ExcelContext;
import com.jz.zeus.excel.test.data.DemoData;
import com.jz.zeus.excel.write.handler.ErrorInfoHandler;
import com.jz.zeus.excel.write.property.CellStyleProperty;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.FillPatternType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @Author JZ
 * @Date 2021/3/22 10:17
 */
public class ExcelWriteTest {

//    private static String path = "C:\\Users\\Administrator\\Desktop\\254.xlsx";
    private static String path = "C:\\Users\\User\\Desktop\\254.xlsx";

    private static String templatePath = "C:\\Users\\User\\Desktop\\123.xlsx";

    public static void main(String[] args) throws IOException {
        Console.log("写入Excel前内存：{}M", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024));
        TimeInterval timer = DateUtil.timer();

        CellStyleProperty styleProperty = CellStyleProperty.getDefaultHeadPropertyByHead(0, "图片（必填）\n允许的文件类型*JPG,1080*1920，大小限制150K。素材必须满足腾讯所有规格要求，否则无法通过审核。");
        styleProperty.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        styleProperty.setCellFillForegroundColor("#8EA9DB");
//        styleProperty.setCellFillForegroundColor("#8EA9DB");

        CellStyleProperty styleProperty1 = CellStyleProperty.getDefaultHeadPropertyByHead(0, "落地页（必填）");
        styleProperty1.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//        styleProperty1.setFillForegroundColor(IndexedColors.RED.index);
        styleProperty1.setCellFillForegroundColor("#8EA9DB");
        List<CellStyleProperty> styleProperties = ListUtil.toList(styleProperty, styleProperty1);

        List<DynamicHead> dynamicHeads = new ArrayList<DynamicHead>() {{
            add(DynamicHead.buildAppendInfo("src", "（必填）"));
            add(DynamicHead.build("dest", "destPlus", "（选填）"));
        }};
        List<CellErrorInfo> errorInfos = ListUtil.toList(
            CellErrorInfo.buildByField(15, DemoData::getId, "不合法id"),
            CellErrorInfo.buildByField(3, DemoData::getSrc, "44884")
        );

        ZeusExcel.write(path)
                .withTemplate(templatePath)
                .sheet("模板")
//                .dynamicHeads(dynamicHeads)
//                .headStyles(styleProperties)
//                .validationInfos(getValidationInfo())
                .errorInfos(errorInfos)
                .needHead(false)
//                .needHead(true)
//                .doWrite(ListUtil.toList("s"), null);
                .doWrite(DemoData.class, getDataList("测0_", 10));

//        ZeusExcelWriter excelWriter = ZeusExcel.write(path).build();
//
//        ZeusWriteSheet writeSheet1 = ZeusExcel.writeSheet(0, "模板")
//                .dynamicHeads(dynamicHeads)
//                .singleRowHeadStyles(styleProperties)
//                .validationInfos(getValidationInfo())
//                .build(DemoData.class);
//        ZeusWriteSheet writeSheet2 = ZeusExcel.writeSheet(1, "模板1")
//                .build(DemoData.class);
//        excelWriter.write(getDataList("测1_", 10), writeSheet1);
//        excelWriter.write(getDataList("测3_", 10), writeSheet1);
////        excelWriter.write(getDataList("测2_", 3), writeSheet2);
//        excelWriter.finish();


        Console.log("写入耗时：{}s", timer.intervalSecond());
        Console.log("写入Excel后内存：{}M", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024));
        Console.log("end");
    }

    @SneakyThrows
    public static void fill(OutputStream outputStream, InputStream inputStream, List<CellErrorInfo> cellErrorInfoList) {

        EasyExcel.write(outputStream)
                .withTemplate(inputStream)
                .sheet("模板")
                .registerWriteHandler(new ErrorInfoHandler(new ExcelContext(), cellErrorInfoList))
                .doWrite(Collections.emptyList());
        inputStream.close();
        outputStream.close();
    }

    public static List<List<String>> getHead() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("字符串");
        List<String> head1 = new ArrayList<String>();
        head1.add("数字");
        List<String> head2 = new ArrayList<String>();
        head2.add("dest");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        return list;
    }

    private static List<DemoData> getDataList(String prefix, int num) {
        List<DemoData> dataList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            DemoData demoData = new DemoData();
            demoData.setId(Long.valueOf(i));
            demoData.setDest(prefix + "dest" + i);
            demoData.setSrc(prefix + "src" + i);
            demoData.setFunc(prefix + "func" + i);
//            demoData.setPrice(BigDecimal.valueOf(3.94));
            demoData.setPrice(null);

            Map<String, String> map = new LinkedHashMap<>();
//            map.put("图片（必填）\n允许的文件类型*JPG,1080*1920，大小限制150K。素材必须满足腾讯所有规格要求，否则无法通过审核。", prefix + "12");
//            map.put("落地页（必填）", prefix + "jfak");
//            map.put("第三方异步点击监测URL（2）", prefix + "jfak");
//            map.put("自定义3", prefix + "集分宝");
            demoData.setExtendColumnMap(map);
            dataList.add(demoData);
        }
        return dataList;
    }

    private static List<List<Object>> getDataList1(List<List<String>> heads) {
        List<List<Object>> dataList = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < 10; rowIndex++) {
            for (int j = 0; j < heads.size(); j++) {
                List<Object> data;
                if (rowIndex >= dataList.size()) {
                    data = new ArrayList<>();
                    dataList.add(rowIndex, data);
                } else {
                    data = dataList.get(rowIndex);
                }
                data.add(heads.get(j).get(0) + rowIndex);
            }
        }
        return dataList;
    }

}

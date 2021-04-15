package com.jz.zeus.excel.test;

import com.alibaba.excel.EasyExcel;
import com.jz.zeus.excel.CellErrorInfo;
import com.jz.zeus.excel.DynamicHead;
import com.jz.zeus.excel.ValidationInfo;
import com.jz.zeus.excel.ZeusExcel;
import com.jz.zeus.excel.read.listener.ExcelReadListener;
import com.jz.zeus.excel.test.data.DemoData;
import com.jz.zeus.excel.test.listener.DemoExcelReadListener;
import com.jz.zeus.excel.write.handler.DynamicHeadHandler;
import com.jz.zeus.excel.write.handler.ErrorInfoHandler;
import com.jz.zeus.excel.write.handler.ExtendColumnHandler;
import com.jz.zeus.excel.write.handler.HeadStyleHandler;
import com.jz.zeus.excel.write.property.CellStyleProperty;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
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
    private static String path = "C:\\Users\\User\\Desktop\\2545.xlsx";

    public static void main(String[] args) throws IOException {
        System.out.println("解析Excel前内存："+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");
        long startTime = System.currentTimeMillis();

        CellStyleProperty styleProperty = CellStyleProperty.getDefaultHeadProperty();
        styleProperty.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        styleProperty.setFillForegroundColor(IndexedColors.RED.index);
        List<CellStyleProperty> styleProperties = new ArrayList<>();
        styleProperties.add(styleProperty);

        List<ValidationInfo> validationInfos = new ArrayList<ValidationInfo>() {{
            add(ValidationInfo.buildColumn("ID", "是", "否"));
        }};
        List<DynamicHead> dynamicHeads = new ArrayList<DynamicHead>() {{
            add(DynamicHead.buildAppendInfo("src", "（必填）"));
            add(DynamicHead.build("dest", "destPlus", "（选填）"));
        }};

        ZeusExcel.write(path)
                .sheet("模板")
                .dynamicHeads(dynamicHeads)
                .singleRowHeadStyles(styleProperties)
                .extendHead(Arrays.asList("扩展1", "扩展2"))
                .validationInfos(validationInfos)
                .doWrite(DemoData.class, getDataList());

        System.out.println("耗时：" + (System.currentTimeMillis() - startTime) / 1000 + "s");
        System.out.println("写入Excel后内存："+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");
        System.out.println("end");
    }

    @SneakyThrows
    public static void fill(OutputStream outputStream, InputStream inputStream, List<CellErrorInfo> cellErrorInfoList) {

        EasyExcel.write(outputStream)
                .withTemplate(inputStream)
                .sheet("模板")
                .registerWriteHandler(new ErrorInfoHandler(cellErrorInfoList))
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

    public static List<CellErrorInfo> getCellErrorInfo() {
        List<CellErrorInfo> cellErrorInfoList = new ArrayList<>();
        cellErrorInfoList.add(new CellErrorInfo(1, 1, "格式错误"));
        cellErrorInfoList.add(new CellErrorInfo(4, "媒体CODE", "关系错误"));
        cellErrorInfoList.add(new CellErrorInfo(2, "FUNC", "格式错误")
                .addErrorMsg("数值放假看电视了积分卡积分错误"));
        return cellErrorInfoList;
    }

    private static List<DemoData> getDataList() {
        List<DemoData> dataList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            DemoData demoData = new DemoData();
            demoData.setId(Long.valueOf(i));
            demoData.setDest("dest" + i);
            demoData.setSrc("src" + i);
            demoData.setFunc("func" + i);
            demoData.setPrice(3.94);

            Map<String, String> map = new LinkedHashMap<>();
            map.put("自定义1", "12");
            map.put("自定义2", "jfak");
            map.put("自定义3", "集分宝");
            demoData.setDynamicColumnMap(map);
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
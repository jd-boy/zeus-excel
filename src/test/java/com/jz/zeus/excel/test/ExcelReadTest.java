package com.jz.zeus.excel.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.jz.zeus.excel.CellErrorInfo;
import com.jz.zeus.excel.read.listener.ExcelReadListener;
import com.jz.zeus.excel.test.data.DemoData;
import com.jz.zeus.excel.test.listener.DemoExcelReadListener;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author JZ
 * @Date 2021/4/2 11:36
 */
public class ExcelReadTest {

    private static String path = "C:\\Users\\Administrator\\Desktop\\254.xlsx";
//    private static String path = "C:\\Users\\User\\Desktop\\254.xlsx";

//    private static String path = "C:\\Users\\User\\Desktop\\2545.xlsx";
//    private static String path = "C:\\Users\\Administrator\\Desktop\\2545.xlsx";

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("解析Excel前内存："+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");
        long startTime = System.currentTimeMillis();

        ExcelReadListener readListener = new DemoExcelReadListener();
//        NoModelReadListener readListener = new TestNoModelReadListener();

//        byte[] bytes = IoUtils.toByteArray(new FileInputStream(path));
//        System.out.println("解析为字节后内存："+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");

        read(path, readListener);
//        Pair<List<DemoData>, List<CellErrorInfo>> errorRecord = readListener.getErrorRecord();
//        if (CollUtil.isNotEmpty(errorRecord.getKey())) {
//            ZeusExcel.write(path)
//                    .sheet("错误数据")
//                    .dynamicHeads(readListener.getDynamicHeads())
//                    .errorInfos(errorRecord.getValue())
//                    .doWrite(DemoData.class, errorRecord.getKey());
//        }



        System.out.println("耗时：" + (System.currentTimeMillis() - startTime) / 1000 + "s");
        System.out.println("解析Excel后内存："+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");
    }

    public static void read(String path, ReadListener readListener) {
        EasyExcel.read(path)
                .sheet((String) null)
//                .headRowNumber(1)
                .head(DemoData.class)
//                .head(getHead())
                .registerReadListener(readListener)
                .doRead();
    }

    public static List<List<String>> getHead() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("ID");
        List<String> head1 = new ArrayList<String>();
        head1.add("SRC");
        List<String> head2 = new ArrayList<String>();
        head2.add("DEST");
        List<String> head3 = new ArrayList<String>();
        head3.add("FUNC");
        list.add(head0);
        list.add(head1);
        list.add(head2);
//        list.add(head3);
        return list;
    }

}

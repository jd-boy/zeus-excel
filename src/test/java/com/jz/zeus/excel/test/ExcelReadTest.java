package com.jz.zeus.excel.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.jz.zeus.excel.read.listener.ExcelReadListener;
import com.jz.zeus.excel.read.listener.NoModelReadListener;
import com.jz.zeus.excel.test.listener.DemoExcelReadListener;
import com.jz.zeus.excel.test.listener.TestNoModelReadListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author JZ
 * @Date 2021/4/2 11:36
 */
public class ExcelReadTest {

    public static void main(String[] args) throws FileNotFoundException {
        String path = "C:\\Users\\Administrator\\Desktop\\254.xlsx";
//        String path = "C:\\Users\\User\\Desktop\\254.xlsx";

        ExcelReadListener readListener = new DemoExcelReadListener(100);
//        ExcelReadListener readListener = new NoModelReadListener();
        read(new FileInputStream(path), readListener);

        NoModelReadListener noModelReadListener = new TestNoModelReadListener();
//        ExcelUtils.read(noModelReadListener, path, "模板", 1);
    }

    public static void read(InputStream inputStream, ReadListener readListener) {
        EasyExcel.read(inputStream)
                .sheet((String) null)
//                .headRowNumber(1)
                .head(getHead())
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
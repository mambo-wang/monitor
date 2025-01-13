package com.virtual.cloud.om.sdk.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * w16051 2021/06/30
 * 修改文件内容的方法类
 */
@Slf4j
public class FileContentUtil {


    /**
     * 查询某文件中字符串第一次出现的行数，secondTargetStr为空时，返回targetStr所在行数，如果secondTargetStr不为空，返回targetStr之后离它最近的secondTargetStr所在行数
     * @param fileName 文件路径
     * @param targetStr
     * @param secondTargetStr
     * @return
     */
    public static int getLineOfInsertPos(String fileName, String targetStr, String secondTargetStr) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            //以行为单位读取文件内容，一次读一整行
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            boolean foundTarget = false;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 记录行号
                log.info("line " + line + ": " + tempString);
                if (tempString.contains(targetStr) && !foundTarget) {
                    foundTarget = true;
                    if (StringUtils.isEmpty(secondTargetStr)) {
                        break;
                    }
                }
                if (foundTarget && tempString.contains(secondTargetStr)) {
                    break;
                }
                line++;
            }
            reader.close();
            if (foundTarget) {
                return line;
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    /**
     * 在文件里面的指定行插入一行数据
     *
     * @param inFilePath
     *            文件
     * @param lineno
     *            行号
     * @param lineToBeInserted
     *            要插入的数据
     * @throws Exception
     *             IO操作引发的异常
     */
    public static void insertStringInFile(String inFilePath, int lineno,
                                          String lineToBeInserted) throws Exception {
        // 临时文件
        File outFile = File.createTempFile("name", ".tmp");

        // 输入
        File inFile = new File(inFilePath);
        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

        // 输出
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter out = new PrintWriter(fos);

        // 保存一行数据
        String thisLine;
        // 行号从1开始
        int i = 1;
        while ((thisLine = in.readLine()) != null) {
            // 如果行号等于目标行，则输出要插入的数据
            if (i == lineno) {
                out.println(lineToBeInserted);
            }
            // 输出读取到的数据
            out.println(thisLine);
            // 行号增加
            i++;
        }
        out.flush();
        out.close();
        in.close();

        // 删除原始文件
        inFile.delete();
        // 把临时文件改名为原文件名
        outFile.renameTo(inFile);

    }

    /**
     * 删除文件中包含特定字符串的行
     * @param inFilePath
     * @param lineToBeDeleted
     */
    public static void deleteLinesOfFileWithStr(String inFilePath, String lineToBeDeleted) throws Exception{
        // 临时文件
        File outFile = File.createTempFile("deleteFile", ".tmp");

        // 输入
        File inFile = new File(inFilePath);
        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

        // 输出
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter out = new PrintWriter(fos);

        // 保存一行数据
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            // 如果行号等于目标行，则输出要插入的数据
            if (thisLine.contains(lineToBeDeleted)) {
                continue;
            }
            // 输出读取到的数据
            out.println(thisLine);
        }
        out.flush();
        out.close();
        in.close();

        // 删除原始文件
        inFile.delete();
        // 把临时文件改名为原文件名
        outFile.renameTo(inFile);
    }

}

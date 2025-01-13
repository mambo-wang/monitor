package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：文件操作工具类
 *
 * @author: y17381
 * @date: 2019/6/27 13:58
 */
@Slf4j
public class FileUtil {

    public final static String OS_WINDOWS = "WINDOWS";
    public final static String OS_LINUX = "LINUX";

    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 功能描述：快速移动文件到指定目录
     *
     * @param: sourceFile 源文件路径
     * @param: destPath  目标目录
     * @return: String 移动后文件的绝对路径
     * @author: y17381
     * @date: 2019/6/5 9:49
     */
    public static String fastMoveFile(String sourceFile, String destPath) {

        Path sourceFilePath = FileSystems.getDefault().getPath(sourceFile);
        if (!sourceFilePath.toFile().exists()) {
            logger.error("move file failed, source file not exist.");
            throw new AppException(ErrorCodes.move_file_failed_source_file_not_exist);
        }
        String fileName = sourceFilePath.toFile().getName();

        if (!FileSystems.getDefault().getPath(destPath).toFile().exists() || !FileSystems.getDefault().getPath(destPath).toFile().isDirectory()) {
            logger.error("move file failed, dest path not exist.");
            throw new AppException(ErrorCodes.move_file_failed_dest_path_not_exist);
        }

        String destFile = generateFileName(destPath, fileName);
        StringBuffer cmd = new StringBuffer();
        String osName = System.getProperty("os.name", "linux").toUpperCase();
        if (osName.contains(OS_LINUX)) {
            cmd.append("mv ")
                    .append("\"").append(sourceFile).append("\"")
                    .append(" ")
                    .append("\"").append(destFile).append("\"");
            FuncUtil.runCommand(new String[]{"sh", "-c", cmd.toString()}, Integer.MAX_VALUE);
        } else if (osName.contains(OS_WINDOWS)) {
            cmd.append("move ")
                    .append("\"").append(sourceFile).append("\"")
                    .append(" ")
                    .append("\"").append(destFile).append("\"");
            FuncUtil.runCommand(new String[]{"cmd", "/c", cmd.toString()}, Integer.MAX_VALUE);
        }

        return destFile;

    }

    @SneakyThrows
    public static String copyFile(SSHHost srcHost, String sourcePath, SSHHost destHost, String destPath){
        log.info("[cluster] scp packages start");
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("sshpass -p ").append(destHost.getPassword());
        strBuffer.append(" scp -r ").append(sourcePath).append(" ");
        strBuffer.append(destHost.getUser()).append("@");
        strBuffer.append(destHost.getIp()).append(":");
        strBuffer.append(destPath);
        return SSHTools.execute(srcHost, strBuffer.toString());
    }

    /**
     * 功能描述：快速复制文件到指定目录
     *
     * @param: sourceFile 源文件路径
     * @param: destPath  目标目录
     * @return: String 移动后文件的绝对路径
     * @author: y17381
     * @date: 2019/6/5 9:49
     */
    public static String fastCopyFile(String sourceFile, String destPath) {

        Path sourceFilePath = FileSystems.getDefault().getPath(sourceFile);
        if (!sourceFilePath.toFile().exists()) {
            logger.error("copy file failed, source file not exist.");
            throw new AppException(ErrorCodes.copy_file_failed_source_file_not_exist);
        }
        String fileName = sourceFilePath.toFile().getName();

        if (!FileSystems.getDefault().getPath(destPath).toFile().exists() || !FileSystems.getDefault().getPath(destPath).toFile().isDirectory()) {
            logger.error("copy file failed, dest path not exist.");
            throw new AppException(ErrorCodes.copy_file_failed_dest_path_not_exist);
        }
        String destFile = generateFileName(destPath, fileName);
        StringBuffer cmd = new StringBuffer();
        String osName = System.getProperty("os.name", "linux").toUpperCase();
        if (osName.contains(OS_LINUX)) {
            cmd.append("cp -rf ")
                    .append("\"").append(sourceFile).append("\"")
                    .append(" ")
                    .append("\"").append(destFile).append("\"");
            FuncUtil.runCommand(new String[]{"sh", "-c", cmd.toString()}, Integer.MAX_VALUE);
        } else if (osName.contains(OS_WINDOWS)) {
            cmd.append("copy ")
                    .append("\"").append(sourceFile).append("\"")
                    .append(" ")
                    .append("\"").append(destFile).append("\"");
            FuncUtil.runCommand(new String[]{"cmd", "/c", cmd.toString()}, Integer.MAX_VALUE);
        }

        return destFile;

    }

    /**
     * 功能描述：删除目录（文件夹）
     *
     * @param: dirPath 目录绝对路径
     * @return: void
     * @author: y17381
     * @date: 2019/6/5 9:51
     */
    public static void deleteDir(String dirPath) {
        Path path = FileSystems.getDefault().getPath(dirPath);
        if (path.toFile().exists()) {
            String osName = System.getProperty("os.name", "linux").toUpperCase();
            StringBuffer cmd = new StringBuffer();
            if (osName.contains(OS_LINUX)) {
                cmd.append("rm -rf ")
                        .append("\"").append(dirPath).append("\"");
                FuncUtil.runCommand(new String[]{"sh", "-c", cmd.toString()}, Integer.MAX_VALUE);
            } else if (osName.contains(OS_WINDOWS)) {
                cmd.append("rd /s /q ")
                        .append("\"").append(dirPath).append("\"");
                FuncUtil.runCommand(new String[]{"cmd", "/c", cmd.toString()}, Integer.MAX_VALUE);
            }
        }
    }

    public static void makeParentDirs(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }


    private static String generateFileName(String destPath, String fileName) {
        Path destFilePath = FileSystems.getDefault().getPath(destPath, fileName);

        for (int i = 0; destFilePath.toFile().exists(); i++) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                String filenameWithoutSuffix = fileName.substring(0, dotIndex);
                String suffix = fileName.substring(dotIndex + 1);
                destFilePath = FileSystems.getDefault().getPath(destPath,
                        String.format("%s_%d", filenameWithoutSuffix, i) + "." + suffix);
            } else {
                destFilePath = FileSystems.getDefault().getPath(destPath,
                        String.format("%s_%d", fileName, i));
            }
        }
        return destFilePath.toFile().getAbsolutePath();
    }

    public static String getTemplatePath(String templatePath) {
        if (!templatePath.endsWith("/")) {
            templatePath += "/";
        }
        return templatePath;
    }

    /**
     * 检查指定目录是否存在。
     *
     * @param directory 目录名称。
     */
    public static boolean isExistDir(String directory) {
        File file = new File(directory);
        return file.isDirectory();
    }

    public static void execCmd(String... volues) {
        ProcessBuilder pb = new ProcessBuilder(volues);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    /**
     * tar -C /tmp/backUp/ -zcvf /tmp/vm_test.tar.gz vm_test
     */
    public static void tarcDirectory(String basePath, String srcFilePath, String srcFileName) {
        execCmd("tar", "-C", basePath, "-zcf", srcFilePath, srcFileName);
    }

    /**
     * 删除本地目录或者文件
     *
     * @param path
     */
    public static void deleteLocalFile(String path) {
        ProcessBuilder pb = new ProcessBuilder("rm", "-rf", path);
        pb.redirectErrorStream(true);
        try {
            pb.start();
        } catch (IOException e) {
            log.error(null, e);
        }
    }

    /**
     * 创建本地目录
     *
     * @param path
     */
    public static void createDir(String path) {
        File file = new File(path);
        file.mkdirs();
    }

    public static long getWrite(String file, String tmpPath, String filename, long begin, long end) {
        long endPointer = 0L;
        RandomAccessFile in = null;
        FileOutputStream out = null;
        try {
            //如果该文件存在，则删除该文件
            File targetFile = new File(tmpPath + "/" + filename);
            if (targetFile.exists()) {
                targetFile.delete();
            }

            in = new RandomAccessFile(new File(file), "r");
            out = new FileOutputStream(new File(tmpPath + "/" + filename));
            byte[] b = new byte[1024];
            int n = 0;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            endPointer = in.getFilePointer();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignroe) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignroe) {
                }
            }
        }
        return endPointer;
    }

    //清空文件路径里的文件
    public static void deletePathFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
            }
        }
    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
            }
        }
        file.delete();
    }

    /**
     * 测试指定的文件是否比指定的新
     */
    public static boolean isFileNewer(File file, Date date) {
        if (date == null) {
            throw new IllegalArgumentException("No specified date");
        }
        return isFileNewer(file, date.getTime());
    }


    public static boolean isFileNewer(File file, long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        if (!file.exists()) {
            return false;
        }
        return file.lastModified() >= timeMillis;
    }

    /**
     * 测试指定的文件是否比指定的旧
     */
    public static boolean isFileOlder(File file, Date date) {
        if (date == null) {
            throw new IllegalArgumentException("No specified date");
        }
        return isFileOlder(file, date.getTime());
    }

    public static boolean isFileOlder(File file, long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        if (!file.exists()) {
            return false;
        }
        return file.lastModified() <= timeMillis;
    }

    public static String readFileContent(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("readFileContent error.", e);
        }

        return null;
    }
}

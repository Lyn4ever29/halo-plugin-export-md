package cn.lyn4ever.export2md.util;

import cn.hutool.core.io.file.FileNameUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/11
 */
public class FileUtil {
    private static final String  ROOT_PATH="/.halo2/plugins/export2doc_files";
    public enum DirPath{
        EXPORT,
        IMPORT;
    }


    /**
     * 判断文件名是否合法
     *
     * @param name
     * @return
     */
    public static boolean isCorrectName(String name) {
        if (name == null || name.length() > 255) {
            return false;
        }
        //不能包含 /\:*?"<>|
        return !FileNameUtil.containsInvalid(name);
    }


    /**
     * 获取导出文件的路径
     *
     * @return
     */
    public static Path getDocFile(DirPath dirPath) {
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(userHome, ROOT_PATH).resolve(dirPath.name().toLowerCase());
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        return path;
    }
}

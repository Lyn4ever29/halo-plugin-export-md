package cn.lyn4ever.plugin.util;

import cn.hutool.core.io.file.FileNameUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/11
 */
public class FileUtil {


    /**
     * 判断文件名是否合法
     *
     * @param name
     * @return
     */
    public static boolean isFileNameValid(String name) {
        if (name == null || name.length() > 255) {
            return false;
        }
        String[] names = name.split("\\.");
        if (names.length != 2) {
            return false;
        }
        //不能包含 /\:*?"<>|
        return !FileNameUtil.containsInvalid(names[0]);
    }


    /**
     * 获取导出文件的路径
     * @return
     */
    public static Path getDocFile() {
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(userHome, ".halo").resolve("plugins").resolve("export2doc_files");
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        return path;
    }
}

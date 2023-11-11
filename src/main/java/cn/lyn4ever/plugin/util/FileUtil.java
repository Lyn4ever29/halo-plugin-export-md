package cn.lyn4ever.plugin.util;

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
        } else {
            return name.matches("^[a-zA-Z0-9](?:[a-zA-Z0-9 ._-]*[a-zA-Z0-9])?\\.[a-zA-Z0-9_-]+$");
        }
    }
}

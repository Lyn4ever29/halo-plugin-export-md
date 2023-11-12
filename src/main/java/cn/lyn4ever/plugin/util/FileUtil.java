package cn.lyn4ever.plugin.util;

import cn.hutool.core.io.file.FileNameUtil;

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
        return FileNameUtil.containsInvalid(names[0]);
    }


}

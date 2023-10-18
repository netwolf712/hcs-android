package com.hcs.android.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 张源 脚本生成工具
 */
public class ShellScriptGenerator {
    /**
     * 根据输入的脚本生成路径 和 命令语句 生成shell脚本
     * @param scriptPath 脚本生成路径
     * @param cmdStrings 命令语句
     * @return
     * @throws Exception
     */
    public static boolean createShellScript(String scriptPath, String... cmdStrings) throws Exception {
        if (cmdStrings == null) {
            System.out.println("strings is null");
            return false;
        }
        File sh = new File(scriptPath);
        boolean createResult = sh.createNewFile();
        if (!createResult){
            System.out.println("create failed");
            return false;
        }

        boolean executeResult = sh.setExecutable(true);
        if (!executeResult){
            System.out.println("execute failed");
            return false;
        }

        FileWriter fw = new FileWriter(sh);
        BufferedWriter bf = new BufferedWriter(fw);
        for (int i = 0; i < cmdStrings.length; i++) {
            bf.write(cmdStrings[i]);
            if (i < cmdStrings.length - 1) {
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
        return true;
    }
}

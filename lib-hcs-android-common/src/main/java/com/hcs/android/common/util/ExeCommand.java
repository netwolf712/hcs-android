package com.hcs.android.common.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;

import com.hcs.android.common.util.log.KLog;

/**
 * 执行命令的类
 * Created by Kappa
 */
public class ExeCommand {

    //shell进程
    private Process process;
    //对应进程的3个流
    private BufferedReader successResult;
    private BufferedReader errorResult;
    //是否同步，true：run会一直阻塞至完成或超时。false：run会立刻返回
    private final boolean bSynchronous;
    //表示shell进程是否还在运行
    private boolean bRunning = false;
    //使用root权限工作
    private boolean bRunAsRoot = false;
    //同步锁
    ReadWriteLock lock = new ReentrantReadWriteLock();


    //保存执行结果
    private final StringBuffer result = new StringBuffer();

    /**
     * 构造函数
     *
     * @param synchronous true：同步，false：异步
     */
    public ExeCommand(boolean synchronous) {
        bSynchronous = synchronous;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public ExeCommand() {
        bSynchronous = true;
    }

    public void setRunAsRoot(boolean runAsRoot)
    {
        this.bRunAsRoot = runAsRoot;
    }
    /**
     * 还没开始执行，和已经执行完成 这两种情况都返回false
     *
     * @return 是否正在执行
     */
    public boolean isRunning() {
        return bRunning;
    }

    /**
     * @return 返回执行结果
     */
    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return new String(result);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 以root权限运行脚本简易方法
     */
    public void runAsRoot(String command)
    {
        try {
            getRuntime().exec(new String[]{"su","-c", command});
        }catch (Exception e){
            KLog.e("Exception", e);
        }
    }
    /**
     * 执行命令
     *
     * @param command eg: cat /sdcard/test.txt
     * 路径最好不要是自己拼写的路径，最好是通过方法获取的路径
     * example：Environment.getExternalStorageDirectory()
     * @param maxTime 最大等待时间 (ms)
     * @return this
     */
    public ExeCommand run(final String command, final int maxTime) {
        if (command == null || command.length() == 0) {
            return this;
        }

        try {
            if(bRunAsRoot)
                process = getRuntime().exec("su");//看情况可能是su
            else
                process = getRuntime().exec("sh");//看情况可能是su
        } catch (Exception e) {
            return this;
        }
        bRunning = true;
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        DataOutputStream os = new DataOutputStream(process.getOutputStream());

        try {
            //向sh写入要执行的命令
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            os.close();
            //如果等待时间设置为非正，就不开启超时关闭功能
            if (maxTime > 0) {
                //超时就关闭进程
                new Thread(() -> {
                    try {
                        Thread.sleep(maxTime);
                    } catch (Exception e) {
                        KLog.e("time wait exception:" + e);
                    }
                    try {
                        process.exitValue();
                    } catch (IllegalThreadStateException e) {
                        KLog.e("take maxTime,forced to destroy process");
                        process.destroy();
                    }
                }).start();
            }

            //开一个线程来处理input流
            final Thread t1 = new Thread(() -> {
                String line;
                Lock writeLock = lock.writeLock();
                try {
                    while ((line = successResult.readLine()) != null) {
                        line += "\n";
                        writeLock.lock();
                        result.append(line);
                        writeLock.unlock();
                    }
                } catch (Exception e) {
                    KLog.e("read InputStream exception:" + e);
                } finally {
                    try {
                        successResult.close();
                    } catch (Exception e) {
                        KLog.e("close InputStream exception:" + e);
                    }
                }
            });
            t1.start();

            //开一个线程来处理error流
            final Thread t2 = new Thread(() -> {
                String line;
                Lock writeLock = lock.writeLock();
                try {
                    while ((line = errorResult.readLine()) != null) {
                        line += "\n";
                        writeLock.lock();
                        result.append(line);
                        writeLock.unlock();
                    }
                } catch (Exception e) {
                    KLog.e("read ErrorStream exception:" + e);
                } finally {
                    try {
                        errorResult.close();
                    } catch (Exception e) {
                        KLog.e("read ErrorStream exception:" + e);
                    }
                }
            });
            t2.start();

            Thread t3 = new Thread(() -> {
                try {
                    //等待执行完毕
                    t1.join();
                    t2.join();
                    process.waitFor();
                } catch (Exception e) {
                    KLog.e("run command exception:" + e);
                } finally {
                    bRunning = false;
                }
            });
            t3.start();

            if (bSynchronous) {
                t3.join();
            }
        } catch (Exception e) {
            KLog.e("run command process exception:" + e);
        }
        return this;
    }

    /**
     * 以root 执行命令
     * @return 命令执行结果
     */
    public static String executeSuCmd(String strComm, int maxTime)
    {
        String cmd = "su & " + strComm;
        ExeCommand exeCommand = new ExeCommand();
        exeCommand.setRunAsRoot(true);
        return exeCommand.run(cmd, maxTime).getResult();
    }

    /**
     * 以root 执行命令
     * @return 命令执行结果
     */
    public static String executeSuCmd(String strComm)
    {
//        String cmd = "su & " + strComm;
//        ExeCommand exeCommand = new ExeCommand();
//        exeCommand.setRunAsRoot(true);
//        return exeCommand.run(strComm, 10000).getResult();
        return executeCmd(strComm);
    }

    /**
     * 以root 执行命令
     * @return 命令执行结果
     */
    public static String executeSynSuCmd(String strComm)
    {
        String cmd = "su & " + strComm;
        ExeCommand exeCommand = new ExeCommand(false);
        exeCommand.setRunAsRoot(true);
        return exeCommand.run(cmd, 10000).getResult();
    }

    /**
     * 以普通身份执行命令
     * @return 命令执行结果
     */
    public static String executeCmd(String strComm)
    {
        ExeCommand exeCommand = new ExeCommand();
        exeCommand.setRunAsRoot(false);
        return exeCommand.run(strComm, 10000).getResult();
    }

    /**
     * 判断是否具有ROOT权限
     * @return true root ,false not root
     */
    public static boolean isRoot() {
        boolean res = false;
        try {
            res = (new File("/system/bin/su").exists()) ||
                    (new File("/system/xbin/su").exists());
        } catch (Exception e) {
            KLog.e(e);
        }
        return res;
    }
}
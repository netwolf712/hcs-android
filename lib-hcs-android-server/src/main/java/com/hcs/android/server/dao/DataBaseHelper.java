package com.hcs.android.server.dao;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.server.config.Config;
import com.hcs.android.server.entity.User;
import com.hcs.android.server.web.WebServer;
import java.util.List;

@Database(entities = {User.class}, version = 6, exportSchema = false)
public abstract class DataBaseHelper extends RoomDatabase {
    private static DataBaseHelper mInstance;
    public abstract UserDao userDao();
    private static final String DATA_BASE_NAME = "hcs-android-base";
    public static DataBaseHelper getInstance(){
        if (mInstance == null) {
            synchronized (WebServer.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(BaseApplication.getAppContext(),
                            DataBaseHelper.class, DATA_BASE_NAME)
                            .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6)//指定版本升级策略
                            .build();
                    mInstance.initDefaultData();
                }
            }

        }
        return mInstance;
    }

    public DataBaseHelper(){

    }

    /**
     * 数据初始化
     * 1、创建初始用户
     */
    public void initDefaultData(){
        if(mInstance != null) {
            //如果用户为空，则创建初始用户
            UserDao userDao = mInstance.userDao();;
            List<User> userList = userDao().getAll();
            if (userList == null || userList.size() == 0){
                //供内部访问的账号
                User innerUser = new User();
                innerUser.setUsername(Config.DEFAULT_INNER_WEB_USERNAME);
                innerUser.setPassword(Config.DEFAULT_INNER_WEB_PASSWORD);
                userDao.insert(innerUser);

                //供外部访问的账号
                User outerUser = new User();
                outerUser.setUsername(Config.DEFAULT_OUTER_WEB_USERNAME);
                outerUser.setPassword(Config.DEFAULT_OUTER_WEB_PASSWORD);
                userDao.insert(outerUser);

                //供服务器访问的账号
                User serviceUser = new User();
                serviceUser.setUsername(Config.DEFAULT_SERVICE_WEB_USERNAME);
                serviceUser.setPassword(Config.DEFAULT_SERVICE_WEB_PASSWORD);
                userDao.insert(serviceUser);
            }
        }
    }

    /**
     * 版本1-2的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
        }
    };

    /**
     * 版本2-3的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
        }
    };

    /**
     * 版本3-4的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            //sqlite不能直接修改字段属性（可以插入新的字段）
            //假如需要修改某个字段的属性，可以先整张表复制出来插入到一张新的表，再将旧的表删除，然后将新表重命名为旧表的名称
            //这里因为还没有数据，所以直接drop
            database.execSQL("DROP TABLE 'User'");
            database.execSQL("DROP TABLE 'NetConfig'");
            database.execSQL("CREATE TABLE 'User' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'username' VARCHAR(64)" +
                    ",'password' VARCHAR(64)" +
                    ",'token' VARCHAR(64))");
            database.execSQL("CREATE TABLE 'NetConfig' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'name' VARCHAR(64)" +
                    ",'netType' VARCHAR(16)" +
                    ",'linkStatus' VARCHAR(16)" +
                    ",'hardwareAddress' VARCHAR(64)" +
                    ",'ipAddress' VARCHAR(64)" +
                    ",'mask' VARCHAR(64)" +
                    ",'gateway' VARCHAR(64)" +
                    ",'gateway6' VARCHAR(64)" +
                    ",'dns1' VARCHAR(64)" +
                    ",'dns2' VARCHAR(64)" +
                    ",'ipAddress6' VARCHAR(64)" +
                    ",'mask6' VARCHAR(64)" +
                    ",'bandwidth' VARCHAR(64)" +
                    ",'linkMode' VARCHAR(64)" +
                    ",'displayName' VARCHAR(64)" +
                    ",'linkId' INTEGER)");
        }
    };

    /**
     * 版本4-5的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            //User增加了登录时间字段
            database.execSQL("ALTER TABLE 'User' ADD COLUMN 'loginTime' BIGINT");
        }
    };

    /**
     * 版本5-6的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            //删除网络配置表
            database.execSQL("DROP TABLE 'NetConfig'");
        }
    };
}


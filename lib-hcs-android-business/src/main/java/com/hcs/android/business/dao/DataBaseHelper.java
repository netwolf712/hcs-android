package com.hcs.android.business.dao;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.entity.Device;
import com.hcs.android.business.entity.Dict;
import com.hcs.android.business.entity.HandoverLog;
import com.hcs.android.business.entity.IPCamera;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.OperationLog;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.TimeSlot;


@Database(entities = {Device.class,
        Patient.class,
        Dict.class,
        OperationLog.class,
        HandoverLog.class,
        MulticastGroup.class,
        IPCamera.class,
        Place.class,
        Attachment.class,
        StepMaster.class,
        TimeSlot.class}, version = 23, exportSchema = false)
public abstract class DataBaseHelper extends RoomDatabase {
    private static DataBaseHelper mInstance;
    public abstract DeviceDao deviceDao();
    public abstract PatientDao patientDao();
    public abstract DictDao dictDao();
    public abstract OperationLogDao operationLogDao();
    public abstract HandoverLogDao handoverLogDao();
    public abstract MulticastGroupDao multicastGroupDao();
    public abstract PlaceDao placeDao();
    public abstract IPCameraDao ipCameraDao();
    public abstract AttachmentDao attachmentDao();
    public abstract StepMasterDao stepMasterDao();
    public abstract TimeSlotDao timeSlotDao();
    private static final String DATA_BASE_NAME = "hcs-android-business";

    public static DataBaseHelper getInstance(){
        if (mInstance == null) {
            synchronized (DataBaseHelper.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(BusinessApplication.getAppContext(),
                            DataBaseHelper.class, DATA_BASE_NAME)
                            .addMigrations(MIGRATION_1_2
                                    , MIGRATION_2_3
                                    , MIGRATION_3_4
                                    , MIGRATION_4_5
                                    , MIGRATION_5_6
                                    , MIGRATION_6_7
                                    , MIGRATION_7_8
                                    , MIGRATION_8_9
                                    , MIGRATION_9_10
                                    , MIGRATION_10_11
                                    , MIGRATION_11_12
                                    , MIGRATION_12_13
                                    , MIGRATION_13_14
                                    , MIGRATION_14_15
                                    , MIGRATION_15_16
                                    , MIGRATION_16_17
                                    , MIGRATION_17_18
                                    , MIGRATION_18_19
                                    , MIGRATION_19_20
                                    , MIGRATION_20_21
                                    , MIGRATION_21_22
                                    , MIGRATION_22_23)//指定版本升级策略
                            .build();
                }
            }
        }

        return mInstance;
    }

    public DataBaseHelper(){

    }

    /**
     * 版本1-2的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("CREATE TABLE 'OperationLog' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'state' INTEGER" +
                    ",'caller' TEXT" +
                    ",'caller_device_id' TEXT" +
                    ",'caller_type' INTEGER NOT NULL" +
                    ",'callee' TEXT" +
                    ",'callee_device_id' TEXT" +
                    ",'callee_type' INTEGER NOT NULL" +
                    ",'cause' TEXT" +
                    ",'type' INTEGER" +
                    ",'result' TEXT" +
                    ",'emergency' INTEGER" +
                    ",'update_time' INTEGER" +
                    ",'append_path' TEXT" +
                    ",'call_ref' TEXT" +
                    ")"
            );
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
            database.execSQL("ALTER TABLE 'Bed' ADD COLUMN 'device_id' TEXT");
            database.execSQL("ALTER TABLE 'DeviceExtend' ADD COLUMN 'master_device_id' TEXT");
            database.execSQL("ALTER TABLE 'Patient' ADD COLUMN 'master_device_id' TEXT");
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
            database.execSQL("DROP TABLE 'DeviceExtend'");
            database.execSQL("CREATE TABLE 'DeviceExtend' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'device_id' TEXT" +
                    ",'bed_sn' INTEGER" +
                    ",'master_device_id' TEXT" +
                    ",'update_time' INTEGER" +
                    ")");

            database.execSQL("DROP TABLE 'Patient'");
            database.execSQL("CREATE TABLE 'Patient' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'bed_sn' INTEGER" +
                    ",'master_device_id' TEXT" +
                    ",'name' TEXT" +
                    ",'serial_number' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'sex' INTEGER" +
                    ",'age' INTEGER" +
                    ",'in_hospital_time' INTEGER" +
                    ",'out_hospital_time' INTEGER" +
                    ",'deleted' INTEGER" +
                    ",'illness' TEXT" +
                    ",'doctor_advice' TEXT" +
                    ",'nursing_level' INTEGER" +
                    ",'critical_type' INTEGER" +
                    ",'isolate' INTEGER NOT NULL" +
                    ",'diet' INTEGER" +
                    ",'metering' INTEGER" +
                    ",'allergy' TEXT" +
                    ",'medical_insurance_type' INTEGER" +
                    ",'protection' TEXT" +
                    ",'blood_type' INTEGER" +
                    ",'doctor_name' TEXT" +
                    ",'doctor_id' INTEGER" +
                    ",'doctor_image' TEXT" +
                    ",'nurse_name' TEXT" +
                    ",'nurse_id' INTEGER" +
                    ",'nurse_image' TEXT" +
                    ")");
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
            //病房表
            database.execSQL("CREATE TABLE 'Room' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'device_id' TEXT" +
                    ",'room_sn' INTEGER" +
                    ",'room_no' INTEGER" +
                    ",'section_no' INTEGER" +
                    ",'room_name' TEXT" +
                    ",'update_time' INTEGER" +
                    ")");

            //病区表
            database.execSQL("CREATE TABLE 'Section' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'device_id' TEXT" +
                    ",'section_sn' INTEGER" +
                    ",'section_no' INTEGER" +
                    ",'section_name' TEXT" +
                    ",'update_time' INTEGER" +
                    ")");

            //交班记录表
            database.execSQL("CREATE TABLE 'HandoverLog' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'type' INTEGER" +
                    ",'user_name' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'duration' INTEGER" +
                    ",'append_path' TEXT" +
                    ",'state' INTEGER" +
                    ",'description' TEXT" +
                    ")");

            //广播配置表
            database.execSQL("CREATE TABLE 'MulticastGroup' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'type' INTEGER" +
                    ",'group_no' INTEGER" +
                    ",'play_type' INTEGER" +
                    ",'update_time' INTEGER" +
                    ",'volume' INTEGER" +
                    ",'file_list' TEXT" +
                    ")");

            //给病床、病房、病区增加分区概念
            database.execSQL("ALTER TABLE 'Bed' ADD COLUMN 'group_no' INTEGER");
            database.execSQL("ALTER TABLE 'Room' ADD COLUMN 'group_no' INTEGER");
            database.execSQL("ALTER TABLE 'Section' ADD COLUMN 'group_no' INTEGER");
        }
    };

    /**
     * 版本5-6的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //病床表
            database.execSQL("DROP TABLE 'Bed'");
            database.execSQL("CREATE TABLE 'Bed' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'bed_sn' INTEGER" +
                    ",'section_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'room_no' TEXT" +
                    ",'bed_no' TEXT" +
                    ",'group_no' TEXT" +
                    ",'place' INTEGER" +
                    ",'bed_name' TEXT" +
                    ",'device_id' TEXT" +
                    ")");

            //病房表
            database.execSQL("DROP TABLE 'Room'");
            database.execSQL("CREATE TABLE 'Room' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'room_sn' INTEGER" +
                    ",'section_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'room_no' TEXT" +
                    ",'group_no' TEXT" +
                    ",'room_name' TEXT" +
                    ",'device_id' TEXT" +
                    ")");

            //病区表
            database.execSQL("DROP TABLE 'Section'");
            database.execSQL("CREATE TABLE 'Section' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'section_sn' INTEGER" +
                    ",'section_no' TEXT" +
                    ",'group_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'section_name' TEXT" +
                    ",'device_id' TEXT" +
                    ")");

            //广播分组表
            database.execSQL("DROP TABLE 'MulticastGroup'");
            database.execSQL("CREATE TABLE 'MulticastGroup' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'group_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'play_type' INTEGER" +
                    ",'file_list' TEXT" +
                    ",'volume' INTEGER" +
                    ")");
        }
    };


    /**
     * 版本6-7的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //广播分组表
            database.execSQL("DROP TABLE 'MulticastGroup'");
            database.execSQL("CREATE TABLE 'MulticastGroup' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'group_sn' INTEGER" +
                    ",'group_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'play_type' INTEGER" +
                    ",'file_list' TEXT" +
                    ",'volume' INTEGER" +
                    ",'device_id' TEXT" +
                    ")");
        }
    };

    /**
     * 版本7-8的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_7_8 = new Migration(7,8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'Device' ADD COLUMN 'place_uid' TEXT");
            database.execSQL("CREATE TABLE 'Place' ('uid'  TEXT PRIMARY KEY NOT NULL" +
                    ",'place_sn' INTEGER" +
                    ",'place_no' TEXT" +
                    ",'group_no' TEXT" +
                    ",'update_time' INTEGER" +
                    ",'place_type' INTEGER" +
                    ",'place_name' TEXT" +
                    ",'parent_uid' TEXT" +
                    ",'master_device_id' TEXT" +
                    ")");
        }
    };

    /**
     * 版本8-9的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_8_9 = new Migration(8,9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'Device' ADD COLUMN 'last_data_command_index' INTEGER");
        }
    };

    /**
     * 版本9-10的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_9_10 = new Migration(9,10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'OperationLog' ADD COLUMN 'start_time' INTEGER");
            database.execSQL("ALTER TABLE 'OperationLog' ADD COLUMN 'stop_time' INTEGER");
        }
    };

    /**
     * 版本10-11的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_10_11 = new Migration(10,11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'OperationLog' ADD COLUMN 'connect_time' INTEGER");
        }
    };

    /**
     * 版本11-12的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_11_12 = new Migration(11,12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'OperationLog' ADD COLUMN 'caller_place_name' TEXT");
            database.execSQL("ALTER TABLE 'OperationLog' ADD COLUMN 'callee_place_name' TEXT");
        }
    };

    /**
     * 版本12-13的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_12_13 = new Migration(12,13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("DROP TABLE 'DeviceExtend'");
            database.execSQL("DROP TABLE 'Section'");
            database.execSQL("DROP TABLE 'Room'");
            database.execSQL("DROP TABLE 'Bed'");

            database.execSQL("CREATE TABLE 'IPCamera' ('uid'  INTEGER PRIMARY KEY NOT NULL" +
                    ",'bind_device_id' TEXT" +
                    ",'ip_address' TEXT" +
                    ",'onvif_port' INTEGER" +
                    ",'onvif_username' TEXT" +
                    ",'onvif_password' TEXT" +
                    ",'channel' INTEGER" +
                    ",'update_time' INTEGER" +
                    ",'default_position_x' REAL" +
                    ",'default_position_y' REAL" +
                    ",'default_zoom' REAL" +
                    ")");
        }
    };
    /**
     * 版本13-14的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_13_14 = new Migration(13,14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("DROP TABLE 'IPCamera'");

            database.execSQL("CREATE TABLE 'IPCamera' ('uid'  INTEGER PRIMARY KEY NOT NULL" +
                    ",'master_device_id' TEXT" +
                    ",'place_uid' TEXT" +
                    ",'ip_address' TEXT" +
                    ",'onvif_port' INTEGER" +
                    ",'onvif_username' TEXT" +
                    ",'onvif_password' TEXT" +
                    ",'channel' INTEGER" +
                    ",'update_time' INTEGER" +
                    ",'default_position_x' REAL" +
                    ",'default_position_y' REAL" +
                    ",'default_zoom' REAL" +
                    ")");
        }
    };

    /**
     * 版本14-15的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_14_15 = new Migration(14,15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("DROP TABLE 'IPCamera'");

            database.execSQL("CREATE TABLE 'IPCamera' ('uid'  INTEGER PRIMARY KEY" +
                    ",'master_device_id' TEXT" +
                    ",'place_uid' TEXT" +
                    ",'ip_address' TEXT" +
                    ",'onvif_port' INTEGER" +
                    ",'onvif_username' TEXT" +
                    ",'onvif_password' TEXT" +
                    ",'channel' INTEGER" +
                    ",'update_time' INTEGER" +
                    ",'default_position_x' REAL" +
                    ",'default_position_y' REAL" +
                    ",'default_zoom' REAL" +
                    ")");
        }
    };

    /**
     * 版本15-16的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_15_16 = new Migration(15,16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("CREATE TABLE 'Attachment' ('uid'  INTEGER PRIMARY KEY" +
                    ",'path' TEXT" +
                    ",'name' TEXT" +
                    ",'size' INTEGER" +
                    ",'type' TEXT" +
                    ",'use' TEXT" +
                    ",'update_time' INTEGER" +
                    ")");
        }
    };

    /**
     * 版本16-17的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_16_17 = new Migration(16,17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("CREATE TABLE 'StepMaster' ('uid'  INTEGER PRIMARY KEY" +
                    ",'master_type' INTEGER" +
                    ",'master_level' INTEGER" +
                    ",'master_no' TEXT" +
                    ",'call_wait_time' INTEGER" +
                    ",'update_time' INTEGER" +
                    ")");
            database.execSQL("CREATE TABLE 'TimeSlot' ('uid'  INTEGER PRIMARY KEY" +
                    ",'type' INTEGER" +
                    ",'period' TEXT" +
                    ",'start_time' INTEGER" +
                    ",'end_time' INTEGER" +
                    ",'update_time' INTEGER" +
                    ")");
        }
    };

    /**
     * 版本17-18的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_17_18 = new Migration(17,18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'TimeSlot' ADD COLUMN 'state' INTEGER");
        }
    };
    /**
     * 版本18-19的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_18_19 = new Migration(18,19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'Patient' ADD COLUMN 'id_card' TEXT");

            //字典表
            database.execSQL("DROP TABLE 'Dict'");
            database.execSQL("CREATE TABLE 'Dict' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'dict_type' TEXT" +
                    ",'dict_value' INTEGER" +
                    ",'display_name' TEXT" +
                    ",'sort' INTEGER" +
                    ",'background_color' INTEGER" +
                    ",'text_color' INTEGER" +
                    ",'icon' TEXT" +
                    ")");
        }
    };
    /**
     * 版本19-20的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_19_20 = new Migration(19,20) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    /**
     * 版本20-21的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_20_21 = new Migration(20,21) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //字典表
            database.execSQL("DROP TABLE 'Dict'");
            database.execSQL("CREATE TABLE 'Dict' ('uid'  INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ",'dict_type' TEXT" +
                    ",'dict_value' INTEGER" +
                    ",'display_name' TEXT" +
                    ",'sort' INTEGER" +
                    ",'background_color' INTEGER" +
                    ",'text_color' INTEGER" +
                    ",'icon' TEXT" +
                    ")");
        }
    };

    /**
     * 版本21-22的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_21_22 = new Migration(21,22) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'MulticastGroup' ADD COLUMN 'time_slot_id' INTEGER");
        }
    };

    /**
     * 版本21-22的迁移策略
     * 构造方法需传 开始版本号 与 截止版本号
     */
    static final Migration MIGRATION_22_23 = new Migration(22,23) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //版本迁移SQL语句
            database.execSQL("ALTER TABLE 'Place' ADD COLUMN 'group_sn' INTEGER");
        }
    };
}


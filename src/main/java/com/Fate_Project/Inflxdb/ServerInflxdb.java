package com.Fate_Project.Inflxdb;

import com.Fate_Project.Servicelmpl.LoginServicelmpl;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class ServerInflxdb {
    private static final Logger logger = LoggerFactory.getLogger(ServerInflxdb.class);
    private static String openurl;              //influxdb服务器的地址
    private static String username;             //influxdb服务器的账号
    private static String password;             //influxdb服务器的密码
    private static String database;             //要连接的库名
    private InfluxDB influxDB;


    /*获取参数*/
    public ServerInflxdb(String usernemr, String password, String openurl, String database){
        this.username = usernemr;
        this.password = password;
        this.openurl = openurl;
        this.database = database;
    }

    /*连接数据库*/
    public void influxdbBuild(){
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(openurl, username, password);
            logger.info("=====================(连接Influxdb数据库)========================");
            logger.info("InfluxDB数据库连接成功！");
        }

    }

    /**
     * 设置数据保存策略
     * defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1  副本个数为1/ 结尾DEFAULT 表示 设为默认的策略
     */
    public void creatRetentionPolicy(){
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "defalut", database, "30d", 1);
        this.query(command);
    }

    /**
     * 查询
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command){
        return influxDB.query(new Query(command, database));
    }

    /**
     * 插入
     * @param measurement 表
     * @param tags 标签
     * @param fields 字段
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields){
        Point.Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(database, "", builder.build());
    }

    /**
     * 删除
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command){
        QueryResult result = influxDB.query(new Query(command, database));
        return result.getError();
    }

    /**
     * 创建数据库
     * @param dbName
     */
    public void createDB(String dbName){
        influxDB.createDatabase(dbName);
    }

    /**
     * 删除数据库
     * @param dbName
     */
    public void deleteDB(String dbName){
        influxDB.deleteDatabase(dbName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenurl() {
        return openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}

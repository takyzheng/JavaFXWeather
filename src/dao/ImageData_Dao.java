package dao;

import entity.ImageData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名 ClassName  ImageData_Dao
 * 项目 ProjectName  Weather
 * 作者 Author  郑添翼 Taky.Zheng
 * 邮箱 E-mail 275158188@qq.com
 * 时间 Date  2019-06-06 16:40 ＞ω＜
 * 描述 Description TODO
 */
public class ImageData_Dao {


    private PreparedStatement ps;
    private Connection con;
    private ResultSet res;

    public ImageData_Dao(Connection con){
        this.con = con;
    }


    //查询
    public List<ImageData> select(){
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        String sql = "SELECT * FROM image_data WHERE time > ? ORDER BY fileName";
        List<ImageData> data = new ArrayList<>();
        try {
            ps = con.prepareStatement(sql);
            ps.setObject(1,localDateTime);
            res = ps.executeQuery();
            while (res.next()) {
                int id = res.getInt("id");
                String type = res.getString("type");
                String url = res.getString("url");
                Timestamp time = res.getTimestamp("time");
                String local = res.getString("local");
                String fileName = res.getString("fileName");
                data.add(new ImageData(id,type,url,time,local,fileName));
            }
        } catch (SQLException e) {
            System.err.println("查询出错!");
        }finally {
            close();
        }
        return data;
    }

    //批量插入
    public int add(List<ImageData> data){
        if (data.size() == 0) return 0;
        int count = 0; //记录成功次数

        //String sql = "INSERT INTO image_data (type,url,time) values (?,?,?)";
        String sql = "INSERT INTO image_data(type, url, time,local,fileName) SELECT ?,?,?,?,? FROM DUAL WHERE NOT EXISTS(SELECT time FROM image_data WHERE time = ?)";

        try {
            ps = con.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("获取prepareStatement失败!");
        }
        data.forEach(p -> {
                if (p.getType() == null) p.setType("无");
                try {
                    //String format = p.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    ps.setString(1,p.getType());
                    ps.setString(2,p.getUrl());
                    ps.setObject(3,p.getTime());
                    ps.setString(4,p.getLocal());
                    ps.setString(5,p.getFileName());
                    ps.setObject(6,p.getTime());
                    ps.addBatch();
                } catch (SQLException e) {
                    System.out.println("添加批失败!");
                }
            });

        try {
            int[] ints = ps.executeBatch();
            for (int anInt : ints) {
                if (anInt != 0) count++;
            }
        } catch (SQLException e) {
            System.out.println("执行批量添加失败!");
        }

        return count;
    }






    //关闭
    private void close() {
        try {
            res.close();
        } catch (SQLException e) {
            System.err.println("关闭结果集出错!");
        }

        try {
            ps.close();
        } catch (SQLException e) {
            System.err.println("关闭批处理器错误!");
        }

    }



}

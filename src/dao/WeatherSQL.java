package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 类名 ClassName  ConWeatherSQL
 * 项目 ProjectName  Weather
 * 作者 Author  郑添翼 Taky.Zheng
 * 邮箱 E-mail 275158188@qq.com
 * 时间 Date  2019-06-06 17:30 ＞ω＜
 * 描述 Description TODO
 */
public class WeatherSQL {

    private Connection con;
    //连接
    public Connection con() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Weather?connectTimeout=10000";
        String username ="root";
        String password = "123";
        this.con = DriverManager.getConnection(url, username, password);
        return this.con;
    }

    public void disconnect(){
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println("断开数据库异常!");
        }
    }

}

package entity;

import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 类名 ClassName  ImageData
 * 项目 ProjectName  Weather
 * 作者 Author  郑添翼 Taky.Zheng
 * 邮箱 E-mail 275158188@qq.com
 * 时间 Date  2019-06-06 15:56 ＞ω＜
 * 描述 Description TODO
 */
public class ImageData {

    private Integer id;
    private String type;
    private String url;
    private String local;
    private LocalDateTime time;
    private Image image;
    private Path path;
    private String fileName;


    //网络获取时调用此构造,初始化各参数
    public ImageData(String url){
        this.url = url;
        Long timestamp = Long.valueOf(getSubTime(url));
        this.fileName = timestamp + ".jpg";
        this.time = LocalDateTime.ofEpochSecond(timestamp/1000,0, ZoneOffset.ofHours(8));
    }

    //使用次构造查询
    public ImageData(Integer id, String type, String url, Date time,String local,String fileName){
        this.id = id;
        this.type = type;
        this.url = url;
        this.time = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        this.local = local;
        this.fileName = fileName;
    }



    public Path getPath() {
        if (this.path == null){
            this.path = Paths.get(local);
        }
        return this.path;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }



    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Image getImage() {
        if (this.image == null) {
            this.image = new Image("file:" + this.local);
        }
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


    public String getTimeString(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.time.format(df);
    }

    public String getTimeString(DateTimeFormatter df){
        return this.time.format(df);
    }

    private String getSubTime(String url){
        int length = url.length();
        String substring = url.substring(length - 13, length);
        return substring;
    }

}

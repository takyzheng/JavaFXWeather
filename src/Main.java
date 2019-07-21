
import dao.ImageData_Dao;
import dao.WeatherSQL;
import entity.ImageData;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * 类名 ClassName  Main
 * 项目 ProjectName  Weather
 * 作者 Author  郑添翼 Taky.Zheng
 * 邮箱 E-mail 275158188@qq.com
 * 时间 Date  2019-06-06 14:59 ＞ω＜
 * 描述 Description TODO
 */
public class Main extends Application {
    int size = 0; //记录图片总数
    int tempCount = 0; //记录当前数
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //时间格式化
    DateTimeFormatter dirDf = DateTimeFormatter.ofPattern("yyyyMMdd");
    private ImageView imageView;
    WeatherSQL weatherSQL = new WeatherSQL();
    Connection con;
    ImageData_Dao imageData_dao;
    MyTask task = new MyTask();
    PlayTask playTask = new PlayTask();
    private URL imagesResource; //文件下载路径
    ObservableList<ImageData> imageDatas; //图片源数据

    @Override
    public void start(Stage primaryStage) throws Exception {
        imagesResource = this.getClass().getResource("images/"); //获取到images文件夹路径放置图片


        //TOP操作栏
        Label state = new Label(); //用来显示状态
        Button select = new Button("点击查询");
        Button previous = new Button("上一张");
        previous.setDisable(true);
        Button next = new Button("下一张");
        next.setDisable(true);
        Button play = new Button("播放");
        play.setDisable(true);

        Button conBtn = new Button("连接数据库");
        conBtn.setVisible(false);
        HBox hBox = new HBox(10,select,previous,next,play,state);
        hBox.setAlignment(Pos.CENTER);

        //LEFT信息栏

        Button start = new Button("启动自动获取网路资源");
        TextArea textArea = new TextArea();
        textArea.setPrefWidth(200);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        VBox vBox_left = new VBox(10,start,textArea);

        //CENTER中部
        ImageView imageView = new ImageView(new Image("file:/Users/zhengtianyi/idea-workspace/Weather/out/production/Weather/images/20190610/1560120798780.jpg"));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);



        //布局
        BorderPane.setMargin(hBox,new Insets(0,0,10,0));
        BorderPane.setMargin(imageView,new Insets(0,0,0,10));
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(hBox);
        root.setLeft(vBox_left);
        root.setCenter(imageView);
        primaryStage.setScene(new Scene(root,1030,600));
        primaryStage.setResizable(false);
        primaryStage.setTitle("气象云图抓取工具1.0 Taky QQ:275158188");
        primaryStage.show();

        try {
            conBtn.setVisible(false);
            this.con = weatherSQL.con();

            textArea.setText("连接数据库成功!\r\n");
            start.setDisable(false);
        } catch (SQLException e) {
            state.setText("数据库连接失败!\r\n");
            conBtn.setVisible(true);
            return;
        }
        imageData_dao = new ImageData_Dao(con);

        primaryStage.setOnCloseRequest(p ->{
            weatherSQL.disconnect();
            System.out.println("程序关闭");
        });

        //查询事件
        select.setOnAction(p -> {
            List<ImageData> select1 = imageData_dao.select();
            int size = select1.size();
            if (size == 0 ){
                select.setText("点击查询");
                state.setText("没有查出结果!");
                next.setDisable(true);
                previous.setDisable(true);
                play.setDisable(true);
                return;
            }
            state.setText("查询出 " + size + " 条记录!");
            this.size = size;
            imageDatas = FXCollections.observableArrayList(select1);
            imageView.setImage(imageDatas.get(tempCount).getImage());
            select.setText("刷新记录");
            next.setDisable(false);
            previous.setDisable(false);
            play.setDisable(false);
        });

        next.setOnAction(p ->{
            if (playTask.isRunning()) {
                Event.fireEvent(play,new ActionEvent());
            }
            if (tempCount == size) tempCount = 0;
            state.setText(tempCount + " " + imageDatas.get(tempCount).getTimeString());
            imageView.setImage(imageDatas.get(tempCount).getImage());
            tempCount++;
        });

        previous.setOnAction(p ->{
            tempCount--;
            if (playTask.isRunning()) {
                Event.fireEvent(play,new ActionEvent());
            }
            if (tempCount <= 0) tempCount = size - 1;
            state.setText(tempCount + " " + imageDatas.get(tempCount).getTimeString());
            imageView.setImage(imageDatas.get(tempCount).getImage());

        });

        play.setOnAction(p -> {
            if (playTask.isRunning()) {
                play.setText("播放");
                playTask.cancel();
                playTask.reset();
                state.setText("播放已暂停");
            }else{
                play.setText("暂停");
                playTask.setDelay(Duration.ZERO);
                playTask.setPeriod(Duration.seconds(0.5));
                playTask.start();
            }
        });

        playTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                imageView.setImage(imageDatas.get(newValue.intValue()).getImage());
                state.setText("正在播放: 第" + (newValue.intValue() + 1) + "张");
            }
        });

        start.setOnAction(p ->{
            if (task.isRunning()) {
                start.setText("启动自动获取网路资源");
                task.cancel();
                textArea.setText(textArea.getText() + "自动获取关闭!\r\n");
                task.reset();
            }else{
                start.setText("关闭自动获取网络资源");
                task.setDelay(Duration.ZERO);
                task.setPeriod(Duration.minutes(30));
                task.start();
                textArea.setText(textArea.getText() + "自动获取开始!\r\n每30分钟获取一次!\n");
            }
        });


        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textArea.setText(textArea.getText() + newValue + "\r\n");
            }
        });



    }


    //解析HTML
    private int getImageDatas() throws IOException {
        //创建URL
        URL url = new URL("http://www.nmc.cn/publish/satellite/FY4A-true-color.htm");
        //获取DOC对象,储存的是真个HTML的内容
        Document doc = Jsoup.connect(url.toString()).get();
        //通过id选择器选择到指定标签
        Elements select = doc.select("#mycarousel");
        //将选择到的标签转换成doc
        Document parse = Jsoup.parse(select.toString());
        //再次通过标签选择器选择到所有的li子标签
        Elements lis = parse.getElementsByTag("li");
        //获取记录条数
        System.out.println("获取到 " + lis.size() + " 条记录!");
        List<ImageData> imageDatas = new ArrayList<>();
        //遍历拿去属性
        for (Element element : lis) {
            String attr = element.getElementsByAttribute("data-original").attr("data-original");
            String replace = attr.replace("/small", "");
            System.out.println(replace);
            ImageData imageData = new ImageData(replace);
            downloradImage(imageData);  //下载图片
            imageDatas.add(imageData);  //向数据库插入数据
        }

        int add = imageData_dao.add(imageDatas);
        return add;
    }

    /**
     * 方法名 MethodName downloradImage
     * 参数 Params [imageData]
     * 返回值 Return void
     * 作者 Author 郑添翼 Taky.Zheng
     * 编写时间 Date 2019-06-09 06:36 ＞ω＜
     * 描述 Description TODO 下载图片
     */
    private void downloradImage(ImageData imageData) throws IOException {
        String dirName = imagesResource.getPath() + imageData.getTime().format(dirDf);
        String file = dirName + "/" + imageData.getFileName();
        Path path = Paths.get(file);
        imageData.setLocal(path.toString()); //设置本地路径
        if (Files.notExists(path.getParent())) Files.createDirectories(path.getParent()); //判断父文件夹是否存在,不存在创建
        //下载图片
        if (Files.notExists(path)) {
            BufferedInputStream bis = new BufferedInputStream(new URL(imageData.getUrl()).openStream());
            BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(path));
            byte[] tempByte = new byte[1024];   //创建一个缓冲的字节数组
            int i = 0; //必须使用一个临时变量记录长度,主要是用于写出时写出多大,否则图片会出现失真
            while ((i = bis.read(tempByte)) != -1){
                bos.write(tempByte,0,i);
            }
            bos.flush();
            bos.close();
            bis.close();
        }

    }



    public static void main(String[] args) throws IOException{
        launch(args);
    }


    class MyTask extends ScheduledService<String> {

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    int count = getImageDatas();
                    return "执行时间: " + LocalDateTime.now().format(df) + " 成功添加 " + count + " 条记录!";
                }
            };
        }
    }


    class PlayTask extends ScheduledService<Number> {
        @Override
        protected Task<Number> createTask() {
            return new Task<Number>() {
                @Override
                protected Number call() throws Exception {
                    tempCount++;
                    if (tempCount == size) tempCount = 0;
                    return tempCount;
                }
            };
        }
    }
}


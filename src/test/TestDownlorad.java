package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 类名 ClassName  TestDownlorad
 * 项目 ProjectName  Weather
 * 作者 Author  郑添翼 Taky.Zheng
 * 邮箱 E-mail 275158188@qq.com
 * 时间 Date  2019-06-10 06:33 ＞ω＜
 * 描述 Description TODO
 */
public class TestDownlorad {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/zhengtianyi/Desktop/123.jpg");
        BufferedInputStream bis = new BufferedInputStream(new URL("http://image.nmc.cn/product/2019/06/09/WXBL/small/SEVP_NSMC_WXBL_FY4A_ETCC_ACHN_LNO_PY_20190609215300000.JPG?v=1560118809772").openStream());
        BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(path));
        byte[] tempByte = new byte[1024];
        while (bis.read(tempByte) != -1){
            bos.write(tempByte);
        }
        bos.flush();
        bos.close();
        bis.close();
    }
}

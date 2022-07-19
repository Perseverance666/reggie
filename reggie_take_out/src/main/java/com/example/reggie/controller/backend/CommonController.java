package com.example.reggie.controller.backend;

import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 * @Date: 2022/6/12 15:29
 */

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //application.yml中定义文件上传位置
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){   //file的名字必须与前端Payload中Content-Disposition的name="file"一致
        log.info(file.toString());
        //file文件是一个临时文件，必须指定转存位置，否则请求结束后会自动清除
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID创建文件名字，避免文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        if(!dir.exists()){
            //如果该目录不存在,就创建目录
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //现将已上传文件读入到输入流中
            FileInputStream fis = new FileInputStream(basePath + name);
            //设置为图片格式
            response.setContentType("image/jpeg");
            //将输入流文件读取到输出流中,通过输出流将文件写回浏览器
            ServletOutputStream sos = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            //将输入流内容读取到bytes数组中
            while((len = fis.read(bytes))!= -1){
                //将bytes数组中数据写入到输出流中
                sos.write(bytes,0,len);
                sos.flush();
            }

            sos.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

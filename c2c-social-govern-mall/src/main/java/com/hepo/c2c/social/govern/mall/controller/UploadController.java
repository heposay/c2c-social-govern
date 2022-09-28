package com.hepo.c2c.social.govern.mall.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.FILE_UPLOAD_DIR;


/**
 * 文件上传控制层
 *
 * @author linhaibo
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @PostMapping("/file")
    public ResultObject<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //生成新的文件名
            String fileName = createNewFileName(originalFilename);
            //保存文件
            FileUtil.writeBytes(file.getBytes(), FILE_UPLOAD_DIR + fileName);
            log.info("文件上传成功.{}", fileName);
            return ResultObject.success("文件上传成功！文件路径名:" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败！");
        }
    }

    @PostMapping("/file/delete")
    public ResultObject<String> deleteFile(@RequestParam("fileName") String fileName) {
        File file = new File(FILE_UPLOAD_DIR + fileName);
        if (file.isDirectory()) {
            return ResultObject.error("错误的文件名称！");
        }
        FileUtil.del(file);
        return ResultObject.success("删除成功");
    }

    /**
     * 创建新的文件目录名
     *
     * @param originalFilename 原始文件名
     * @return 新目录文件名
     */
    private String createNewFileName(String originalFilename) {
        //获取后缀名
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        //生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(FILE_UPLOAD_DIR, StrUtil.format("/file/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //生成文件名
        return StrUtil.format("/file/{}/{}/{}.{}", d1, d2, name, suffix);
    }

}

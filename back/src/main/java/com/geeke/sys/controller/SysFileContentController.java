package com.geeke.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.entity.SysFileContent;
import com.geeke.sys.service.SysFileContentService;
import com.geeke.sys.service.SysFileService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 系统附件字节Controller
 * @author szy
 * @version 2021-08-26
 */
@RestController
@RequestMapping(value = "/sys/fileContent")
public class SysFileContentController extends BaseController {

	@Autowired
	private SysFileContentService sysFileContentService;

    @Autowired
    private SysFileService sysFileService;
	
    @GetMapping("/{id}")
    public void getById(@PathVariable("id") String id, HttpServletResponse response){
        SysFileContent sysFileContent = sysFileContentService.get(id);
        response.setHeader("Content-Type",sysFileContent.getFileType());
        try {
            OutputStream os = response.getOutputStream();

            os.write(sysFileContent.getFileByte());

            os.flush();

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/export/{excelId}")
    public void getExcelByid(@PathVariable("excelId") String excelId, HttpServletResponse response){
        SysFileContent sysFileContent = sysFileContentService.get(excelId);
        SysFile sysFile = sysFileService.get(excelId);
        response.setHeader("Content-Type",sysFileContent.getFileType());

        try {
            response.setContentType("application/vnd.ms-word;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String((sysFile.getName()+".xlsx").getBytes("gb2312"), "ISO8859-1"));
            OutputStream os = response.getOutputStream();

            os.write(sysFileContent.getFileByte());

            os.flush();

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
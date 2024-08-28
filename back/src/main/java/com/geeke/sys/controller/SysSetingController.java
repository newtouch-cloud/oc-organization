package com.geeke.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.entity.SysFileContent;
import com.geeke.sys.entity.SysSeting;
import com.geeke.sys.service.SysFileContentService;
import com.geeke.sys.service.SysFileService;
import com.geeke.sys.service.SysSetingService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统设置Controller
 * @author
 * @version
 */
@RestController
@RequestMapping(value = "/sys/sysSeting")
public class SysSetingController extends BaseController {

    @Autowired
    private SysSetingService sysSetingService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private SysFileContentService sysFileContentService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SysSeting entity = sysSetingService.get(id);

        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = { "list", "" })
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SysSeting> result = sysSetingService.listPage(
            searchParams.getParams(),
            searchParams.getOffset(),
            searchParams.getLimit(),
            searchParams.getOrderby()
        );

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SysSeting> result = sysSetingService.listAll(searchParams.getParams(), searchParams.getOrderby());

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SysSeting entity) {
        int rows = sysSetingService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<SysSeting> entitys) {
        List<String> ids = sysSetingService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<SysSeting> entitys) {
        List<String> ids = sysSetingService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<SysSeting> entitys) {
        int rows = sysSetingService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(
        @RequestParam("sysSeting") String sysSeting,
        @RequestParam(value = "attachments", required = false) MultipartFile[] files
    ) {
        SysSeting entity = JSONObject.parseObject(sysSeting, SysSeting.class);
        String id = sysSetingService.save(entity, files).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    /**
     * 系统设置回显图片
     *
     * @param id
     * @param response
     */
    @GetMapping("/getFile/{id}")
    public void getById(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request)
        throws Exception {
        if (id.indexOf(".") >= 0) {
            id = id.substring(0, id.lastIndexOf("."));
        }
        String modifiedSice = request.getHeader("if-modified-since");
        //日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        if (StringUtils.isNotBlank(modifiedSice)) {
            //Thu, 04 Feb 2021 07:02:25 GMT   转化日期
            //            Date modifiedSiceDate = simpleDateFormat.parse(modifiedSice);
            SysFile sysFile = sysFileService.get(id);
            if (sysFile != null) {
                //                Date updateDate = sysFile.getUpdateDate();
                if (modifiedSice.equals(sysFile.getUpdateDate())) {
                    response.setStatus(304);
                    return;
                }
            }
        }
        SysFileContent sysFileContent = sysFileContentService.get(id);
        response.setHeader("Content-Type", sysFileContent.getFileType());
        //        Date updateDate = sysFileContent.getUpdateDate();
        //        String format = simpleDateFormat.format(updateDate);
        String format = sysFileContent.getUpdateDate();
        response.addHeader("Last-Modified", format);
        try {
            OutputStream os = response.getOutputStream();

            os.write(sysFileContent.getFileByte());

            os.flush();

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/filedownload")
    public void filedownload(@RequestParam("fileId") String fileId, HttpServletResponse response, HttpServletRequest request)
        throws Exception {
        if (fileId.indexOf(".") >= 0) {
            fileId = fileId.substring(0, fileId.lastIndexOf("."));
        }
        logger.info("fileId = {}", fileId);
        String modifiedSice = request.getHeader("if-modified-since");
        //日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        if (StringUtils.isNotBlank(modifiedSice)) {
            //Thu, 04 Feb 2021 07:02:25 GMT   转化日期
            //            Date modifiedSiceDate = simpleDateFormat.parse(modifiedSice);
            SysFile sysFile = sysFileService.get(fileId);
            if (sysFile != null) {
                //                Date updateDate = sysFile.getUpdateDate();
                if (modifiedSice.equals(sysFile.getUpdateDate())) {
                    response.setStatus(304);
                    return;
                }
            }
        }
        SysFileContent sysFileContent = sysFileContentService.get(fileId);
        response.setHeader("Content-Type", sysFileContent.getFileType());
        //        Date updateDate = sysFileContent.getUpdateDate();
        //        String format = simpleDateFormat.format(updateDate);
        String format = sysFileContent.getUpdateDate();
        response.addHeader("Last-Modified", format);
        try {
            OutputStream os = response.getOutputStream();

            os.write(sysFileContent.getFileByte());

            os.flush();

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

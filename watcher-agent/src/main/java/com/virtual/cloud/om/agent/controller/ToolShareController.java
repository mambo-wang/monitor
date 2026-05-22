package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.agent.entity.ToolShareFolder;
import com.virtual.cloud.om.agent.entity.ToolShareFile;
import com.virtual.cloud.om.agent.service.ToolShareService;
import com.virtual.cloud.om.sdk.utils.JwtTokenUtil;
import com.virtual.cloud.om.sdk.dto.SysUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tool-share")
public class ToolShareController {

    @Autowired
    private ToolShareService toolShareService;

    @GetMapping("/folders")
    public Map<String, Object> getFolders(@RequestParam(required = false) Long parentId) {
        List<ToolShareFolder> folders = toolShareService.getFolders(parentId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", folders);
        return result;
    }

    @PostMapping("/folder")
    public Map<String, Object> createFolder(@RequestBody ToolShareFolder folder, HttpServletRequest request) {
        // 从 token 中获取用户 ID
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            try {
                String subject = JwtTokenUtil.getClaimsFromToken(token).getSubject();
                SysUserDTO userDTO = JwtTokenUtil.convertTokenToUser(subject);
                if (userDTO != null && userDTO.getUsername() != null) {
                    // TODO: 根据用户名查询实际用户ID，这里暂时设为 1
                    folder.setCreateUserId(1L);
                }
            } catch (Exception e) {
                // 解析失败，使用默认值
                folder.setCreateUserId(1L);
            }
        } else {
            folder.setCreateUserId(1L);
        }
        toolShareService.createFolder(folder);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @GetMapping("/files")
    public Map<String, Object> getFiles(@RequestParam(required = false) Long folderId) {
        List<ToolShareFile> files = toolShareService.getFiles(folderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", files);
        return result;
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("toolName") String toolName,
                                          @RequestParam("toolDesc") String toolDesc,
                                          @RequestParam(required = false) Long folderId) {
        ToolShareFile resultFile = toolShareService.uploadFile(file, toolName, toolDesc, folderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", resultFile);
        return result;
    }

    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        toolShareService.downloadFile(id, response);
    }
}
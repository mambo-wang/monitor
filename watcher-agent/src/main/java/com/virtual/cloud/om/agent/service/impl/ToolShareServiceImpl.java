package com.virtual.cloud.om.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.agent.entity.ToolShareFile;
import com.virtual.cloud.om.agent.entity.ToolShareFolder;
import com.virtual.cloud.om.agent.mapper.ToolShareFileMapper;
import com.virtual.cloud.om.agent.mapper.ToolShareFolderMapper;
import com.virtual.cloud.om.agent.service.ToolShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ToolShareServiceImpl implements ToolShareService {

    @Autowired
    private ToolShareFolderMapper folderMapper;

    @Autowired
    private ToolShareFileMapper fileMapper;

    @Value("${toolshare.upload.path:/data/toolshare/files}")
    private String uploadPath;

    // 获取实际可用的上传目录
    private String getRealUploadPath() {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 如果目录创建失败，使用系统临时目录
        if (!dir.exists()) {
            dir = new File(System.getProperty("java.io.tmpdir"), "toolshare");
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    @Override
    public List<ToolShareFolder> getFolders(Long parentId) {
        LambdaQueryWrapper<ToolShareFolder> wrapper = new LambdaQueryWrapper<>();
        if (parentId == null) {
            wrapper.isNull(ToolShareFolder::getParentId);
        } else {
            wrapper.eq(ToolShareFolder::getParentId, parentId);
        }
        return folderMapper.selectList(wrapper);
    }

    @Override
    public void createFolder(ToolShareFolder folder) {
        folder.setCreateTime(LocalDateTime.now());
        // createUserId 由 Controller 从 token 中获取并设置
        if (folder.getCreateUserId() == null) {
            folder.setCreateUserId(1L); // 默认值
        }
        folderMapper.insert(folder);
    }

    @Override
    public List<ToolShareFile> getFiles(Long folderId) {
        LambdaQueryWrapper<ToolShareFile> wrapper = new LambdaQueryWrapper<>();
        if (folderId == null) {
            wrapper.isNull(ToolShareFile::getFolderId);
        } else {
            wrapper.eq(ToolShareFile::getFolderId, folderId);
        }
        return fileMapper.selectList(wrapper);
    }

    @Override
    public ToolShareFile uploadFile(MultipartFile file, String toolName, String toolDesc, Long folderId) {
        // 使用实际可用的上传目录
        String realPath = getRealUploadPath();
        File uploadDir = new File(realPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        String filePath = realPath + File.separator + newFileName;

        // 保存文件
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        // 保存文件信息
        ToolShareFile toolShareFile = new ToolShareFile();
        toolShareFile.setFileName(originalFilename);
        toolShareFile.setToolName(toolName);
        toolShareFile.setToolDesc(toolDesc);
        toolShareFile.setFilePath(filePath);
        toolShareFile.setFileSize(file.getSize());
        toolShareFile.setDownloadCount(0L);
        toolShareFile.setFolderId(folderId);
        toolShareFile.setCreateUserId(1L); // TODO: 获取当前用户ID
        toolShareFile.setCreateTime(LocalDateTime.now());
        fileMapper.insert(toolShareFile);

        return toolShareFile;
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        ToolShareFile file = fileMapper.selectById(id);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        // 增加下载量
        file.setDownloadCount(file.getDownloadCount() + 1);
        fileMapper.updateById(file);

        // 输出文件
        File f = new File(file.getFilePath());
        if (!f.exists()) {
            throw new RuntimeException("文件不存在");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());

        try (InputStream in = new FileInputStream(f); OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
package com.virtual.cloud.om.agent.service;

import com.virtual.cloud.om.agent.entity.ToolShareFolder;
import com.virtual.cloud.om.agent.entity.ToolShareFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ToolShareService {
    List<ToolShareFolder> getFolders(Long parentId);
    void createFolder(ToolShareFolder folder);
    List<ToolShareFile> getFiles(Long folderId);
    ToolShareFile uploadFile(MultipartFile file, String toolName, String toolDesc, Long folderId);
    void downloadFile(Long id, HttpServletResponse response);
}
package com.virtual.cloud.om.agent.service;

import com.virtual.cloud.om.agent.entity.ToolShareFolder;
import com.virtual.cloud.om.agent.entity.ToolShareFile;
import java.util.List;

public interface ToolShareService {
    List<ToolShareFolder> getFolders(Long parentId);
    void createFolder(ToolShareFolder folder);
    List<ToolShareFile> getFiles(Long folderId);
    ToolShareFile uploadFile(Object file, String toolName, String toolDesc, Long folderId);
    void downloadFile(Long id, HttpServletResponse response);
}
package com.zyd.springbootserviceseedproject.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import java.util.List;

/**
 * 用户审批任务信息 服务层
 */
public interface IWfUserApprovalTaskInfoService extends IService<WfUserApprovalTaskInfo> {

    WfUserApprovalTaskInfo createTask(String taskId, Long recordId, String processKey,
                                       String processInstanceId, String nodeId, String nodeName,
                                       Integer nodeType, Long userId, Long deptId);

    WfUserApprovalTaskInfo getByTaskId(String taskId);

    void updateStatus(String taskId, Integer status);

    List<WfUserApprovalTaskInfo> getByProcessInstanceId(String processInstanceId);

    void deleteByProcessInstanceId(String processInstanceId);
}

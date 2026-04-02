package com.zyd.springbootserviceseedproject.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.mapper.WfUserApprovalTaskInfoMapper;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WfUserApprovalTaskInfoServiceImpl extends ServiceImpl<WfUserApprovalTaskInfoMapper, WfUserApprovalTaskInfo>
        implements IWfUserApprovalTaskInfoService {

    @Override
    public WfUserApprovalTaskInfo createTask(String taskId, Long recordId, String processKey,
                                               String processInstanceId, String nodeId, String nodeName,
                                               Integer nodeType, Long userId, Long deptId) {
        WfUserApprovalTaskInfo task = new WfUserApprovalTaskInfo();
        task.setTaskId(taskId);
        task.setRecordId(recordId);
        task.setProcessKey(processKey);
        task.setProcessInstanceId(processInstanceId);
        task.setNodeId(nodeId);
        task.setNodeName(nodeName);
        task.setNodeType(nodeType);
        task.setUserId(userId);
        task.setDeptId(deptId);
        task.setStatus(WorkFlowStatusEnums.PENDING.getCode());
        task.setApprovalApplyTime(LocalDateTime.now());
        task.setApprovalAssignedTime(LocalDateTime.now());
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setDeleteStatus(0);
        save(task);
        return task;
    }

    @Override
    public WfUserApprovalTaskInfo getByTaskId(String taskId) {
        return baseMapper.selectByTaskId(taskId);
    }

    @Override
    public void updateStatus(String taskId, Integer status) {
        WfUserApprovalTaskInfo task = getByTaskId(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setApprovalDealTime(LocalDateTime.now());
            task.setUpdateTime(LocalDateTime.now());
            updateById(task);
        }
    }

    @Override
    public List<WfUserApprovalTaskInfo> getByProcessInstanceId(String processInstanceId) {
        return baseMapper.selectByProcessInstanceId(processInstanceId);
    }

    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<WfUserApprovalTaskInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfUserApprovalTaskInfo::getProcessInstanceId, processInstanceId);
        remove(wrapper);
    }
}

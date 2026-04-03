package com.zyd.springbootserviceseedproject.workflow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyd.springbootserviceseedproject.common.core.domain.AjaxResult;
import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.workflow.domain.WfApprovalProgressInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowTaskStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfApprovalProgressInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfRecordSnapshotService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批中心接口
 */
@Slf4j
@RestController
@RequestMapping("/workflow/approval")
public class WorkflowApprovalCenterController {

    @Resource
    private IWfInstanceGroupInfoService instanceGroupInfoService;

    @Resource
    private IWfInstanceInfoService instanceInfoService;

    @Resource
    private IWfApprovalProgressInfoService approvalProgressInfoService;

    @Resource
    private IWfRecordSnapshotService recordSnapshotService;

    @Resource
    private IWfUserApprovalTaskInfoService userApprovalTaskInfoService;

    /**
     * 获取审批进度
     */
    @GetMapping("/progress/{instanceGroupId}")
    public AjaxResult getProgress(@PathVariable Long instanceGroupId) {
        WfInstanceGroupInfo group = instanceGroupInfoService.getById(instanceGroupId);
        if (group == null) {
            return AjaxResult.error("流程组不存在");
        }

        List<WfInstanceInfo> instances = instanceInfoService.getByInstanceGroupId(instanceGroupId);
        List<WfApprovalProgressInfo> progressList = approvalProgressInfoService.listByGroupId(instanceGroupId);

        Map<String, Object> result = new HashMap<>();
        result.put("group", group);
        result.put("instances", instances);
        result.put("progressList", progressList);
        return AjaxResult.success(result);
    }

    /**
     * 获取数据快照
     */
    @GetMapping("/snapshot/{processInstanceId}")
    public AjaxResult getSnapshot(@PathVariable String processInstanceId) {
        WfRecordSnapshot snapshot = recordSnapshotService.getByProcessInstanceId(processInstanceId);
        return AjaxResult.success(snapshot);
    }

    /**
     * 获取所有业务类型
     */
    @GetMapping("/bizTypes")
    public AjaxResult listBizTypes() {
        List<String> bizTypeNames = instanceGroupInfoService.listDistinctBizTypeNames();
        return AjaxResult.success(bizTypeNames);
    }

    /**
     * 查询指定流程实例的待办任务
     */
    @GetMapping("/pendingTasks/{processInstanceId}")
    public AjaxResult getPendingTasks(@PathVariable String processInstanceId) {
        List<WfUserApprovalTaskInfo> tasks = userApprovalTaskInfoService.list(
                new LambdaQueryWrapper<WfUserApprovalTaskInfo>()
                        .eq(WfUserApprovalTaskInfo::getProcessInstanceId, processInstanceId)
                        .eq(WfUserApprovalTaskInfo::getStatus, WorkFlowTaskStatusEnums.PENDING.getCode())
                        .orderByDesc(WfUserApprovalTaskInfo::getCreateTime));
        return AjaxResult.success(tasks);
    }

    /**
     * 查询当前用户的待办任务列表
     */
    @GetMapping("/myPendingTasks")
    public AjaxResult getMyPendingTasks() {
        Long userId = SecurityUtils.getUserId();
        List<WfUserApprovalTaskInfo> tasks = userApprovalTaskInfoService.list(
                new LambdaQueryWrapper<WfUserApprovalTaskInfo>()
                        .eq(WfUserApprovalTaskInfo::getUserId, userId)
                        .eq(WfUserApprovalTaskInfo::getStatus, WorkFlowTaskStatusEnums.PENDING.getCode())
                        .orderByDesc(WfUserApprovalTaskInfo::getCreateTime));
        return AjaxResult.success(tasks);
    }
}

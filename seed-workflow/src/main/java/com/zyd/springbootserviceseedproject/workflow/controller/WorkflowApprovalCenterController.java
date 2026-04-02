package com.zyd.springbootserviceseedproject.workflow.controller;

import com.zyd.springbootserviceseedproject.common.core.domain.AjaxResult;
import com.zyd.springbootserviceseedproject.workflow.domain.WfApprovalProgressInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;
import com.zyd.springbootserviceseedproject.workflow.service.IWfApprovalProgressInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfRecordSnapshotService;
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
}

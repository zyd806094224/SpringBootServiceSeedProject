package com.zyd.springbootserviceseedproject.workflow.controller;

import com.zyd.springbootserviceseedproject.common.core.domain.AjaxResult;
import com.zyd.springbootserviceseedproject.workflow.component.WorkflowComponent;
import com.zyd.springbootserviceseedproject.workflow.dto.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流流程操作接口
 */
@Slf4j
@RestController
@RequestMapping("/workflow/process")
public class WorkflowProcessController {

    @Resource
    private WorkflowComponent workflowComponent;

    /**
     * 启动流程
     */
    @PostMapping("/start")
    public AjaxResult startProcess(@RequestBody @Valid WorkflowStartRequest request) {
        WorkflowStartResult result = workflowComponent.startProcess(request);
        return AjaxResult.success(result);
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody @Valid WorkflowApproveRequest request) {
        workflowComponent.approve(request);
        return AjaxResult.success();
    }

    /**
     * 驳回
     */
    @PostMapping("/reject")
    public AjaxResult reject(@RequestBody @Valid WorkflowApproveRequest request) {
        workflowComponent.reject(request);
        return AjaxResult.success();
    }

    /**
     * 驳回至指定节点
     */
    @PostMapping("/rejectTo")
    public AjaxResult rejectTo(@RequestBody @Valid WorkflowApproveRequest request) {
        workflowComponent.rejectTo(request);
        return AjaxResult.success();
    }

    /**
     * 撤回流程
     */
    @PostMapping("/withdraw")
    public AjaxResult withdraw(@RequestBody @Valid WorkflowApproveRequest request) {
        workflowComponent.withdrawProcess(request);
        return AjaxResult.success();
    }

    /**
     * 转交任务
     */
    @PostMapping("/transfer")
    public AjaxResult transfer(@RequestBody @Valid WorkflowTransferRequest request) {
        workflowComponent.transfer(request);
        return AjaxResult.success();
    }

    /**
     * 获取审批历史
     */
    @GetMapping("/history/{processInstanceId}")
    public AjaxResult getHistory(@PathVariable String processInstanceId) {
        List<Map<String, Object>> history = workflowComponent.getProcessHistory(processInstanceId);
        return AjaxResult.success(history);
    }

    /**
     * 获取可驳回节点
     */
    @GetMapping("/rejectableNodes/{taskId}")
    public AjaxResult getRejectableNodes(@PathVariable String taskId) {
        List<Map<String, Object>> nodes = workflowComponent.getRejectableNodes(taskId);
        return AjaxResult.success(nodes);
    }
}

package com.zyd.springbootserviceseedproject.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyd.springbootserviceseedproject.workflow.domain.WfApprovalProgressInfo;
import java.util.List;

/**
 * 审批进度信息 服务层
 */
public interface IWfApprovalProgressInfoService extends IService<WfApprovalProgressInfo> {

    List<WfApprovalProgressInfo> listByGroupId(Long instanceGroupId);
}

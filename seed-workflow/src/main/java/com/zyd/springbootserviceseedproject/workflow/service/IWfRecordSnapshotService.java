package com.zyd.springbootserviceseedproject.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;

/**
 * 审批数据快照 服务层
 */
public interface IWfRecordSnapshotService extends IService<WfRecordSnapshot> {

    void createSnapshot(Long recordId, Long bizId, Integer bizType,
                        String processInstanceId, String formSchema);

    WfRecordSnapshot getByProcessInstanceId(String processInstanceId);
}

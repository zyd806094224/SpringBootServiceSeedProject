package com.zyd.springbootserviceseedproject.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import java.util.List;

/**
 * 流程实例信息 服务层
 */
public interface IWfInstanceInfoService extends IService<WfInstanceInfo> {

    WfInstanceInfo createInstance(Long recordId, Integer bizType, String processKey,
                                  Long instanceGroupId, String processInstanceId);

    WfInstanceInfo getByProcessInstanceId(String processInstanceId);

    List<WfInstanceInfo> getByInstanceGroupId(Long instanceGroupId);

    void updateToFinalStatus(String processInstanceId, Integer status);

    void deleteByInstanceGroupId(Long instanceGroupId);
}

package com.zyd.springbootserviceseedproject.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import java.util.List;

/**
 * 流程实例组信息 服务层
 */
public interface IWfInstanceGroupInfoService extends IService<WfInstanceGroupInfo> {

    List<String> listDistinctBizTypeNames();
}

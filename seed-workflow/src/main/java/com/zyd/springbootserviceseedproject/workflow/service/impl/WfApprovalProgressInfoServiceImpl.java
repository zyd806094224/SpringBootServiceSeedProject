package com.zyd.springbootserviceseedproject.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.workflow.domain.WfApprovalProgressInfo;
import com.zyd.springbootserviceseedproject.workflow.mapper.WfApprovalProgressInfoMapper;
import com.zyd.springbootserviceseedproject.workflow.service.IWfApprovalProgressInfoService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WfApprovalProgressInfoServiceImpl extends ServiceImpl<WfApprovalProgressInfoMapper, WfApprovalProgressInfo>
        implements IWfApprovalProgressInfoService {

    @Override
    public List<WfApprovalProgressInfo> listByGroupId(Long instanceGroupId) {
        return baseMapper.selectByInstanceGroupId(instanceGroupId);
    }
}

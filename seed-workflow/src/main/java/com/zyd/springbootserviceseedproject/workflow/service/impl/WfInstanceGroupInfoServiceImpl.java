package com.zyd.springbootserviceseedproject.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.mapper.WfInstanceGroupInfoMapper;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WfInstanceGroupInfoServiceImpl extends ServiceImpl<WfInstanceGroupInfoMapper, WfInstanceGroupInfo>
        implements IWfInstanceGroupInfoService {

    @Override
    public List<String> listDistinctBizTypeNames() {
        return baseMapper.selectDistinctBizTypeNames();
    }
}

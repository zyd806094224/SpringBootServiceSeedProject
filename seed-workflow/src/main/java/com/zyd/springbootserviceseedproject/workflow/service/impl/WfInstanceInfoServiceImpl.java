package com.zyd.springbootserviceseedproject.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.mapper.WfInstanceInfoMapper;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WfInstanceInfoServiceImpl extends ServiceImpl<WfInstanceInfoMapper, WfInstanceInfo>
        implements IWfInstanceInfoService {

    @Override
    public WfInstanceInfo createInstance(Long recordId, Integer bizType, String processKey,
                                          Long instanceGroupId, String processInstanceId) {
        WfInstanceInfo instance = new WfInstanceInfo();
        instance.setRecordId(recordId);
        instance.setBizType(bizType);
        instance.setProcessKey(processKey);
        instance.setInstanceGroupId(instanceGroupId);
        instance.setProcessInstanceId(processInstanceId);
        instance.setStatus(WorkFlowStatusEnums.PENDING.getCode());
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        instance.setDeleteStatus(0);
        save(instance);
        return instance;
    }

    @Override
    public WfInstanceInfo getByProcessInstanceId(String processInstanceId) {
        return baseMapper.selectByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<WfInstanceInfo> getByInstanceGroupId(Long instanceGroupId) {
        return baseMapper.selectByInstanceGroupId(instanceGroupId);
    }

    @Override
    public void updateToFinalStatus(String processInstanceId, Integer status) {
        WfInstanceInfo instance = getByProcessInstanceId(processInstanceId);
        if (instance == null) {
            return;
        }
        // 计算版本号：组内最大版本+1
        Integer maxVersion = baseMapper.getMaxVersionByGroupId(instance.getInstanceGroupId());
        instance.setVersion(maxVersion + 1);
        instance.setStatus(status);
        instance.setUpdateTime(LocalDateTime.now());
        updateById(instance);
    }

    @Override
    public void deleteByInstanceGroupId(Long instanceGroupId) {
        LambdaQueryWrapper<WfInstanceInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfInstanceInfo::getInstanceGroupId, instanceGroupId);
        remove(wrapper);
    }
}

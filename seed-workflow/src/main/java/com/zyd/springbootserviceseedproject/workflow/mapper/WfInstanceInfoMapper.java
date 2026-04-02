package com.zyd.springbootserviceseedproject.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 流程实例信息 Mapper 接口
 */
@Mapper
public interface WfInstanceInfoMapper extends BaseMapper<WfInstanceInfo> {

    default WfInstanceInfo selectByProcessInstanceId(String processInstanceId) {
        return selectOne(new LambdaQueryWrapper<WfInstanceInfo>()
                .eq(WfInstanceInfo::getProcessInstanceId, processInstanceId));
    }

    default List<WfInstanceInfo> selectByInstanceGroupId(Long instanceGroupId) {
        return selectList(new LambdaQueryWrapper<WfInstanceInfo>()
                .eq(WfInstanceInfo::getInstanceGroupId, instanceGroupId)
                .orderByAsc(WfInstanceInfo::getVersion));
    }

    default Integer getMaxVersionByGroupId(Long instanceGroupId) {
        WfInstanceInfo instance = selectOne(new LambdaQueryWrapper<WfInstanceInfo>()
                .eq(WfInstanceInfo::getInstanceGroupId, instanceGroupId)
                .orderByDesc(WfInstanceInfo::getVersion)
                .last("LIMIT 1"));
        return instance != null && instance.getVersion() != null ? instance.getVersion() : 0;
    }
}

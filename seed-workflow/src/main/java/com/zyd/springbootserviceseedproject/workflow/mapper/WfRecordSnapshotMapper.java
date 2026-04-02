package com.zyd.springbootserviceseedproject.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批数据快照 Mapper 接口
 */
@Mapper
public interface WfRecordSnapshotMapper extends BaseMapper<WfRecordSnapshot> {

    default WfRecordSnapshot selectByProcessInstanceId(String processInstanceId) {
        return selectOne(new LambdaQueryWrapper<WfRecordSnapshot>()
                .eq(WfRecordSnapshot::getProcessInstanceId, processInstanceId));
    }
}

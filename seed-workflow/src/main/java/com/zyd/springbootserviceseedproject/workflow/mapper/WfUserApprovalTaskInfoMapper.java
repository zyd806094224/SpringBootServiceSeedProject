package com.zyd.springbootserviceseedproject.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户审批任务信息 Mapper 接口
 */
@Mapper
public interface WfUserApprovalTaskInfoMapper extends BaseMapper<WfUserApprovalTaskInfo> {

    default WfUserApprovalTaskInfo selectByTaskId(String taskId) {
        return selectOne(new LambdaQueryWrapper<WfUserApprovalTaskInfo>()
                .eq(WfUserApprovalTaskInfo::getTaskId, taskId));
    }

    default List<WfUserApprovalTaskInfo> selectByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapper<WfUserApprovalTaskInfo>()
                .eq(WfUserApprovalTaskInfo::getProcessInstanceId, processInstanceId)
                .orderByAsc(WfUserApprovalTaskInfo::getApprovalAssignedTime));
    }
}

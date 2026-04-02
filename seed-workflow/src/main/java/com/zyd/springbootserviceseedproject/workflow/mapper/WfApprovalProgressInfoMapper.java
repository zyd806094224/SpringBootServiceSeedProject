package com.zyd.springbootserviceseedproject.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.workflow.domain.WfApprovalProgressInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 审批进度信息 Mapper 接口
 */
@Mapper
public interface WfApprovalProgressInfoMapper extends BaseMapper<WfApprovalProgressInfo> {

    List<WfApprovalProgressInfo> selectByInstanceGroupId(@Param("instanceGroupId") Long instanceGroupId);

    List<WfApprovalProgressInfo> selectByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}

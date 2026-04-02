package com.zyd.springbootserviceseedproject.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 流程实例组信息 Mapper 接口
 */
@Mapper
public interface WfInstanceGroupInfoMapper extends BaseMapper<WfInstanceGroupInfo> {

    /**
     * 查询所有不重复的业务类型名称
     */
    @Select("SELECT DISTINCT biz_type_name FROM wf_instance_group_info WHERE delete_status = 0 ORDER BY biz_type_name")
    List<String> selectDistinctBizTypeNames();
}

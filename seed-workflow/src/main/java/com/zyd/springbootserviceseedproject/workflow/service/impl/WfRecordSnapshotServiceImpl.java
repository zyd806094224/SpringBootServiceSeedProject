package com.zyd.springbootserviceseedproject.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;
import com.zyd.springbootserviceseedproject.workflow.mapper.WfRecordSnapshotMapper;
import com.zyd.springbootserviceseedproject.workflow.service.IWfRecordSnapshotService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WfRecordSnapshotServiceImpl extends ServiceImpl<WfRecordSnapshotMapper, WfRecordSnapshot>
        implements IWfRecordSnapshotService {

    @Override
    public void createSnapshot(Long recordId, Long bizId, Integer bizType,
                                String processInstanceId, String formSchema) {
        WfRecordSnapshot snapshot = new WfRecordSnapshot();
        snapshot.setRecordId(recordId);
        snapshot.setBizId(bizId);
        snapshot.setBizType(bizType);
        snapshot.setProcessInstanceId(processInstanceId);
        snapshot.setFormSchema(formSchema);
        snapshot.setCreateTime(LocalDateTime.now());
        snapshot.setUpdateTime(LocalDateTime.now());
        save(snapshot);
    }

    @Override
    public WfRecordSnapshot getByProcessInstanceId(String processInstanceId) {
        return baseMapper.selectByProcessInstanceId(processInstanceId);
    }
}

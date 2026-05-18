package com.zyd.springbootserviceseedproject.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldValue;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;
import com.zyd.springbootserviceseedproject.system.domain.vo.MemoInfoVo;
import com.zyd.springbootserviceseedproject.system.mapper.MemoFieldValueMapper;
import com.zyd.springbootserviceseedproject.system.mapper.MemoInfoMapper;
import com.zyd.springbootserviceseedproject.system.service.IMemoInfoService;

/**
 * 备忘录信息 服务层实现
 *
 * @author zyd
 */
@Service
public class MemoInfoServiceImpl implements IMemoInfoService
{
    @Autowired
    private MemoInfoMapper memoInfoMapper;

    @Autowired
    private MemoFieldValueMapper fieldValueMapper;

    @Override
    public MemoInfo selectMemoInfoById(Long memoId)
    {
        return memoInfoMapper.selectMemoInfoById(memoId);
    }

    @Override
    public List<MemoInfo> selectMemoInfoList(MemoInfo memoInfo)
    {
        return memoInfoMapper.selectMemoInfoList(memoInfo);
    }

    @Override
    public MemoInfoVo selectMemoInfoDetail(Long memoId)
    {
        MemoInfo memoInfo = memoInfoMapper.selectMemoInfoById(memoId);
        if (memoInfo == null)
        {
            return null;
        }

        MemoInfoVo vo = new MemoInfoVo();
        vo.setMemoInfo(memoInfo);

        List<MemoFieldValue> fieldValues = fieldValueMapper.selectFieldValueByMemoId(memoId);
        vo.setFieldValues(fieldValues);

        return vo;
    }

    @Override
    @Transactional
    public int insertMemoInfo(MemoInfoVo vo)
    {
        int rows = memoInfoMapper.insertMemoInfo(vo.getMemoInfo());

        List<MemoFieldValue> fieldValues = vo.getFieldValues();
        if (fieldValues != null && !fieldValues.isEmpty())
        {
            for (MemoFieldValue fv : fieldValues)
            {
                fv.setMemoId(vo.getMemoInfo().getMemoId());
            }
            fieldValueMapper.batchInsertFieldValue(fieldValues);
        }

        return rows;
    }

    @Override
    @Transactional
    public int updateMemoInfo(MemoInfoVo vo)
    {
        int rows = memoInfoMapper.updateMemoInfo(vo.getMemoInfo());

        Long memoId = vo.getMemoInfo().getMemoId();
        fieldValueMapper.deleteFieldValueByMemoId(memoId);

        List<MemoFieldValue> fieldValues = vo.getFieldValues();
        if (fieldValues != null && !fieldValues.isEmpty())
        {
            for (MemoFieldValue fv : fieldValues)
            {
                fv.setMemoId(memoId);
            }
            fieldValueMapper.batchInsertFieldValue(fieldValues);
        }

        return rows;
    }

    @Override
    @Transactional
    public int deleteMemoInfoById(Long memoId)
    {
        fieldValueMapper.deleteFieldValueByMemoId(memoId);
        return memoInfoMapper.deleteMemoInfoById(memoId);
    }

    @Override
    @Transactional
    public int deleteMemoInfoByIds(Long[] memoIds)
    {
        fieldValueMapper.deleteFieldValueByMemoIds(memoIds);
        return memoInfoMapper.deleteMemoInfoByIds(memoIds);
    }
}

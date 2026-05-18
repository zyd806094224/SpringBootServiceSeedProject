package com.zyd.springbootserviceseedproject.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldDef;
import com.zyd.springbootserviceseedproject.system.mapper.MemoFieldDefMapper;
import com.zyd.springbootserviceseedproject.system.service.IMemoFieldDefService;

/**
 * 备忘录字段定义 服务层实现
 *
 * @author zyd
 */
@Service
public class MemoFieldDefServiceImpl implements IMemoFieldDefService
{
    @Autowired
    private MemoFieldDefMapper fieldDefMapper;

    @Override
    public MemoFieldDef selectFieldDefById(Long fieldId)
    {
        return fieldDefMapper.selectFieldDefById(fieldId);
    }

    @Override
    public List<MemoFieldDef> selectFieldDefByCategoryId(Long categoryId)
    {
        return fieldDefMapper.selectFieldDefByCategoryId(categoryId);
    }

    @Override
    public List<MemoFieldDef> selectFieldDefList(MemoFieldDef fieldDef)
    {
        return fieldDefMapper.selectFieldDefList(fieldDef);
    }

    @Override
    public int insertFieldDef(MemoFieldDef fieldDef)
    {
        return fieldDefMapper.insertFieldDef(fieldDef);
    }

    @Override
    public int updateFieldDef(MemoFieldDef fieldDef)
    {
        return fieldDefMapper.updateFieldDef(fieldDef);
    }

    @Override
    public int deleteFieldDefById(Long fieldId)
    {
        return fieldDefMapper.deleteFieldDefById(fieldId);
    }

    /**
     * 全量替换策略保存字段定义
     */
    @Override
    @Transactional
    public int saveFieldDefs(Long categoryId, List<MemoFieldDef> fieldDefs)
    {
        fieldDefMapper.deleteFieldDefByCategoryIds(new Long[]{categoryId});

        int count = 0;
        if (fieldDefs != null && !fieldDefs.isEmpty())
        {
            for (int i = 0; i < fieldDefs.size(); i++)
            {
                MemoFieldDef fieldDef = fieldDefs.get(i);
                fieldDef.setCategoryId(categoryId);
                fieldDef.setSortOrder(i);
                count += fieldDefMapper.insertFieldDef(fieldDef);
            }
        }
        return count;
    }
}

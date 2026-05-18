package com.zyd.springbootserviceseedproject.system.mapper;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldDef;

/**
 * 备忘录字段定义 数据层
 *
 * @author zyd
 */
public interface MemoFieldDefMapper
{
    public MemoFieldDef selectFieldDefById(Long fieldId);

    public List<MemoFieldDef> selectFieldDefByCategoryId(Long categoryId);

    public List<MemoFieldDef> selectFieldDefList(MemoFieldDef fieldDef);

    public int insertFieldDef(MemoFieldDef fieldDef);

    public int updateFieldDef(MemoFieldDef fieldDef);

    public int deleteFieldDefById(Long fieldId);

    public int deleteFieldDefByIds(Long[] fieldIds);

    public int deleteFieldDefByCategoryIds(Long[] categoryIds);
}

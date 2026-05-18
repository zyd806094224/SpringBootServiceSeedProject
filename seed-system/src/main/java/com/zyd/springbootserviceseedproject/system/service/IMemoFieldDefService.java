package com.zyd.springbootserviceseedproject.system.service;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldDef;

/**
 * 备忘录字段定义 服务层
 *
 * @author zyd
 */
public interface IMemoFieldDefService
{
    public MemoFieldDef selectFieldDefById(Long fieldId);

    public List<MemoFieldDef> selectFieldDefByCategoryId(Long categoryId);

    public List<MemoFieldDef> selectFieldDefList(MemoFieldDef fieldDef);

    public int insertFieldDef(MemoFieldDef fieldDef);

    public int updateFieldDef(MemoFieldDef fieldDef);

    public int deleteFieldDefById(Long fieldId);

    public int saveFieldDefs(Long categoryId, List<MemoFieldDef> fieldDefs);
}

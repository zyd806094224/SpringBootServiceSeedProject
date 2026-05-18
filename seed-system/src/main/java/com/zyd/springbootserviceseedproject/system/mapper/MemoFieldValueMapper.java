package com.zyd.springbootserviceseedproject.system.mapper;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldValue;

/**
 * 备忘录字段值 数据层
 *
 * @author zyd
 */
public interface MemoFieldValueMapper
{
    public List<MemoFieldValue> selectFieldValueByMemoId(Long memoId);

    public int insertFieldValue(MemoFieldValue fieldValue);

    public int batchInsertFieldValue(List<MemoFieldValue> list);

    public int deleteFieldValueByMemoId(Long memoId);

    public int deleteFieldValueByMemoIds(Long[] memoIds);
}

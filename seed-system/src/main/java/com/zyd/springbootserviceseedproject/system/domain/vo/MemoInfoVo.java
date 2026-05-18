package com.zyd.springbootserviceseedproject.system.domain.vo;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldValue;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;

/**
 * 备忘录详情VO（包含字段值）
 *
 * @author zyd
 */
public class MemoInfoVo
{
    /** 备忘录基本信息 */
    private MemoInfo memoInfo;

    /** 字段值列表 */
    private List<MemoFieldValue> fieldValues;

    public MemoInfo getMemoInfo()
    {
        return memoInfo;
    }

    public void setMemoInfo(MemoInfo memoInfo)
    {
        this.memoInfo = memoInfo;
    }

    public List<MemoFieldValue> getFieldValues()
    {
        return fieldValues;
    }

    public void setFieldValues(List<MemoFieldValue> fieldValues)
    {
        this.fieldValues = fieldValues;
    }
}

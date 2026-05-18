package com.zyd.springbootserviceseedproject.system.mapper;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;

/**
 * 备忘录信息 数据层
 *
 * @author zyd
 */
public interface MemoInfoMapper
{
    public MemoInfo selectMemoInfoById(Long memoId);

    public List<MemoInfo> selectMemoInfoList(MemoInfo memoInfo);

    public int insertMemoInfo(MemoInfo memoInfo);

    public int updateMemoInfo(MemoInfo memoInfo);

    public int deleteMemoInfoById(Long memoId);

    public int deleteMemoInfoByIds(Long[] memoIds);

    public int deleteMemoInfoByCategoryIds(Long[] categoryIds);
}

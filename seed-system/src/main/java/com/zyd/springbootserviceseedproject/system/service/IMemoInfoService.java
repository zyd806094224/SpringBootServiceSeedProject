package com.zyd.springbootserviceseedproject.system.service;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;
import com.zyd.springbootserviceseedproject.system.domain.vo.MemoInfoVo;

/**
 * 备忘录信息 服务层
 *
 * @author zyd
 */
public interface IMemoInfoService
{
    public MemoInfo selectMemoInfoById(Long memoId);

    public List<MemoInfo> selectMemoInfoList(MemoInfo memoInfo);

    public MemoInfoVo selectMemoInfoDetail(Long memoId);

    public int insertMemoInfo(MemoInfoVo vo);

    public int updateMemoInfo(MemoInfoVo vo);

    public int deleteMemoInfoById(Long memoId);

    public int deleteMemoInfoByIds(Long[] memoIds);
}

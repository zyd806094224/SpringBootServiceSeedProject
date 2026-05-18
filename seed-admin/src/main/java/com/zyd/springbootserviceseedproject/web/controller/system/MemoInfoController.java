package com.zyd.springbootserviceseedproject.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zyd.springbootserviceseedproject.common.core.controller.BaseController;
import com.zyd.springbootserviceseedproject.common.core.domain.AjaxResult;
import com.zyd.springbootserviceseedproject.common.core.page.TableDataInfo;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldDef;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;
import com.zyd.springbootserviceseedproject.system.domain.vo.MemoInfoVo;
import com.zyd.springbootserviceseedproject.system.service.IMemoFieldDefService;
import com.zyd.springbootserviceseedproject.system.service.IMemoInfoService;

/**
 * 备忘录信息 信息操作处理
 *
 * @author zyd
 */
@RestController
@RequestMapping("/api/memo/info")
public class MemoInfoController extends BaseController
{
    @Autowired
    private IMemoInfoService memoInfoService;

    @Autowired
    private IMemoFieldDefService fieldDefService;

    @GetMapping("/list")
    public TableDataInfo list(MemoInfo memoInfo)
    {
        memoInfo.setUserId(getUserId());
        startPage();
        List<MemoInfo> list = memoInfoService.selectMemoInfoList(memoInfo);
        return getDataTable(list);
    }

    @GetMapping(value = "/{memoId}")
    public AjaxResult getInfo(@PathVariable Long memoId)
    {
        MemoInfoVo vo = memoInfoService.selectMemoInfoDetail(memoId);
        if (vo == null || !vo.getMemoInfo().getUserId().equals(getUserId()))
        {
            return error("备忘录不存在");
        }

        List<MemoFieldDef> fieldDefs = fieldDefService.selectFieldDefByCategoryId(vo.getMemoInfo().getCategoryId());
        AjaxResult ajax = success(vo);
        ajax.put("fieldDefs", fieldDefs);
        return ajax;
    }

    @PostMapping
    public AjaxResult add(@RequestBody MemoInfoVo vo)
    {
        vo.getMemoInfo().setUserId(getUserId());
        vo.getMemoInfo().setCreateBy(getUsername());
        if (vo.getFieldValues() != null)
        {
            vo.getFieldValues().forEach(fv -> fv.setCreateBy(getUsername()));
        }
        int rows = memoInfoService.insertMemoInfo(vo);
        if (rows > 0)
        {
            return success(memoInfoService.selectMemoInfoDetail(vo.getMemoInfo().getMemoId()));
        }
        return error("新增备忘录失败");
    }

    @PutMapping
    public AjaxResult edit(@RequestBody MemoInfoVo vo)
    {
        MemoInfoVo existing = memoInfoService.selectMemoInfoDetail(vo.getMemoInfo().getMemoId());
        if (existing == null || !existing.getMemoInfo().getUserId().equals(getUserId()))
        {
            return error("备忘录不存在");
        }
        vo.getMemoInfo().setUpdateBy(getUsername());
        if (vo.getFieldValues() != null)
        {
            vo.getFieldValues().forEach(fv -> fv.setCreateBy(getUsername()));
        }
        int rows = memoInfoService.updateMemoInfo(vo);
        if (rows > 0)
        {
            return success(memoInfoService.selectMemoInfoDetail(vo.getMemoInfo().getMemoId()));
        }
        return error("修改备忘录失败");
    }

    @DeleteMapping(value = "/{memoIds}")
    public AjaxResult remove(@PathVariable Long[] memoIds)
    {
        for (Long memoId : memoIds)
        {
            MemoInfo existing = memoInfoService.selectMemoInfoById(memoId);
            if (existing == null || !existing.getUserId().equals(getUserId()))
            {
                return error("备忘录不存在");
            }
        }
        return toAjax(memoInfoService.deleteMemoInfoByIds(memoIds));
    }
}

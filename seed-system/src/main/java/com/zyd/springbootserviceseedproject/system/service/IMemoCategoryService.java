package com.zyd.springbootserviceseedproject.system.service;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoCategory;

/**
 * 备忘录分类 服务层
 *
 * @author zyd
 */
public interface IMemoCategoryService
{
    public MemoCategory selectCategoryById(Long categoryId);

    public List<MemoCategory> selectCategoryList(MemoCategory category);

    public int insertCategory(MemoCategory category);

    public int updateCategory(MemoCategory category);

    public int deleteCategoryById(Long categoryId);

    public int deleteCategoryByIds(Long[] categoryIds);

    public boolean checkCategoryNameUnique(MemoCategory category);
}

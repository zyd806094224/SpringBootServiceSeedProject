package com.zyd.springbootserviceseedproject.system.mapper;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.MemoCategory;

/**
 * 备忘录分类 数据层
 *
 * @author zyd
 */
public interface MemoCategoryMapper
{
    public MemoCategory selectCategoryById(Long categoryId);

    public List<MemoCategory> selectCategoryList(MemoCategory category);

    public int insertCategory(MemoCategory category);

    public int updateCategory(MemoCategory category);

    public int deleteCategoryById(Long categoryId);

    public int deleteCategoryByIds(Long[] categoryIds);

    public MemoCategory checkCategoryNameUnique(String categoryName, Long userId);
}

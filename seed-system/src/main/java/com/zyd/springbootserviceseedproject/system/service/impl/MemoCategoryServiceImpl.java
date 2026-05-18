package com.zyd.springbootserviceseedproject.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zyd.springbootserviceseedproject.system.domain.MemoCategory;
import com.zyd.springbootserviceseedproject.system.domain.MemoInfo;
import com.zyd.springbootserviceseedproject.system.mapper.MemoCategoryMapper;
import com.zyd.springbootserviceseedproject.system.mapper.MemoFieldDefMapper;
import com.zyd.springbootserviceseedproject.system.mapper.MemoInfoMapper;
import com.zyd.springbootserviceseedproject.system.mapper.MemoFieldValueMapper;
import com.zyd.springbootserviceseedproject.system.service.IMemoCategoryService;

/**
 * 备忘录分类 服务层实现
 *
 * @author zyd
 */
@Service
public class MemoCategoryServiceImpl implements IMemoCategoryService
{
    @Autowired
    private MemoCategoryMapper categoryMapper;

    @Autowired
    private MemoFieldDefMapper fieldDefMapper;

    @Autowired
    private MemoInfoMapper memoInfoMapper;

    @Autowired
    private MemoFieldValueMapper fieldValueMapper;

    @Override
    public MemoCategory selectCategoryById(Long categoryId)
    {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public List<MemoCategory> selectCategoryList(MemoCategory category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    @Override
    public int insertCategory(MemoCategory category)
    {
        return categoryMapper.insertCategory(category);
    }

    @Override
    public int updateCategory(MemoCategory category)
    {
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int deleteCategoryById(Long categoryId)
    {
        // 级联删除：分类 → 字段定义、备忘录 → 字段值
        Long[] categoryIds = new Long[]{categoryId};

        // 1. 查询该分类下的所有备忘录，删除对应的字段值
        MemoInfo query = new MemoInfo();
        query.setCategoryId(categoryId);
        List<MemoInfo> memoList = memoInfoMapper.selectMemoInfoList(query);
        if (!memoList.isEmpty())
        {
            Long[] memoIds = memoList.stream().map(MemoInfo::getMemoId).toArray(Long[]::new);
            fieldValueMapper.deleteFieldValueByMemoIds(memoIds);
            memoInfoMapper.deleteMemoInfoByIds(memoIds);
        }

        // 2. 删除字段定义
        fieldDefMapper.deleteFieldDefByCategoryIds(categoryIds);

        // 3. 删除分类
        return categoryMapper.deleteCategoryById(categoryId);
    }

    @Override
    public int deleteCategoryByIds(Long[] categoryIds)
    {
        // 级联删除
        for (Long categoryId : categoryIds)
        {
            MemoInfo query = new MemoInfo();
            query.setCategoryId(categoryId);
            List<MemoInfo> memoList = memoInfoMapper.selectMemoInfoList(query);
            if (!memoList.isEmpty())
            {
                Long[] memoIds = memoList.stream().map(MemoInfo::getMemoId).toArray(Long[]::new);
                fieldValueMapper.deleteFieldValueByMemoIds(memoIds);
                memoInfoMapper.deleteMemoInfoByIds(memoIds);
            }
        }
        fieldDefMapper.deleteFieldDefByCategoryIds(categoryIds);
        return categoryMapper.deleteCategoryByIds(categoryIds);
    }

    @Override
    public boolean checkCategoryNameUnique(MemoCategory category)
    {
        Long categoryId = category.getCategoryId() == null ? -1L : category.getCategoryId();
        MemoCategory info = categoryMapper.checkCategoryNameUnique(category.getCategoryName(), category.getUserId());
        if (info != null && !info.getCategoryId().equals(categoryId))
        {
            return false;
        }
        return true;
    }
}

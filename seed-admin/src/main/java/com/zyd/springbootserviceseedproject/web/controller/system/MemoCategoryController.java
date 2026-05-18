package com.zyd.springbootserviceseedproject.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.zyd.springbootserviceseedproject.system.domain.MemoCategory;
import com.zyd.springbootserviceseedproject.system.domain.MemoFieldDef;
import com.zyd.springbootserviceseedproject.system.service.IMemoCategoryService;
import com.zyd.springbootserviceseedproject.system.service.IMemoFieldDefService;

/**
 * 备忘录分类 信息操作处理
 *
 * @author zyd
 */
@RestController
@RequestMapping("/api/memo/category")
public class MemoCategoryController extends BaseController
{
    @Autowired
    private IMemoCategoryService categoryService;

    @Autowired
    private IMemoFieldDefService fieldDefService;

    @GetMapping("/list")
    public AjaxResult list(MemoCategory category)
    {
        category.setUserId(getUserId());
        List<MemoCategory> list = categoryService.selectCategoryList(category);
        return success(list);
    }

    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        MemoCategory category = categoryService.selectCategoryById(categoryId);
        if (category == null || !category.getUserId().equals(getUserId()))
        {
            return error("分类不存在");
        }
        return success(category);
    }

    @PostMapping
    public AjaxResult add(@Validated @RequestBody MemoCategory category)
    {
        category.setUserId(getUserId());
        category.setCreateBy(getUsername());
        if (!categoryService.checkCategoryNameUnique(category))
        {
            return error("分类名称'" + category.getCategoryName() + "'已存在");
        }
        int rows = categoryService.insertCategory(category);
        if (rows > 0)
        {
            return success(categoryService.selectCategoryById(category.getCategoryId()));
        }
        return error("新增分类失败");
    }

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MemoCategory category)
    {
        MemoCategory existing = categoryService.selectCategoryById(category.getCategoryId());
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("分类不存在");
        }
        category.setUserId(getUserId());
        category.setUpdateBy(getUsername());
        if (!categoryService.checkCategoryNameUnique(category))
        {
            return error("分类名称'" + category.getCategoryName() + "'已存在");
        }
        int rows = categoryService.updateCategory(category);
        if (rows > 0)
        {
            return success(categoryService.selectCategoryById(category.getCategoryId()));
        }
        return error("修改分类失败");
    }

    @DeleteMapping(value = "/{categoryId}")
    public AjaxResult remove(@PathVariable Long categoryId)
    {
        MemoCategory existing = categoryService.selectCategoryById(categoryId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("分类不存在");
        }
        return toAjax(categoryService.deleteCategoryById(categoryId));
    }

    @GetMapping(value = "/{categoryId}/fields")
    public AjaxResult getFieldDefs(@PathVariable Long categoryId)
    {
        MemoCategory existing = categoryService.selectCategoryById(categoryId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("分类不存在");
        }
        List<MemoFieldDef> list = fieldDefService.selectFieldDefByCategoryId(categoryId);
        return success(list);
    }

    @PutMapping(value = "/{categoryId}/fields")
    public AjaxResult saveFieldDefs(@PathVariable Long categoryId, @RequestBody List<MemoFieldDef> fieldDefs)
    {
        MemoCategory existing = categoryService.selectCategoryById(categoryId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("分类不存在");
        }
        for (MemoFieldDef fd : fieldDefs)
        {
            fd.setCreateBy(getUsername());
        }
        fieldDefService.saveFieldDefs(categoryId, fieldDefs);
        return success(fieldDefService.selectFieldDefByCategoryId(categoryId));
    }
}

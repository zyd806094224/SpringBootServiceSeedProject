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
import com.zyd.springbootserviceseedproject.common.core.page.TableDataInfo;
import com.zyd.springbootserviceseedproject.system.domain.PmAccount;
import com.zyd.springbootserviceseedproject.system.service.IPmAccountService;

/**
 * 密码管理 信息操作处理
 *
 * @author zyd
 */
@RestController
@RequestMapping("/api/accounts")
public class PmAccountController extends BaseController
{
    @Autowired
    private IPmAccountService accountService;

    /**
     * 获取账号列表（支持关键字和分类筛选）
     * GET /api/accounts?keyword=xxx&category=xxx
     */
    @GetMapping("/list")
    public TableDataInfo list(PmAccount account)
    {
        account.setUserId(getUserId());
        // 将 keyword 参数映射到 title 字段进行模糊搜索
        startPage();
        List<PmAccount> list = accountService.selectAccountList(account);
        return getDataTable(list);
    }

    /**
     * 获取账号列表（兼容前端直接 GET /api/accounts）
     * 同时支持 keyword 和 category 查询参数
     */
    @GetMapping
    public TableDataInfo listAll(PmAccount account)
    {
        account.setUserId(getUserId());
        startPage();
        List<PmAccount> list = accountService.selectAccountList(account);
        return getDataTable(list);
    }

    /**
     * 获取账号详情
     * GET /api/accounts/{id}
     */
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable Long accountId)
    {
        PmAccount account = accountService.selectAccountById(accountId);
        if (account == null || !account.getUserId().equals(getUserId()))
        {
            return error("账号不存在");
        }
        return success(account);
    }

    /**
     * 新增账号
     * POST /api/accounts
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PmAccount account)
    {
        account.setUserId(getUserId());
        account.setCreateBy(getUsername());
        int rows = accountService.insertAccount(account);
        if (rows > 0)
        {
            return success(accountService.selectAccountById(account.getAccountId()));
        }
        return error("新增账号失败");
    }

    /**
     * 修改账号
     * PUT /api/accounts/{id}
     */
    @PutMapping(value = "/{accountId}")
    public AjaxResult edit(@PathVariable Long accountId, @Validated @RequestBody PmAccount account)
    {
        // 校验账号归属当前用户
        PmAccount existing = accountService.selectAccountById(accountId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("账号不存在");
        }
        account.setAccountId(accountId);
        account.setUpdateBy(getUsername());
        int rows = accountService.updateAccount(account);
        if (rows > 0)
        {
            return success(accountService.selectAccountById(accountId));
        }
        return error("修改账号失败");
    }

    /**
     * 删除账号
     * DELETE /api/accounts/{id}
     */
    @DeleteMapping(value = "/{accountId}")
    public AjaxResult remove(@PathVariable Long accountId)
    {
        // 校验账号归属当前用户
        PmAccount existing = accountService.selectAccountById(accountId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("账号不存在");
        }
        return toAjax(accountService.deleteAccountById(accountId));
    }
}

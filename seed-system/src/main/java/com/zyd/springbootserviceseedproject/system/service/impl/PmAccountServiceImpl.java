package com.zyd.springbootserviceseedproject.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zyd.springbootserviceseedproject.common.utils.AesUtils;
import com.zyd.springbootserviceseedproject.system.domain.PmAccount;
import com.zyd.springbootserviceseedproject.system.mapper.PmAccountMapper;
import com.zyd.springbootserviceseedproject.system.service.IPmAccountService;

/**
 * 密码管理账号 服务层实现
 *
 * @author zyd
 */
@Service
public class PmAccountServiceImpl implements IPmAccountService
{
    @Autowired
    private PmAccountMapper accountMapper;

    /**
     * 查询账号信息（密码解密后返回）
     */
    @Override
    public PmAccount selectAccountById(Long accountId)
    {
        PmAccount account = accountMapper.selectAccountById(accountId);
        if (account != null)
        {
            decryptPassword(account);
        }
        return account;
    }

    /**
     * 查询账号列表（密码解密后返回）
     */
    @Override
    public List<PmAccount> selectAccountList(PmAccount account)
    {
        List<PmAccount> list = accountMapper.selectAccountList(account);
        for (PmAccount item : list)
        {
            decryptPassword(item);
        }
        return list;
    }

    /**
     * 新增账号（密码加密后存储）
     */
    @Override
    public int insertAccount(PmAccount account)
    {
        encryptPassword(account);
        return accountMapper.insertAccount(account);
    }

    /**
     * 修改账号（如果密码有变更则加密后存储）
     */
    @Override
    public int updateAccount(PmAccount account)
    {
        // 如果传入了新密码，则加密；否则保留原密码
        if (account.getPassword() != null && !account.getPassword().isEmpty())
        {
            encryptPassword(account);
        }
        return accountMapper.updateAccount(account);
    }

    /**
     * 删除账号（逻辑删除）
     */
    @Override
    public int deleteAccountById(Long accountId)
    {
        return accountMapper.deleteAccountById(accountId);
    }

    /**
     * 批量删除账号（逻辑删除）
     */
    @Override
    public int deleteAccountByIds(Long[] accountIds)
    {
        return accountMapper.deleteAccountByIds(accountIds);
    }

    /**
     * 加密密码
     */
    private void encryptPassword(PmAccount account)
    {
        if (account.getPassword() != null && !account.getPassword().isEmpty())
        {
            account.setPassword(AesUtils.encrypt(account.getPassword()));
        }
    }

    /**
     * 解密密码
     */
    private void decryptPassword(PmAccount account)
    {
        if (account.getPassword() != null && !account.getPassword().isEmpty())
        {
            try
            {
                account.setPassword(AesUtils.decrypt(account.getPassword()));
            }
            catch (Exception e)
            {
                // 解密失败时保留原文（兼容历史未加密数据）
            }
        }
    }
}

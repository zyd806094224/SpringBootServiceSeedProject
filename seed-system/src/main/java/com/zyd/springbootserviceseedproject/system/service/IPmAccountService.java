package com.zyd.springbootserviceseedproject.system.service;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.PmAccount;

/**
 * 密码管理账号 服务层
 *
 * @author zyd
 */
public interface IPmAccountService
{
    /**
     * 查询账号信息
     *
     * @param accountId 账号ID
     * @return 账号信息（密码已解密）
     */
    public PmAccount selectAccountById(Long accountId);

    /**
     * 查询账号列表
     *
     * @param account 查询条件（需设置userId）
     * @return 账号集合（密码已解密）
     */
    public List<PmAccount> selectAccountList(PmAccount account);

    /**
     * 新增账号（密码加密存储）
     *
     * @param account 账号信息
     * @return 结果
     */
    public int insertAccount(PmAccount account);

    /**
     * 修改账号
     *
     * @param account 账号信息
     * @return 结果
     */
    public int updateAccount(PmAccount account);

    /**
     * 删除账号
     *
     * @param accountId 账号ID
     * @return 结果
     */
    public int deleteAccountById(Long accountId);

    /**
     * 批量删除账号
     *
     * @param accountIds 需要删除的账号ID
     * @return 结果
     */
    public int deleteAccountByIds(Long[] accountIds);
}

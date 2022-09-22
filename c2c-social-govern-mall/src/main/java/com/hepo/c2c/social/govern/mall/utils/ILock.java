package com.hepo.c2c.social.govern.mall.utils;

/**
 * Description: 加锁
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-22 10:51
 *
 * @author linhaibo
 */
public interface ILock {

    /**
     * 加锁
     *
     * @param timeout 过期时间，单位：秒
     * @return 是否成功加锁
     */
    boolean tryLock(long timeout);

    /**
     * 释放锁
     */
    void unLock();
}

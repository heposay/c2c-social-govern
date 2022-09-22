package com.hepo.c2c.social.govern.mall.utils;

/**
 * Description:  订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-22 08:17
 *
 * @author linhaibo
 */
public class OrderStatusConstants {
    /**
     * 未支付
     */
    public static final Integer UNPAY = 1;
    /**
     * 已支付
     */
    public static final Integer PAID = 2;
    /**
     * 已核销
     */
    public static final Integer CONSUMED = 3;
    /**
     * 已取消
     */
    public static final Integer CANCALED = 4;
    /**
     * 退款中
     */
    public static final Integer REFUNDING = 5;
    /**
     * 已退款
     */
    public static final Integer REFUNDED = 6;

}

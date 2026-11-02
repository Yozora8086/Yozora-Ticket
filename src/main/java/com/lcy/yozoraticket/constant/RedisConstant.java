package com.lcy.yozoraticket.constant;

public class RedisConstant {
    //登录session key
    public static final String LOGIN_SESSION_KEY = "login:session:";
    //预热演出信息key
    public static final String WARMUP_EVENT_KEY = "event:detail:";
    //预热场次所剩余的票数
    public static final String WARMUP_TICKET_PERMIT_KEY = "event:permit:";
    //用户购票
    public static final String TICKET_BUY_KEY = "ticket:buy:";
    //冻结票数
    public static final String TICKET_LOCK_KEY = "ticket:lock:";
    //订单缓存
    public static final String TICKET_ORDER_KEY = "ticket:order:";
    //订单详情缓存
    public static final String TICKET_ORDER_ITEM_KEY = "ticket:orderItem:";
    //支付记录缓存
    public static final String TICKET_PAYMENT_KEY = "ticket:payment:";
    //电子票缓存
    public static final String TICKET_ELECTRONIC_KEY = "ticket:electronic:";
}

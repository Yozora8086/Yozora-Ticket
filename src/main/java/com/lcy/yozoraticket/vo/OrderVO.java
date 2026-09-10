package com.lcy.yozoraticket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {
    //订单号
    private String orderNo;
    //总价(合计金额)
    private Integer total;
    //当前状态(0 = 待支付, 1 = 已支付, 2 = 已取消, 3 = 已超时, 4 = 已退款)
    private Byte status;
    //订单关闭时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payCloseTime;
}

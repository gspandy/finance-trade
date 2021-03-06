package org.github.finance.mall.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.github.finance.mall.express.domain.ExpressDomain;
import org.github.finance.mall.payment.domain.PaymentDomain;
import org.github.finance.mall.share.order.constance.OrderStatusEnum;

import lombok.Data;

/**
 * @author ligaofeng 2017年1月14日 下午11:42:47
 */
@Data
public class OrderDomain {

    private String                   userId;

    private String                   orderId;

    private int                      orderProductSize;

    private BigDecimal               orderAmout;

    private OrderStatusEnum          oderStatus;

    //快递费
    private BigDecimal               expressAmount;

    //快递发出时间
    private Date                     outTime;

    private List<OrderProductDomain> orderProductDomainList;

    private PaymentDomain            paymentDomain;

    private ExpressDomain            expressDomain;

}

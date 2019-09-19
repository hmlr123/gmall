package com.hmlr123.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.hmlr123.gmall.annotations.LoginRequired;
import com.hmlr123.gmall.bean.OmsOrder;
import com.hmlr123.gmall.bean.PaymentInfo;
import com.hmlr123.gmall.payment.conf.AlipayConfig;
import com.hmlr123.gmall.service.OrderService;
import com.hmlr123.gmall.service.PaymentService;
import com.hmlr123.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 支付.
 *
 * @author liwei
 * @date 2019/9/15 14:40
 */
@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;

    @Reference
    OrderService orderService;

    @Reference
    PaymentService paymentService;

    /**
     * 跳转支付界面，
     * 此处应该重新统计数据，从数据库获取
     *
     * @param outTradeNo    外部订单号
     * @param totalAmount   总价
     * @param request       请求
     * @param modelMap      数据模型
     * @return
     */
    @RequestMapping("index")
    @LoginRequired(loginSuccess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String nickname = (String) request.getAttribute("nickname");

        modelMap.put("nickname", nickname);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("outTradeNo", outTradeNo);
        return "index";
    }

    /**
     * mx.
     *
     * @param outTradeNo
     * @param totalAmount
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("mx/submit")
    @LoginRequired(loginSuccess = true)
    public String mx(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        return null;
    }

    /**
     * alipay.
     * 1. 保存支付信息
     * 2. 发送订单数据到alipay
     *    产生支付表单，等待用户支付，客户端不断请求浏览器刷新订单状态（aliPay策略）
     * 3. 延迟队列检查订单状态（请求AliPay，防止aliPay异常，导致我们的损失）
     *
     * @param outTradeNo
     * @param totalAmount
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("alipay/submit")
    @LoginRequired(loginSuccess = true)
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        //获取alipay客户端 PC端请求
//        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        //设置回调函数
//        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
//        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        //封装支付数据
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("out_trade_no", outTradeNo);
        hashMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        hashMap.put("total_amount", totalAmount);
        hashMap.put("subject", "便宜货，随便卖，嘿嘿");

//        alipayRequest.setBizContent(JSON.toJSONString(hashMap));

        String from = null;

//        try {
//            //生成请求表单
//            from = alipayClient.pageExecute(alipayRequest).getBody();
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }

        //获取订单数据
        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);

        //容错处理
        if (null == omsOrder) {
            return null;
        }
        //封装支付数据
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(omsOrder.getOrderSn());
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("窝窝头，一块钱三个，嘿嘿");
        paymentInfo.setTotalAmount(totalAmount);

        //将支付数据保存到数据库
        paymentService.savePaymentInfo(paymentInfo);

        /**
         * 由于没有aliPay 密钥 自行请求回调地址，模拟aliPay的回调请求
         *
         * 需要放在后面由于这个请求不结束，下面的请求就不会经过，请求回调还是在当前请求线程里面，导致回调修改订单状态没有订单，最好单独开个线程发起请求
         * 回调函数需要有一定的延时，避免当前新增支付数据还没生成，回调就已经启动了，无法修改订单状态
         *
         */
        Map<String, String> aliPayParam = new HashMap<>();
        aliPayParam.put("sign", "aliPay回调请求签名");
        aliPayParam.put("trade_no", UUID.randomUUID().toString());       //支付宝交易号
        aliPayParam.put("out_trade_no", outTradeNo);                     //商户订单号
        aliPayParam.put("trade_status", "TRADE_FINISHED");               //交易状态 ali的四种交易状态
        aliPayParam.put("total_amount", String.valueOf(totalAmount));    //订单金额
        aliPayParam.put("subject", "窝窝头，一块钱四个，嘿嘿");              //订单标题
        String url = "http://payment.gmall.hmlr123.com/alipay/callback/return";
        HttpclientUtil.doPost(url, aliPayParam);

        //发送延迟消息队列  用于检查支付状态，化被动为主动
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo, 5);

        return "finish";
    }


    /**
     * 支付宝回调地址.
     * 1. 修改订单状态
     * 2. 发送消息队列 通知支付已完成
     *
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("alipay/callback/return")
    public String aliPayCallBackReturn(HttpServletRequest request, ModelMap modelMap) {

        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();

        //签名认证 2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签
        if (StringUtils.isNotBlank(sign)) {
            // 验签成功
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);             //支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);  //回调请求字符串
            paymentInfo.setCallbackTime(new Date());
            // 更新用户的支付状态 服务异步执行 幂等性检查
            paymentService.updatePayment(paymentInfo);
        }

        return "finish";
    }

  /**
     * 模拟AliPay返回支付状态
     *
     * @param request
     * @return
     */
    @RequestMapping("alipay/checkStatus")
    @ResponseBody
    public String aliPayStatus(HttpServletRequest request) {
        String out_trade_no = request.getParameter("out_trade_no");

        //交易成功，等待支付，交易结束不退款，未付款交易超时关闭，或支付完成后全额退款
        String[] aliPayStatus = {"TRADE_SUCCESS", "WAIT_BUYER_PAY", "TRADE_FINISHED", "TRADE_CLOSED"};

        String aliPayStatu = aliPayStatus[new Random().nextInt(aliPayStatus.length)];
        Map<String, String> aliPayResult = new HashMap<>();
        aliPayResult.put("trade_status", aliPayStatu);
        aliPayResult.put("out_trade_no", out_trade_no);
        return JSON.toJSONString(aliPayResult);
    }

}

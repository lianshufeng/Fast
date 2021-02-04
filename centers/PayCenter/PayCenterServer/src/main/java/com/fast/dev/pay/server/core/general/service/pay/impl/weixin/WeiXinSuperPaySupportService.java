package com.fast.dev.pay.server.core.general.service.pay.impl.weixin;

import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.model.Product;
import com.fast.dev.pay.client.model.account.WeiXinAccountModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.support.weixin.WeiXinBaseOrder;
import com.fast.dev.pay.client.support.weixin.model.WeiXinAmount;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.general.conf.PayConf;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.weixin.WeiXinHelper;
import com.fast.dev.pay.server.core.general.service.pay.PaySupportService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public abstract class WeiXinSuperPaySupportService implements PaySupportService {

    @Autowired
    private PayConf payConf;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;

    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;


    @Autowired
    private UserLogHelper logHelper;

    @Override
    public String createOrderId(String serviceCode) {
        return OrderUtil.build(AccountType.WeiXinPay, serviceCode);
    }

    @Override
    public ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder) {

        //企业支付账户
        final EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(payOrder.getPayAccountId());


        //构建通用参数
        WeiXinBaseOrder parm = null;
        try {
            parm = buildParm(enterprisePayAccount, payOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultContent.build(ResultState.PaySupportBuildParameterError, PreOrderModel.builder().error(new HashMap<>() {{
                put("message", e.getMessage());
            }}).build());
        }


        // 微信支付创建订单
        logHelper.log("order_request", parm);
        ResponseModel ret = createOrder(enterprisePayAccount, parm);
        logHelper.log("order_response", ret);
        //返回的数据
        PreOrderModel preOrderModel = new PreOrderModel();

        if (ret.getCode() == 200) {
            preOrderModel.setPlatformOrder(payOrder.getOrderId());
            preOrderModel.setSupport(BeanUtil.toMap(ret.getBody()));
        } else {
            preOrderModel.setError(BeanUtil.toMap(ret.getBody()));
        }
        return ResultContent.build(ret.getCode() == 200 ? ResultState.Success : ResultState.OrderError, preOrderModel);
    }


    /**
     * 创建订单
     *
     * @return
     */
    @SneakyThrows
    protected ResponseModel createOrder(EnterprisePayAccount enterprisePayAccount, WeiXinBaseOrder baseOrder) {
        //读取请求的证书
        final WeiXinHelper weiXinHelper = this.paySupportHelperCacheManager.get(enterprisePayAccount, WeiXinHelper.class);
        //进行接口调用
        return weiXinHelper.request(baseOrder.getUrl(), true, baseOrder);
    }


    /**
     * 构建微信请求参数
     *
     * @return
     */
    public WeiXinBaseOrder buildParm(EnterprisePayAccount enterprisePayAccount, PrePayOrderModel payOrder) {

        WeiXinAccountModel weiXinAccountModel = enterprisePayAccount.readAccount();

        WeiXinBaseOrder weixinOrder = (WeiXinBaseOrder) payOrder.getBasePaySupport();
        // 公众号id
        weixinOrder.setAppid(weiXinAccountModel.getAppid());
        // 直联商户号
        weixinOrder.setMchid(weiXinAccountModel.getMchid());
        //动态的回调地址:->
        weixinOrder.setNotify_url(String.format(payConf.getWeixin().getNotifyUrl(), payOrder.getOrderId()));
        // 商品描述
        weixinOrder.setDescription(buildDescription(payOrder.getProduct()));
        // 商户订单号
        weixinOrder.setOut_trade_no(payOrder.getOrderId());
        //订单金额
        weixinOrder.setAmount(new WeiXinAmount(payOrder.getPrice().intValue()));
        // 交易结束时间
        setTimeExpire(weixinOrder);
        return weixinOrder;
    }


    /**
     * 创建商品描述
     *
     * @return
     */
    private String buildDescription(Product product) {
        if (product == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        //标题
        if (StringUtils.hasText(product.getTitle())) {
            stringBuilder.append(product.getTitle());
        }
        //备注
        if (StringUtils.hasText(product.getRemark())) {
            stringBuilder.append("-");
            stringBuilder.append(product.getRemark());
        }
        return stringBuilder.toString();
    }


    /**
     * 设置过期时间
     */
    private void setTimeExpire(WeiXinBaseOrder weixinOrder) {
        Date expireDate = new Date(this.dbHelper.getTime() + this.payConf.getTimeExpire());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
        String timeExpire = simpleDateFormat.format(expireDate) + "+08:00";
        weixinOrder.setTime_expire(timeExpire);
    }


}

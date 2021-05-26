package com.fast.dev.pay.client.service;

import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
import com.fast.dev.pay.client.model.userbank.UserBankCardModel;
import com.fast.dev.pay.client.model.userbank.UserBankPreBindCardModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户银行卡业务
 */
@FeignClient(name = "paycenterserver/manager/user/bankcard")
public interface UserBankCardService {


    /**
     * 查询企业下某个用户的所有绑定的快捷支付银行卡
     *
     * @param payAccountId
     * @param uid
     * @param pageable
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<UserBankCardModel> list(@RequestParam("payAccountId") String payAccountId, @RequestParam("uid") String uid, @PageableDefault Pageable pageable);


    /**
     * 通过企业
     * @param epId
     * @param uid
     * @param pageable
     * @return
     */
    @RequestMapping(value = "listFromEnterprise", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<UserBankCardModel> listFromEnterprise(@RequestParam("epId") String epId, @RequestParam("uid") String uid, @PageableDefault Pageable pageable);


    /**
     * 绑定用户的快捷支付
     *
     * @param userBankPreBindCardModel
     * @return
     */
    @RequestMapping(value = "preBind")
    ResultContent<String> preBind(@RequestBody UserBankPreBindCardModel userBankPreBindCardModel);


    /**
     * 绑定用户的快捷支付
     *
     * @return
     */
    @RequestMapping(value = "bind", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<String> bind(@RequestParam("preBindId") String preBindId, @RequestParam("code") String code);

    /**
     * 绑定用户的快捷支付（无短信）
     *
     * @return
     */
    @RequestMapping(value = "directBind")
    ResultContent<String> directBind(@RequestBody UserBankBindCardModel userBankBindCard);


    /**
     * 解除用户快捷支付的绑定
     *
     * @return
     */
    @RequestMapping(value = "unBind", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultState unBind(@RequestParam("id") String id);


}

package com.fast.dev.pay.server.core.general.dao.extend;

import com.fast.dev.pay.server.core.general.domain.PreUserBankCard;

public interface PreUserBankCardDaoExtend extends UserBankCardDaoExtend {

    PreUserBankCard findAndRemoveById(String preBindId);

    boolean delCard(String uid, String payAccountId, String accountNumber);
}

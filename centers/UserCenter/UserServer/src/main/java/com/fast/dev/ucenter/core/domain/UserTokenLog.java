package com.fast.dev.ucenter.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 令牌日志
 */
@Data
@Document
@AllArgsConstructor
public class UserTokenLog extends UserToken {


}

package com.fast.dev.pay.server.core.cpcn.param;

import lombok.Builder;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
public class Key {

    private PrivateKey privateKey;

    private PublicKey publicKey;
}

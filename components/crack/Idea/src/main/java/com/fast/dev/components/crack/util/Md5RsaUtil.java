package com.fast.dev.components.crack.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.RSAPrivateKeySpec;

/**
 * idea签名用的工具
 * 
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2017年12月10日
 *
 */
public class Md5RsaUtil {
	// 私钥
	private static PrivateKey privateKey;

	static {
		try {
			BigInteger private_mod = new BigInteger(
					"9616540267013058477253762977293425063379243458473593816900454019721117570003248808113992652836857529658675570356835067184715201230519907361653795328462699");
			BigInteger private_exp = new BigInteger(
					"4802033916387221748426181350914821072434641827090144975386182740274856853318276518446521844642275539818092186650425384826827514552122318308590929813048801");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(private_mod, private_exp);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据签名
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] Sign(byte[] data) throws Exception {
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

}

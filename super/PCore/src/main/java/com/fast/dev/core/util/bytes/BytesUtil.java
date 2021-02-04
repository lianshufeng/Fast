package com.fast.dev.core.util.bytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @功能：字节集工具类
 * @作者：练书锋
 * @创建日期 : 2013-8-29
 */
public class BytesUtil {

	/**
	 * 填充算法,将字节集填充到8的整数倍
	 * @param bin
	 * @return
	 * @throws IOException
	 */
	public static byte[] fillCode(byte[] bin) throws IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		int n = (bin.length + 10) % 8;
		if (n != 0) {
			n = 8 - n;
		}
		// 将随即码改为数据完整度的校验
		int r = 0;
		for (int i = 0; i < bin.length; i++) {
			r = (r + (bin[i] & 0xFF)) % 256;
		}
		int first = (r & 248) | n;
		arrayOutputStream.write(first);
		arrayOutputStream.write(r);
		arrayOutputStream.write(bin);
		for (int i = 0; i < n; i++) {
			arrayOutputStream.write(r);
		}
		byte[] result = arrayOutputStream.toByteArray();
		arrayOutputStream.close();
		return result;
	}

	/**
	 * 将填充算法的数据还原
	 * @param bin
	 * @return
	 * @throws IOException
	 */
	public static byte[] unFillCode(byte[] bin) throws IOException {
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bin);
		int first = arrayInputStream.read();// 第一位
		// 校验码
		int r = arrayInputStream.read();// 第二位
		int n = (r & 248) ^ first;
		if (n < 0 || n > 14 || arrayInputStream.available() < n) {
			return null;// 长度不正确，解密失败
		}
		byte[] result = new byte[arrayInputStream.available() - n];
		arrayInputStream.read(result);
		arrayInputStream.close();
		int d = 0;
		for (int i = 0; i < result.length; i++) {
			d = (d + (result[i] & 0xFF)) % 256;
		}
		return d == r ? result : null;
	}

	/**
	 * 倒找字节集
	 * 
	 * @param bin
	 * @param queryBin
	 * @return
	 */
	public static int findLast(byte[] bin, byte[] queryBin) {
		int total = bin.length - queryBin.length + 1;
		for (int i = 0; i < total; i++) {
			int lastIndex = total - i - 1;
			byte[] target = subBytes(bin, lastIndex, queryBin.length + lastIndex);
			if (isEquery(target, queryBin)) {
				return lastIndex;
			}
		}
		return -1;
	}

	/**
	 * 从一个字节中查询出现的字节
	 * 
	 * @param bin
	 * @param queryBin
	 * @return
	 */
	public static int find(byte[] bin, byte[] queryBin) {
		for (int i = 0; i < bin.length - queryBin.length + 1; i++) {
			byte[] target = subBytes(bin, i, queryBin.length + i);
			if (isEquery(target, queryBin)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 两个字节集数组是否相等
	 * 
	 * @param bin1
	 * @param bin2
	 * @return
	 */
	public static boolean isEquery(byte[] bin1, byte[] bin2) {
		for (int i = 0; i < bin1.length; i++) {
			if (bin1[i] != bin2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 取字节集中间
	 * 
	 * @param bin
	 * @param start
	 * @param end
	 * @return
	 */
	public static byte[] subBytes(final byte[] bin, final int start, final int end) {
		if (start == 0 && end == bin.length) {
			return bin;
		}
		byte[] result = new byte[end - start];
		System.arraycopy(bin, start, result, 0, result.length);
		return result;
	}

	/**
	 * 将字节集转换为long类
	 * 
	 * @param b
	 * @return
	 */
	public static long binToLong(byte[] b) {
		long l = 0;
		for (int i = 0; i < b.length; i++) {
			long j_tmp = 1;
			for (int j = 0; j < b.length - i - 1; j++) {
				j_tmp <<= 8;
			}
			l += j_tmp * (b[i] & 0xFF);
		}
		return l;
	}

	/**
	 * 将字节集转换为int类型
	 * 
	 * @param b
	 * @return
	 */
	public static int binToInt(byte[] b) {
		return (int) binToLong(b);
	}

	/**
	 * 将字节集转换为short类型
	 * 
	 * @param b
	 * @return
	 */
	public static short binToShort(byte[] b) {
		return (short) binToLong(b);
	}

	/**
	 * 将long类转字节集
	 * 
	 * @param l
	 * @param size
	 *            保证长度
	 * @return
	 * @throws IOException
	 */
	public static byte[] longToBin(long l, int size) throws IOException {
		byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			result[i] = (byte) ((l >> ((size - 1 - i) << 3)) & 0xFF);
		}
		return result;
	}

	/**
	 * 将int类转字节集
	 * 
	 * @param
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static byte[] intToBin(int i, int size) throws IOException {
		return longToBin(i, size);
	}

	/**
	 * 将short类型转换为字节集
	 * 
	 * @param i
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public static byte[] shortToBin(short i, int size) throws IOException {
		return longToBin(i, size);
	}

	/**
	 * 将字节集转换为16进制
	 * 
	 * @param bin
	 * @return
	 */
	public static String binToHex(byte[] bin) {
		return binToHex(bin, false);
	}

	/**
	 * 将字节集转换16进制
	 * 
	 * @param bin
	 * @param format
	 *            格式化显示
	 * @return
	 */
	public static String binToHex(byte[] bin, boolean format) {
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b : bin) {
			int i = (int) b;
			if (i < 0) {
				i = i + 256;
			}
			String hex = Integer.toHexString(i);
			while (hex.length() < 2) {
				hex = "0" + hex;
			}
			stringBuffer.append(hex);
			if (format) {
				stringBuffer.append(" ");
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 十六进制转换为字节集
	 * 
	 * @param
	 * @return
	 * @throws IOException
	 */
	public static byte[] hexToBin(String str) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String hex = str;
		if (hex.length() % 2 != 0)
			hex = "0" + hex;
		for (int i = 0; i < hex.length() / 2; i++) {
			int point = i * 2;
			byteArrayOutputStream.write(Integer.parseInt(hex.substring(point, point + 2), 16));
		}
		byteArrayOutputStream.flush();
		byte[] bin = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		return bin;
	}

	/**
	 * 合并两个字节集
	 * 
	 * @param bin1
	 * @param
	 * @return
	 * @throws IOException
	 */
	public static byte[] merge(byte[] bin1, byte[]... bins) throws IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		arrayOutputStream.write(bin1);
		for (byte[] bin : bins) {
			arrayOutputStream.write(bin);
		}
		arrayOutputStream.flush();
		byte[] bin = arrayOutputStream.toByteArray();
		arrayOutputStream.close();
		return bin;
	}

	/**
	 * 对象到字节集
	 * 
	 * @param
	 * @return
	 * @throws IOException
	 */
	public static byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(object);
		oos.flush();
		byte[] bin = out.toByteArray();
		oos.close();
		out.close();
		return bin;
	}

	/**
	 * 字节集到对象
	 * 
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T bytesToObject(byte[] buffer) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		ObjectInputStream inputStream = new ObjectInputStream(in);
		Object o = inputStream.readObject();
		inputStream.close();
		in.close();
		return (T) o;
	}

}

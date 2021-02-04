package com.fast.build.helper.core.util;

/**
 * 字符串工具集合
 * 
 * @author 练书锋
 *
 */
public class TextUtil {

	/**
	 * 字符串
	 * 
	 * @param source
	 * @param startText
	 * @param endText
	 * @return
	 */
	public static String subText(String source, String startText, String endText, int offSet) {
		int start = source.indexOf(startText, offSet) + 1;
		if (start == -1) {
			return null;
		}
		int end = source.indexOf(endText, start + offSet + startText.length() - 1);
		if (end == -1) {
			end = source.length();
		}
		return source.substring(start + startText.length() - 1, end);
	}

	/**
	 * 合并字符串数组到字符串，以关键词间隔
	 * 
	 * @param arr
	 * @param keyword
	 * @return
	 */
	public static String join(String[] arr, String keyword) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length - 1; i++) {
			sb.append(arr[i]);
			sb.append(keyword);
		}
		if (arr.length > 0) {
			sb.append(arr[arr.length - 1]);
		}

		return sb.toString();
	}

}

package com.fast.build.helper.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Http client
 * 
 * @作者：练书锋 @日期：2013年10月22日
 * 
 */
public class HttpClient {

	private static final String CHARSET = "charset";

	// 连接超时时间
	private int connectTimeout = 60000;
	// 读取超时时间
	private int readTimeout = 60000;
	// cookies
	private Map<String, String> cookies = new HashMap<String, String>();
	// 设置是否自动跳转
	private boolean followRedirects = true;

	// 监听输出管道
	private OutputStream listenOutputStream;

	// 代理IP
	private String proxyHost;
	// 代理端口
	private int porxyPort;

	public OutputStream getListenOutputStream() {
		return listenOutputStream;
	}

	public HttpClient setListenOutputStream(OutputStream listenOutputStream) {
		this.listenOutputStream = listenOutputStream;
		return this;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public HttpClient setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public HttpClient setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		return this;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public HttpClient setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public HttpClient setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	public int getPorxyPort() {
		return porxyPort;
	}

	public HttpClient setPorxyPort(int porxyPort) {
		this.porxyPort = porxyPort;
		return this;
	}

	/**
	 * 默认的构造方法
	 */
	public HttpClient() {

	}

	/**
	 * 请重写该方法用于显示下载进度
	 * 
	 * @param currentPoint
	 * @param totalPoint
	 */
	public void showProgress(long currentPoint, long totalPoint) {

	}

	/**
	 * 使用get方式请求
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public byte[] get(final String url) throws IOException {
		return ReadDocuments(url, false, null).getData();
	}

	/**
	 * 使用POST请求
	 * 
	 * @param url
	 * @param bin
	 * @return
	 * @throws IOException
	 */
	public byte[] post(final String url, final byte[] bin) throws IOException {
		return ReadDocuments(url, true, bin).getData();
	}

	/**
	 * 请求网络访问
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public ResultBean ReadDocuments(String url) throws IOException {
		return ReadDocuments(url, false, null);
	}

	/**
	 * 
	 * @param url
	 * @param isPost
	 * @param
	 *            POST专用的参数
	 * @return
	 * @throws IOException
	 */
	public ResultBean ReadDocuments(String url, boolean isPost, byte[] postData) throws IOException {
		return ReadDocuments(new URL(url), isPost, postData, null, null);
	}

	/**
	 * HTTP访问网络
	 * 
	 * @param url
	 * @param isPost
	 * @param postData
	 * @param requestHead
	 * @return
	 * @throws IOException
	 */
	public ResultBean ReadDocuments(String url, boolean isPost, byte[] postData, Map<String, String> requestHead)
			throws IOException {
		return ReadDocuments(new URL(url), isPost, postData, requestHead, null);
	}

	public ResultBean ReadDocuments(final URL url, final boolean isPost, final byte[] postData,
			Map<String, String> requestHead, Boolean onlyHead) throws IOException {
		HttpURLConnection httpURLConnection = connect(url, isPost, requestHead);
		if (httpURLConnection == null) {
			return null;
		}
		// 写出参数
		if (isPost && postData != null) {
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(postData);
			outputStream.flush();
			outputStream.close();
		}
		// 设置响应与返回的响应头信息
		ResultBean resultBean = readInputStream(httpURLConnection, onlyHead);
		// 设置服务器需要设置的cookies
		readAndSetCookies(httpURLConnection);
		return resultBean;
	}

	/**
	 * 创建连接管道
	 * 
	 * @param url
	 * @param isPost
	 * @param requestHead
	 * @return 返回写入的数据管道
	 * @throws IOException
	 */
	protected HttpURLConnection connect(final URL url, final Boolean isPost, Map<String, String> requestHead)
			throws IOException {
		URLConnection uRLConnection = null;
		if (this.proxyHost == null) {
			uRLConnection = url.openConnection();
		} else {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.porxyPort));
			uRLConnection = url.openConnection(proxy);
		}
		HttpURLConnection httpURLConnection = (HttpURLConnection) uRLConnection;
		// 设置是否自动重定向
		httpURLConnection.setInstanceFollowRedirects(isFollowRedirects());
		// 设置访问模式
		httpURLConnection.setRequestMethod(isPost != null && isPost ? "POST" : "GET");
		// 设置是否从httpUrlConnection读入，默认情况下是true;
		httpURLConnection.setDoInput(true);
		// http正文内，因此需要设为true, 默认情况下是false;
		httpURLConnection.setDoOutput(true);
		// Post 请求不能使用缓存
		httpURLConnection.setUseCaches(false);
		// 请求头
		if (requestHead == null) {
			requestHead = new HashMap<String, String>();
		}
		// 设置请求类型编码
		if (requestHead.get("Content-type") == null) {
			httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		}
		// 设置浏览器的版本
		if (requestHead.get("User-Agent") == null) {
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		}
		// 设置默认的Referer
		if (requestHead.get("Referer") == null) {
			httpURLConnection.setRequestProperty("Referer", url.toExternalForm());
		}

		for (String key : requestHead.keySet()) {
			httpURLConnection.setRequestProperty(key, requestHead.get(key));
		}

		// 加载cookies
		if (this.cookies != null && this.cookies.size() > 0)
			httpURLConnection.setRequestProperty("Cookie", getInnerCookies());

		httpURLConnection.setConnectTimeout(connectTimeout);
		httpURLConnection.setReadTimeout(readTimeout);
		// 开始连接连接
		httpURLConnection.connect();
		// 返回该创建连接后的对象
		return httpURLConnection;
	}

	/**
	 * 取响应的结果
	 * 
	 * @param httpURLConnection
	 * @return
	 * @throws IOException
	 */
	protected ResultBean readInputStream(HttpURLConnection httpURLConnection, Boolean onlyHead) throws IOException {
		// 读取响应的数据
		InputStream inputStream = null;
		try {
			inputStream = httpURLConnection.getInputStream();
		} catch (Exception e) {
			inputStream = httpURLConnection.getErrorStream();
		}
		long totalByte = -1;
		if (httpURLConnection.getHeaderField("Content-Length") != null) {
			totalByte = Long.parseLong(httpURLConnection.getHeaderField("Content-Length"));
		}
		ResultBean resultBean = new ResultBean();
		// 判断是否读取内容
		if (onlyHead == null || onlyHead == false) {
			byte[] resultBin = null;
			if (inputStream != null) {
				// 读取内容部分
				resultBin = inputStreamToBytes(inputStream, totalByte);
				inputStream.close();
			}
			resultBean.setData(resultBin);
		}
		// 设置响应的头信息
		resultBean.setResponseHead(httpURLConnection.getHeaderFields());
		// 设置响应状态code
		resultBean.setStat(httpURLConnection.getResponseCode());
		return resultBean;
	}

	/**
	 * 设置当前网络访问后的cookies
	 * 
	 * @param httpURLConnection
	 */
	protected void readAndSetCookies(HttpURLConnection httpURLConnection) {
		setInnerCookies(httpURLConnection.getHeaderFields().get("Set-Cookie"));
	}

	private void setInnerCookies(List<String> cookiesStr) {
		if (cookiesStr != null)
			for (int i = 0; i < cookiesStr.size(); i++) {
				String tempStr = cookiesStr.get(i);
				tempStr = tempStr.replaceAll(";", "=");
				String[] mapping = tempStr.split("=");
				if (mapping.length > 1) {
					String key = mapping[0];
					String value = mapping[1];
					this.cookies.put(key, value);
				}
			}
	}

	private String getInnerCookies() {
		String result = "";
		for (String key : this.cookies.keySet()) {
			result += key + "=" + this.cookies.get(key) + "; ";
		}
		return result.trim();
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	private byte[] inputStreamToBytes(InputStream inputStream, long totalByte) throws IOException {
		byte[] container = new byte[102400];
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		int b = -1;
		while ((b = inputStream.read(container)) != -1) {
			if (listenOutputStream != null) {
				try {
					listenOutputStream.write(container, 0, b);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			arrayOutputStream.write(container, 0, b);
			showProgress(arrayOutputStream.size(), totalByte);
		}
		arrayOutputStream.flush();
		byte[] data = arrayOutputStream.toByteArray();
		arrayOutputStream.close();
		return data;
	}

	/**
	 * 返回结果集
	 * 
	 * @author admin
	 * 
	 */
	public class ResultBean implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2933585102741021542L;

		public ResultBean() {

		}

		private byte[] data;

		private Map<String, List<String>> responseHead;

		private int stat = 0;

		public int getStat() {
			return stat;
		}

		public void setStat(int stat) {
			this.stat = stat;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public Map<String, List<String>> getResponseHead() {
			return responseHead;
		}

		public void setResponseHead(Map<String, List<String>> responseHead) {
			this.responseHead = responseHead;
		}

		/**
		 * 取出字符编码，没有则返回null
		 * 
		 * @return
		 */
		public String getCharset() {
			String charset = null;
			// 取出文字编码
			List<String> contentTypes = this.getResponseHead().get("Content-Type");
			if (contentTypes != null) {
				for (String contentType : contentTypes) {
					int at = contentType.indexOf(CHARSET + "=");
					if (at > -1) {
						String right = contentType.substring(at + CHARSET.length() + 1, contentType.length());
						charset = right.trim();
					}
				}
			}
			return charset;
		}
	}

}

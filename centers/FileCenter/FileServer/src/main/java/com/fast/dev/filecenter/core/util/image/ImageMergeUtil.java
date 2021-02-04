package com.fast.dev.filecenter.core.util.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

/**
 * 图像合成处理
 * 
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2017年10月12日
 *
 */
public class ImageMergeUtil {

	/**
	 * 合并图片到背景底色居中
	 * 
	 * @param source
	 *            图片原
	 * @param scale
	 *            相对输出的缩放比例
	 * @param rgb
	 *            背景色的RgB
	 * @param target
	 *            输出路径
	 * @param width
	 *            输出图片的宽
	 * @param height
	 *            输出图片的高
	 */
	public static boolean mergeToCenter(final File sourceImageFile, double scale, int rgb, final File outputFile,
			int width, int height) {
		try {
			// 创建背景
			BufferedImage backgroupImageBuffer = createImageBuffer(width, height, rgb);
			mergeToCenter(sourceImageFile, scale, backgroupImageBuffer, outputFile, width, height);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 合并图片到背景中
	 * 
	 * @param sourceImageFile
	 * @param scale
	 * @param backGroupImageFile
	 * @param outputFile
	 * @param width
	 * @param height
	 * @return
	 */
	public static boolean mergeToCenter(final File sourceImageFile, double scale, File backGroupImageFile,
			final File outputFile, int width, int height) {
		try {
			// 背景图
			BufferedImage backgroupImageBuffer = createScaleBackBuffer(backGroupImageFile, width, height);
			// 合并图
			mergeToCenter(sourceImageFile, scale, backgroupImageBuffer, outputFile, width, height);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 合并图像
	 * 
	 * @param sourceImageFile
	 * @param scale
	 * @param backGroupImageFile
	 * @param outputFile
	 * @param width
	 * @param height
	 * @throws Exception
	 */
	private static void mergeToCenter(final File sourceImageFile, double scale, BufferedImage backgroupImageBuffer,
			final File outputFile, int width, int height) throws Exception {
		// 目标图片
		BufferedImage sourceImage = readImage(sourceImageFile);
		// 图片缩放率
		double scaleRate = getImageScale(backgroupImageBuffer, sourceImage, scale);
		// 图片缩放
		sourceImage = scaleImage(sourceImage, scaleRate);
		// 图片合成
		drawImageToCenter(backgroupImageBuffer, sourceImage);
		// 保存图片缓存到目标
		saveBufferFile(outputFile, backgroupImageBuffer);
	}

	/**
	 * 创建背景图
	 * 
	 * @param backGroupImageFile
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage createScaleBackBuffer(File backGroupImageFile, int width, int height)
			throws IOException {
		// 读取背景图片
		BufferedImage sourceImage = ImageIO.read(backGroupImageFile);
		// 创建原缩放比例的缓存
		BufferedImage targetImage = new BufferedImage(width, height, Image.SCALE_SMOOTH);
		// 图片指针
		int[][] imagePoint = new int[][] { new int[] { sourceImage.getWidth(), sourceImage.getHeight() },
				new int[] { targetImage.getWidth(), targetImage.getHeight() } };
		// 计算缩放率
		double rateW = (double) imagePoint[1][0] / (double) imagePoint[0][0];
		double rateH = (double) imagePoint[1][1] / (double) imagePoint[0][1];
		double rate = rateW > rateH ? rateW : rateH;
		// 图片缩放到足够的大
		BufferedImage newBufferedImage = scaleImage(sourceImage, rate);
		imagePoint[0][0] = newBufferedImage.getWidth();
		imagePoint[0][1] = newBufferedImage.getHeight();

		int x = (newBufferedImage.getWidth() - targetImage.getWidth()) / 2;
		int y = (newBufferedImage.getHeight() - targetImage.getHeight()) / 2;

		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				if (x + w < newBufferedImage.getWidth() && y + h < newBufferedImage.getHeight()) {
					int rgb = newBufferedImage.getRGB(x + w, y + h);
					targetImage.setRGB(w, h, rgb);
				}
			}
		}
		return targetImage;
	}

	/**
	 * 创建图像buffer，并填充背景颜色
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private static BufferedImage createImageBuffer(int width, int height, int rgb) {
		BufferedImage image = new BufferedImage(width, height, Image.SCALE_SMOOTH);
		Graphics2D graphics2d = image.createGraphics();
		graphics2d.setColor(new Color(rgb));
		graphics2d.fillRect(0, 0, width, height);
		graphics2d.dispose();
		return image;
	}

	/**
	 * 缩放图片
	 * 
	 * @param sourceBuffer
	 * @param scaleRate
	 */
	private static BufferedImage scaleImage(BufferedImage sourceBuffer, double scaleRate) {
		int width = (int) (sourceBuffer.getWidth() * scaleRate);
		int height = (int) (sourceBuffer.getHeight() * scaleRate);
		// 创建原图缩放比例的图片
		Image image = sourceBuffer.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		// 创建原缩放比例的缓存
		BufferedImage tag = new BufferedImage(width, height, Image.SCALE_SMOOTH);
		// 支持png的透明特性
		Graphics2D graphics2d = tag.createGraphics();
		BufferedImage bufferedImage = graphics2d.getDeviceConfiguration().createCompatibleImage(image.getWidth(null),
				image.getHeight(null), Transparency.TRANSLUCENT);
		graphics2d.dispose();
		Graphics2D newGraphics2D = bufferedImage.createGraphics();
		// 绘制缩小后的图
		newGraphics2D.drawImage(image, 0, 0, null);
		newGraphics2D.dispose();
		return bufferedImage;
	}

	/**
	 * 获取图片的缩放率
	 * 
	 * @param externalImage
	 *            外部
	 * @param interiorImage
	 *            内部
	 * @param referRate
	 *            参考缩放率
	 * @return 合理的缩放比例
	 */
	private static double getImageScale(BufferedImage externalImage, BufferedImage interiorImage, double referRate) {
		int[][] imagePoint = new int[][] { new int[] { 0, 0 }, new int[] { 0, 0 } };
		// 图片1的宽和高
		imagePoint[0][0] = externalImage.getWidth();
		imagePoint[0][1] = externalImage.getHeight();
		// 图片2的宽和高
		imagePoint[1][0] = interiorImage.getWidth();
		imagePoint[1][1] = interiorImage.getHeight();
		int index = imagePoint[0][0] / imagePoint[1][0] > imagePoint[0][1] / imagePoint[1][1] ? 1 : 0;
		// 新的长度
		int newSize = (int) (imagePoint[0][index] * referRate);
		// 计算出新的比值
		double rate = newSize / (double) imagePoint[1][index];
		return rate;
	}

	/**
	 * 画图
	 * 
	 * @param externalImage
	 * @param interiorImage
	 */
	private static void drawImageToCenter(final BufferedImage externalImage, final BufferedImage interiorImage) {
		// 计算出居中的坐标
		int x = (externalImage.getWidth() - interiorImage.getWidth()) / 2;
		int y = (externalImage.getHeight() - interiorImage.getHeight()) / 2;
		Graphics2D sourceGraphics2D = externalImage.createGraphics();
		Image targetImage = interiorImage.getScaledInstance(interiorImage.getWidth(), interiorImage.getHeight(),
				Image.SCALE_SMOOTH);
		sourceGraphics2D.drawImage(targetImage, x, y, null);
		sourceGraphics2D.dispose();
	}

	/**
	 * 保存图像缓存到文件
	 * 
	 * @param outputFile
	 * @param bufferedImage
	 * @throws IOException
	 */
	private static void saveBufferFile(final File outputFile, final BufferedImage bufferedImage) throws IOException {
		String imageFormat = FilenameUtils.getExtension(outputFile.getName());
		ImageIO.write(bufferedImage, imageFormat, outputFile);
	}

	/**
	 * 读取图片
	 * 
	 * @param sourceFile
	 * @throws IOException
	 */
	private static BufferedImage readImage(File sourceFile) throws IOException {
		BufferedImage image = ImageIO.read(sourceFile);
		return image;
	}

	// public static void main(String[] args) {
	// long l = System.currentTimeMillis();
	// // 9211020 灰色
	// // 986895 黑色
	// int rgb = 9211020;
	// int width = 720;
	// int height = 1080;
	// // int width = 1920;
	// // int height = 1080;
	// double scale = 1 - 0.618;
	// // mergeToCenter(new File("c:/logo.png"), scale, rgb, new File("c:/1.png"),
	// // width, height);
	// // mergeToCenter(new File("c:/logo.png"), scale, new
	// File("c:/backgroup.jpg"),
	// // new File("c:/2.png"), 720, 1080);
	//
	// // mergeToCenter(new File("c:/logo.png"), scale, new
	// File("c:/backgroup.jpg"),
	// // new File("c:/3.png"), 1920, 1080);
	//
	// mergeToCenter(new File("c:/logo.png"), scale, new
	// File("c:/launch_backgroup.jpg"), new File("c:/test.png"),
	// 1280, 1920);
	// System.out.println(" time : " + (System.currentTimeMillis() - l));
	// }

}

package image_processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

public class GetImageTextureFeature {
	private static int width, height;// width表示有几行，向下的，height表示有几列，向右的
	private static float[] Uniformity = new float[4]; // 4个一致性
	private static float Contrast[] = new float[4];// 4个对比度
	private static float Entropy[] = new float[4];// 4个熵
	private static float Relevance[] = new float[4];// 4个纹理相关性
	private static float Expectation[] = new float[4];// 4个期望
	private static float StandardDeviation[] = new float[4];// 4个标准差

	/*
	 * main()编写代码时检验用 getExpAndSD()为正式调用方法，传入一个地址，返回期望和标准差组成的地8为数组
	 */

	/*
	 * 将数据插入到纹理特征数据库
	 */
	public static void insertTextureToDB() throws SQLException, IOException {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from image_library");
		while (rs.next()) {
			int imageID = rs.getInt(1);
			String imageName = rs.getString(3);
			String imagePath = rs.getString(2);
			String result = null;
			float a[] = getExpAndSD(imagePath);
			String sql = "insert into image_Texture_Feature  values(" + imageID + "," + a[0] + "," + a[1] + "," + a[2]
					+ "," + a[3] + "," + a[4] + "," + a[5] + "," + a[6] + "," + a[7] + ")";
			PreparedStatement ptmt = con.prepareStatement(sql);
			boolean is = ptmt.execute();
			if (is) {
				result = "图片" + imageName + "纹理特征值插入操作失败！";
			} else {
				result = "图片" + imageName + "纹理特征值插入操作成功！";
			}
			System.out.println(result);
		}
	}

	public static float[] getExpAndSD(String imagePath) throws IOException {
		float ret[] = new float[8];
		int a[][] = DownGrayLevel(imagePath); // 8*8灰度矩阵
		float b[][] = CalculationGLCM(0, 1, a);// {0,1}灰度共生矩阵
		float c[][] = CalculationGLCM(-1, 1, a);// {-1,1}灰度共生矩阵
		float d[][] = CalculationGLCM(1, 0, a);// {1,0}灰度共生矩阵
		float e[][] = CalculationGLCM(1, 1, a);// {1,1}灰度共生矩阵

		b = GLCMNormalization(b);
		c = GLCMNormalization(c);
		d = GLCMNormalization(d);
		e = GLCMNormalization(e);

		Uniformity[0] = TextureUniformity(b);
		Uniformity[1] = TextureUniformity(c);
		Uniformity[2] = TextureUniformity(d);
		Uniformity[3] = TextureUniformity(e);

		Contrast[0] = CalculateContrast(b);
		Contrast[1] = CalculateContrast(c);
		Contrast[2] = CalculateContrast(d);
		Contrast[3] = CalculateContrast(e);

		Entropy[0] = CalculateEntropy(b);
		Entropy[1] = CalculateEntropy(c);
		Entropy[2] = CalculateEntropy(d);
		Entropy[3] = CalculateEntropy(e);

		Relevance[0] = CalculateRelevance(b);
		Relevance[1] = CalculateRelevance(c);
		Relevance[2] = CalculateRelevance(d);
		Relevance[3] = CalculateRelevance(e);

		Expectation[0] = CalculateExpectation(Uniformity);
		Expectation[1] = CalculateExpectation(Contrast);
		Expectation[2] = CalculateExpectation(Entropy);
		Expectation[3] = CalculateExpectation(Relevance);

		StandardDeviation[0] = CalculateStandardDeviation(Uniformity);
		StandardDeviation[1] = CalculateStandardDeviation(Contrast);
		StandardDeviation[2] = CalculateStandardDeviation(Entropy);
		StandardDeviation[3] = CalculateStandardDeviation(Relevance);

		for (int i = 0; i < Expectation.length; i++) {
			ret[i] = Expectation[i];
			ret[i + Expectation.length] = StandardDeviation[i];
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		 insertTextureToDB();
		/*
		 * 注释掉的代码为编写代码时检验用
		 */

		/*String path = "D:/CBIR/资料/101_ObjectCategories/101_ObjectCategories/accordion/image_0004.jpg";
		int a[][] = DownGrayLevel(path); // 8*8灰度矩阵 float
		float b[][] = CalculationGLCM(0, 1, a);// {0,1}灰度共生矩阵 float
		float c[][] = CalculationGLCM(-1, 1, a);// {-1,1}灰度共生矩阵 float
		float d[][] = CalculationGLCM(1, 0, a);// {1,0}灰度共生矩阵 float
		float e[][] = CalculationGLCM(1, 1, a);// {1,1}灰度共生矩阵

		b = GLCMNormalization(b);
		c = GLCMNormalization(c);
		d = GLCMNormalization(d);
		e = GLCMNormalization(e);

		Uniformity[0] = TextureUniformity(b);
		Uniformity[1] = TextureUniformity(c);
		Uniformity[2] = TextureUniformity(d);
		Uniformity[3] = TextureUniformity(e);

		Contrast[0] = CalculateContrast(b);
		Contrast[1] = CalculateContrast(c);
		Contrast[2] = CalculateContrast(d);
		Contrast[3] = CalculateContrast(e);

		Entropy[0] = CalculateEntropy(b);
		Entropy[1] = CalculateEntropy(c);
		Entropy[2] = CalculateEntropy(d);
		Entropy[3] = CalculateEntropy(e);

		Relevance[0] = CalculateRelevance(b);
		Relevance[1] = CalculateRelevance(c);
		Relevance[2] = CalculateRelevance(d);
		Relevance[3] = CalculateRelevance(e);

		Expectation[0] = CalculateExpectation(Uniformity);
		Expectation[1] = CalculateExpectation(Contrast);
		Expectation[2] = CalculateExpectation(Entropy);
		Expectation[3] = CalculateExpectation(Relevance);

		StandardDeviation[0] = CalculateStandardDeviation(Uniformity);
		StandardDeviation[1] = CalculateStandardDeviation(Contrast);
		StandardDeviation[2] = CalculateStandardDeviation(Entropy);
		StandardDeviation[3] = CalculateStandardDeviation(Relevance);

		System.out.print("Uniformity:");
		for (int i = 0; i < Uniformity.length; i++) {
			System.out.print(Uniformity[i] + " | ");
		}
		System.out.println();
		System.out.print("Contrast");
		for (int i = 0; i < Contrast.length; i++) {
			System.out.print(Contrast[i] + " | ");
		}
		System.out.println();
		System.out.print("Entropy:");
		for (int i = 0; i < Entropy.length; i++) {
			System.out.print(Entropy[i] + " | ");
		}
		System.out.println();
		System.out.print("Relevance:");
		for (int i = 0; i < Relevance.length; i++) {
			System.out.print(Relevance[i] + " | ");
		}
		System.out.println();
		System.out.print("Expectation:");
		for (int i = 0; i < Expectation.length; i++) {
			System.out.print(Expectation[i] + " | ");
		}
		System.out.println();
		System.out.print("StandardDeviation:");
		for (int i = 0; i < StandardDeviation.length; i++) {
			System.out.print(StandardDeviation[i] + " | ");
		}
		System.out.println();
		System.out.println("width=" + width);
		System.out.println("height" + height);
*/
	}

	/*
	 * 计算标准差
	 */
	public static float CalculateStandardDeviation(float a[]) {
		float ret = 0, sum = 0, ave = 0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i];
		}
		ave = sum / a.length;
		for (int i = 0; i < a.length; i++) {
			sum = (float) Math.pow((a[i] - ave), 2);
		}
		ret = (float) Math.sqrt(sum / a.length);
		return ret;
	}

	/*
	 * 计算期望
	 */
	public static float CalculateExpectation(float a[]) {
		float ret = 0;
		for (int i = 0; i < a.length; i++) {
			ret = (float) (ret + a[i] * 0.25);
		}
		return ret;
	}

	/*
	 * 计算纹理相关性
	 */
	public static float CalculateRelevance(float a[][]) {
		float ret = 0;
		float temp1 = 0, temp2 = 0, temp3 = 0;
		float ux = 0, uy = 0, sx = 0, sy = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				temp1 += a[i][j];
				temp2 += a[j][i];
			}
			ux = ux + i * temp1;
			uy = uy + i * temp2;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				temp1 += a[i][j];
				temp2 += a[j][i];
				temp3 += i * j * a[i][j];
			}
			sx = (float) Math.sqrt((sx + Math.pow((i - ux), 2) * temp1));
			sy = (float) Math.sqrt((sy + Math.pow((i - uy), 2) * temp1));
		}
		ret = (temp3 - ux * uy) / (sx * sy);
		return ret;
	}

	/*
	 * 计算熵
	 */
	public static float CalculateEntropy(float a[][]) {
		float ret = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ret = (float) (ret + a[i][j] * log(a[i][j], 2));
			}
		}
		return ret;
	}

	public static double log(double value, double base) {
		if (value == 0) {
			return Math.log(0.000000000001) / Math.log(base);
		} else {
			return Math.log(value) / Math.log(base);
		}
	}

	/*
	 * 计算对比度
	 */
	public static float CalculateContrast(float a[][]) {
		float ret = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ret = (float) (ret + Math.pow((i - j), 2) * a[i][j]);
			}
		}
		return ret;
	}

	/*
	 * 纹理一致性计算
	 */
	public static float TextureUniformity(float a[][]) {
		float ret = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ret = (float) (ret + Math.pow(a[i][j], 2));
			}
		}
		return ret;
	}

	/*
	 * 归一化处理
	 */
	public static float[][] GLCMNormalization(float a[][]) {
		float temp = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				temp = temp + a[i][j];
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				a[i][j] = a[i][j] / temp;
			}
		}
		return a;
	}

	/*
	 * 计算灰度共生矩阵
	 */
	public static float[][] CalculationGLCM(int a, int b, int downgray[][]) {
		float ret[][] = new float[8][8];
		for (int m = 0; m < 8; m++) {
			for (int n = 0; n < 8; n++) {
				int count = 0;
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						if (i + a < 0 || i + a > width - 1 || j + b > height - 1) {
							continue;
						} else {
							if (downgray[i][j] == m && downgray[i + a][j + b] == n) {
								count++;
							}
						}
					}
				}
				ret[m][n] = count;
			}
		}
		return ret;
	}

	/*
	 * 降低灰度等级：0-255变成0-7
	 */
	public static int[][] DownGrayLevel(String imagePath) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		int ret[][] = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				final int color = bufferedImage.getRGB(i, j);
				final int r = (color >> 16) & 0xff;
				final int g = (color >> 8) & 0xff;
				final int b = color & 0xff;
				double Gray = 0.30 * r + 0.59 * g + 0.11 * b;
				if (Gray <= 31) {
					ret[i][j] = 0;
				} else if (Gray <= 63) {
					ret[i][j] = 1;
				} else if (Gray <= 95) {
					ret[i][j] = 2;
				} else if (Gray <= 127) {
					ret[i][j] = 3;
				} else if (Gray <= 159) {
					ret[i][j] = 4;
				} else if (Gray <= 191) {
					ret[i][j] = 5;
				} else if (Gray <= 223) {
					ret[i][j] = 6;
				} else {
					ret[i][j] = 7;
				}
			}
		}
		return ret;
	}
}

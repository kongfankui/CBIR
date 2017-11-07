package image_processing;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class GetImageShapeFeature {
	private static int width, height;
	private static float standard = 0.01f;
	private static int Gray[][];

	/*
	 * main()用于编写时检验 在main()中调用insertShapeintoDB()将所有特征值插入数据库
	 */
	public static void main(String[] args) throws IOException, SQLException {
		insertShapeintoDB();
		/*
		 * 注释掉的为编写时检验的代码
		 */
		/*
		 * System.out.print("请输入原始图的地址："); BufferedReader br = new
		 * BufferedReader(new InputStreamReader(System.in)); String
		 * OriginalImagePath = br.readLine(); OriginalImagePath =
		 * OriginalImagePath.replace("\\", "\\/"); Grayscale(OriginalImagePath);
		 * MedianFiltering(); Gray=DoSobel(Gray); int
		 * u1=Gray[0][0],u2=Gray[0][0]; for(int i=0;i<width;i++){ for(int
		 * j=0;j<height;j++){ if(u1>Gray[i][j]){ u1=Gray[i][j]; }
		 * if(u2<Gray[i][j]){ u2=Gray[i][j]; } } } float threshold=(float)
		 * ((u1+u2)/2.0); DoTwoValued( threshold); double
		 * HU[]=CalculateMomentInvariants(); for(int i=0;i<HU.length;i++){
		 * System.out.println("HU["+i+"]="+HU[i]); }
		 */
	}

	/*
	 * 插入形状数据库
	 */
	public static void insertShapeintoDB() throws SQLException, IOException {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from image_library");
		while (rs.next()) {
			int imageID = rs.getInt(1);
			String imageName = rs.getString(3);
			String imagePath = rs.getString(2);
			String result = null;
			double ret[] = getShapeFeature(imagePath);
			String sql = "insert into image_Shape_Feature  values(" + imageID + "," + ret[0] + "," + ret[1] + ","
					+ ret[2] + "," + ret[3] + "," + ret[4] + "," + ret[5] + "," + ret[6] + ")";
			PreparedStatement ptmt = con.prepareStatement(sql);
			boolean is = ptmt.execute();
			if (is) {
				result = "图片" + imageName + "形状特征值插入操作失败！";
			} else {
				result = "图片" + imageName + "形状特征值插入操作成功！";
			}
			System.out.println(result);
		}
	}

	/*
	 * 根据路径等到形状特征值数组
	 */
	public static double[] getShapeFeature(String imagePath) throws IOException {
		imagePath = imagePath.replace("\\", "\\/");
		Grayscale(imagePath);
		MedianFiltering();
		Gray = DoSobel(Gray);
		int u1 = Gray[0][0], u2 = Gray[0][0];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (u1 > Gray[i][j]) {
					u1 = Gray[i][j];
				}
				if (u2 < Gray[i][j]) {
					u2 = Gray[i][j];
				}
			}
		}
		float threshold = (float) ((u1 + u2) / 2.0);
		DoTwoValued(threshold);
		double HU[] = CalculateMomentInvariants();
		return HU;
	}

	/*
	 * 图像灰度化
	 */
	public static void Grayscale(String imagePath) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		Gray = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				final int color = bufferedImage.getRGB(i, j);
				final int r = (color >> 16) & 0xff;
				final int g = (color >> 8) & 0xff;
				final int b = color & 0xff;
				double gray = 0.30 * r + 0.59 * g + 0.11 * b;
				Gray[i][j] = (int) gray;
			}
		}
	}

	/*
	 * 中值滤波
	 */
	public static void MedianFiltering() {
		int median[] = new int[9];
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				median[0] = Gray[i - 1][j - 1];
				median[1] = Gray[i - 1][j];
				median[2] = Gray[i - 1][j + 1];
				median[3] = Gray[i][j - 1];
				median[4] = Gray[i][j];
				median[5] = Gray[i][j + 1];
				median[6] = Gray[i + 1][j - 1];
				median[7] = Gray[i + 1][j];
				median[8] = Gray[i + 1][j + 1];
				Arrays.sort(median);
				Gray[i][j] = median[4];
			}
		}
	}

	/*
	 * 用sobel算子对图像进行锐化
	 */
	public static int[][] DoSobel(int Gray[][]) {
		int ret[][] = new int[width][height];
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				int gx = Gray[i + 1][j - 1] + 2 * Gray[i + 1][j] + Gray[i + 1][j + 1] - Gray[i - 1][j - 1]
						- 2 * Gray[i - 1][j] - Gray[i - 1][j + 1];
				int gy = Gray[i - 1][j + 1] + 2 * Gray[i][j + 1] + Gray[i + 1][j + 1] - Gray[i - 1][j - 1]
						- 2 * Gray[i][j - 1] - Gray[i + 1][j - 1];
				ret[i][j] = (int) Math.min(255, (Math.sqrt(gx*gx + gy*gy)));
			}
		}
		return ret;
	}

	/*
	 * 迭代阈值法进行二值化
	 */
	public static void DoTwoValued(float threshold) {
		float threshold1 = threshold, threshold2 = 0;
		float G1 = 0, G2 = 0;
		int count1 = 0, count2 = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (Gray[i][j] <= threshold1) {
					G1 += Gray[i][j];
					count1++;
				} else {
					G2 += Gray[i][j];
					count2++;
				}
			}
		}
		G1 = (float) ((G1 * 1.0) / count1);
		G2 = (float) ((G2 * 1.0) / count2);
		threshold2 = (G1 + G2) / 2;
		if (Math.abs(threshold1 - threshold2) <= standard) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (Gray[i][j] <= threshold2) {
						Gray[i][j] = 0;
					} else {
						Gray[i][j] = 255;
					}
				}
			}
		} else {
			DoTwoValued(threshold2);
		}
	}

	/*
	 * 计算Hu不变矩和离心率，6个不变矩和离心率一起存在数组中
	 */
	public static double[] CalculateMomentInvariants() {
		int M00 = 0, M10 = 0, M01 = 0;
		double AveX = 0, AveY = 0;
		double u20, u02, u11, u30, u12, u03, u21;
		double temporary[] = new double[2], ret[] = new double[7];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				M00 += Gray[i][j];
				M01 += i;
				M01 += j;
			}
		}
		AveX = M10 * 1.0 / M00;
		AveY = M01 * 1.0 / M00;
		//temporary[0]存储MJk,temporary[1]存储r
		temporary = getMjk(2, 0, AveX, AveY);
		u20 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(0, 2, AveX, AveY);
		u02 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(1, 1, AveX, AveY);
		u11 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(3, 0, AveX, AveY);
		u30 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(2, 1, AveX, AveY);
		u21 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(0, 3, AveX, AveY);
		u03 = temporary[0] / (Math.pow(M00, temporary[1]));
		temporary = getMjk(1, 2, AveX, AveY);
		u12 = temporary[0] / (Math.pow(M00, temporary[1]));
		ret[0] = u20 + u02;
		ret[1] = Math.pow((u20 - u02), 2) + 4 * Math.pow(u11, 2);
		ret[2] = Math.pow((u30 - 3 * u12), 2) + Math.pow((u03 - 3 * u21), 2);
		ret[3] = Math.pow((u30 + u12), 2) + Math.pow((u03 + u21), 2);
		ret[4] = (u30 - 3 * u12) * (u03 + u12) * (Math.pow((u30 + u12), 2) - 3 * Math.pow((u21 + u03), 2))
				+ (u03 - 3 * u21) * (u30 + u21) * (Math.pow((u03 + u21), 2) - 3 * Math.pow((u12 + u30), 2));
		ret[5] = (u20 - u02) * (Math.pow((u30 + u12), 2) - Math.pow((u21 + u03), 2))
				+ 4 * u11 * (u30 + u12) * (u03 + u21);

		ret[6] = (Math.pow((u20 - u02), 2) + 4 * u11 * u11) / (Math.pow((u20 + u02), 2));

		return ret;
	}

	/*
	 * 配合CalculateMomentInvariants()使用，ret[0]存储MJk,ret[1]存储r
	 */
	public static double[] getMjk(int j, int k, double AveX, double AveY) {
		double ret[] = new double[2];
		for (int a = 0; a < width; a++) {
			for (int b = 0; b < height; b++) {
				ret[0] = ret[0] + Gray[a][b] * Math.pow((a - AveX), j) * Math.pow((b - AveY), k);
			}
		}
		ret[1] = (j + k) * 1.0 / 2 + 1;
		return ret;
	}

}

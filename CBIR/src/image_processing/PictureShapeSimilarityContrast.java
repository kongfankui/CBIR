package image_processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PictureShapeSimilarityContrast {
	private static double[] OriginalImageShape = new double[3];
	private static double[] ComparisonImageShape = new double[3];
	private static float ResultSet[];
	private static int ResultIDSet[];
	private static String ResultPathSet[];

	/*
	 * 测试代码所用
	 */
	public static void main(String[] args) throws Exception {
		int count=new GetRecordCount().getCount();
		ResultSet=new float[count];
		ResultIDSet=new int[count];
		ResultPathSet=new String[count];
		DoOriginalImage();
		DoComparison();
		mysort();
		/*
		 * for(int i=0;i<10;i++){
		 * System.out.print("ResultSet["+i+"]="+ResultSet[i]+" , "); }
		 * System.out.println();
		 */
		ResultSet = ReturnSimilaritySet();
		for (int i = 0; i < OriginalImageShape.length; i++) {
			System.out.print(OriginalImageShape[i] + " | ");
		}
		System.out.println();
		/*
		 * for(int i=0;i<ResultSet.length;i++){
		 * ResultSet[i]=ResultSet[i]/ResultSet[ResultSet.length-1]; }
		 */
		for (int i = 0; i < 10; i++) {
			System.out.print("ResultSet[" + i + "]=" + ResultSet[i] + " , ");
			System.out.println("ResultIDSet[" + i + "]=" + ResultIDSet[i]);
			System.out.println("ResultPathSet[" + i + "]=" + ResultPathSet[i]);
		}
		System.out.println("相似度最大：" + ResultSet[0]);
		System.out.println("相似度最小：" + ResultSet[9143]);
	}
	
	/*
	 * 返回路径集
	 */
	public String[] ReturnPathSet(String OriginalPath) throws Exception {
		int count=new GetRecordCount().getCount();
		ResultSet=new float[count];
		ResultIDSet=new int[count];
		ResultPathSet=new String[count];
		DoOriginalImage(OriginalPath);
		DoComparison();
		mysort();
		return ResultPathSet;
	}

	/*
	 * 返回相似度集
	 */
	public static float[] ReturnSimilaritySet() {
		for (int i = 0; i < ResultSet.length; i++) {
			ResultSet[i] = ResultSet[i] / ResultSet[ResultSet.length - 1];
		}
		for (int i = 0; i < ResultSet.length; i++) {
			ResultSet[i] = (float) (1.0 - ResultSet[i]);
		}
		return ResultSet;
	}

	

	/*
	 * 实现地址、ID、距离差同步排序
	 */
	public static void mysort() {
		float temp;
		int tempid;
		String tempPath;
		for (int i = 0; i < ResultSet.length; i++) {
			for (int j = i; j < ResultSet.length; j++) {
				if (ResultSet[i] > ResultSet[j]) {
					temp = ResultSet[i];
					ResultSet[i] = ResultSet[j];
					ResultSet[j] = temp;
					tempid = ResultIDSet[i];
					ResultIDSet[i] = ResultIDSet[j];
					ResultIDSet[j] = tempid;
					tempPath = ResultPathSet[i];
					ResultPathSet[i] = ResultPathSet[j];
					ResultPathSet[j] = tempPath;
				}
			}
		}
	}

	/*
	 * 获取地址结果集
	 */
	public static void SetResultPathSet() throws SQLException {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select imagePath from image_Library");
		int i = 0;
		while (rs.next()) {
			ResultPathSet[i++] = rs.getString("imagePath");
		}
	}

	/*
	 * 求距离逼存到ResultSet数组里
	 */
	public static float SeekingDistance() {
		float ret = 0;
		double sum = 0;
		for (int i = 0; i < OriginalImageShape.length; i++) {
			sum = sum + Math.pow((OriginalImageShape[i] - ComparisonImageShape[i]), 2);
		}
		ret = (float) Math.sqrt(sum / OriginalImageShape.length);
		return ret;
	}

	/*
	 * 原图特征值和数据库中的对比
	 */
	public static void DoComparison() throws Exception {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from image_Shape_Feature");
		int a = 0;
		while (rs.next()) {
			for (int i = 0; i < ComparisonImageShape.length - 1; i++) {
				ComparisonImageShape[i] = rs.getDouble(i + 2);
			}
			ComparisonImageShape[ComparisonImageShape.length - 1] = rs.getDouble(8);
			ResultSet[a] = SeekingDistance();
			ResultIDSet[a] = ++a;
		}
		SetResultPathSet();
	}

	/*
	 * 求原图特征值并存到OriginalImageShape数组里
	 */
	public static void DoOriginalImage() throws IOException {
		System.out.print("请输入原始图的地址：");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String OriginalImagePath = br.readLine();
		OriginalImagePath = OriginalImagePath.replace("\\", "\\/");
		double shape[] = new double[7];
		shape = GetImageShapeFeature.getShapeFeature(OriginalImagePath);
		OriginalImageShape[0] = shape[0];
		OriginalImageShape[1] = shape[1];
		OriginalImageShape[2] = shape[6];
	}

	public static void DoOriginalImage(String OriginalImagePath) throws IOException {
		OriginalImagePath = OriginalImagePath.replace("\\", "\\/");
		double shape[] = new double[7];
		shape = GetImageShapeFeature.getShapeFeature(OriginalImagePath);
		OriginalImageShape[0] = shape[0];
		OriginalImageShape[1] = shape[1];
		OriginalImageShape[2] = shape[6];
	}
}

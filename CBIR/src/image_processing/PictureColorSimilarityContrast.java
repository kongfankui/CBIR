package image_processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * 实现输入一个图片地址
 * 检索后台数据库，寻找相似度高的图片
 * 最后将两张图片输出
 */
public class PictureColorSimilarityContrast {
	private static float OriginalImageHSI[] = new float[9];
	private static float ComparisonImageHSI[] = new float[9];
	private static float ResultSet[];
	private static int ResultIDSet[];
	private static String ResultPathSet[];

	/*
	 * main()函数用于编写过程试验代码正确性 ReturnPathSet()函数用于正式使用，返回路径集
	 * 
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
		for (int i = 0; i < OriginalImageHSI.length; i++) {
			System.out.print(OriginalImageHSI[i] + " | ");
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
		System.out.println("相差最小：" + ResultSet[0]);
		System.out.println("相差最大：" + ResultSet[9143]);
	}

	/*
	 * 计算比对比返回路径集
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



	public static void DoOriginalImage() throws IOException {
		System.out.print("请输入原始图的地址：");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String OriginalImagePath = br.readLine();
		OriginalImagePath = OriginalImagePath.replace("\\", "\\/");
		OriginalImageHSI = GetImageColorFeature.returnHSI_Mx(OriginalImagePath);
	}

	public static void DoOriginalImage(String OriginalImagePath) throws IOException {
		OriginalImagePath = OriginalImagePath.replace("\\", "\\/");
		OriginalImageHSI = GetImageColorFeature.returnHSI_Mx(OriginalImagePath);
	}

	public static void DoComparison() throws Exception {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from image_Color_Feature");
		int a = 0;
		while (rs.next()) {
			for (int i = 0; i < 9; i++) {
				ComparisonImageHSI[i] = rs.getFloat(i + 2);
			}
			ResultSet[a] = SeekingDistance();
			ResultIDSet[a] = ++a;
		}
		SetResultPathSet();
	}

	public static float SeekingDistance() {
		float ret = 0;
		double sum = 0;
		for (int i = 0; i < OriginalImageHSI.length; i++) {
			sum = sum + Math.pow((OriginalImageHSI[i] - ComparisonImageHSI[i]), 2);
		}
		ret = (float) Math.sqrt(sum / OriginalImageHSI.length);
		return ret;
	}

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

	public static void SetResultPathSet() throws SQLException {
		Connection con = DBUtil.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select imagePath from image_Library");
		int i = 0;
		while (rs.next()) {
			ResultPathSet[i++] = rs.getString("imagePath");
		}
	}
}

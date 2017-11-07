package image_processing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ComprehensiveSearch {
	  private static float[] ColorFeature;
	  private static float[] TextureFeature;
	  private static double[] ShapeTeature;
	  private static double ComprehensiveFeature;
	  private static float[] ResultSet;
	  private static String ResultPathSet[];
	  
	  public static void main(String[] args) throws Exception {
		//insertComprehensiveFeatureIntoDB();
		  String pString="D:/CBIR/资料/101_ObjectCategories/101_ObjectCategories/accordion/image_0001.jpg";
		  String path[]=ReturnPathSet(pString);
		  float[] hhh=ReturnSimilaritySet();
          mysort();
	
	}
	  
	  public static void getComprehensiveFeature(String OriginalPath) throws IOException{
		   ColorFeature=GetImageColorFeature.returnHSI_Mx(OriginalPath) ;
		   TextureFeature=GetImageTextureFeature.getExpAndSD(OriginalPath);
		   ShapeTeature=GetImageShapeFeature.getShapeFeature(OriginalPath);
		   double a=0,b=0,c=0;
		   for(int i=0;i<ColorFeature.length;i++){
			   a+=Math.pow(ColorFeature[i], 2);
		   }
		   for(int i=0;i<TextureFeature.length;i++){
			   b+=Math.pow(TextureFeature[i], 2);
		   }
		   for(int i=0;i<ShapeTeature.length;i++){
			   c+=Math.pow(ShapeTeature[i], 2);
		   }
		   ComprehensiveFeature=(a+b+c)/3;
	  }
	  
	  public static void insertComprehensiveFeatureIntoDB() throws SQLException, IOException{
		   String result = null;
		   Connection con=DBUtil.getConnection();
		    Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery("select * from image_library");
	    	while(rs.next()){
	    		int id=rs.getInt(1);
	    		String imageName=rs.getString(3);
	    		String path=rs.getString(2);
	    		getComprehensiveFeature(path);
	    		String sql="insert into ComprehensiveFeature values("+id+","+ComprehensiveFeature+")";
		    	PreparedStatement ptmt=con.prepareStatement(sql);
		    	boolean is=ptmt.execute();
				if(is){
					result = "图片"+imageName+"综合特征值插入操作失败！";
				}else {
					result =  "图片"+imageName+"综合特征值插入操作成功！";
				}
				System.out.println(result);
	    	}
	  }
	  
	  public static String[] ReturnPathSet(String OriginalPath) throws Exception {
			int count=new GetRecordCount().getCount();
			ResultSet=new float[count];
			ResultPathSet=new String[count];
			getComprehensiveFeature(OriginalPath);
			DoComparison();
			mysort();
			return ResultPathSet;
		}
		
	  
		private static void mysort() {
			float temp;
			String tempPath;
			for (int i = 0; i < ResultSet.length; i++) {
				for (int j = i; j < ResultSet.length; j++) {
					if (ResultSet[i] > ResultSet[j]) {
						temp = ResultSet[i];
						ResultSet[i] = ResultSet[j];
						ResultSet[j] = temp;
						tempPath = ResultPathSet[i];
						ResultPathSet[i] = ResultPathSet[j];
						ResultPathSet[j] = tempPath;
					}
				}
			}
	}

		private static void DoComparison() throws SQLException {
			Connection con = DBUtil.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from ComprehensiveFeature");
			int a = 0;
			while (rs.next()) {
				double feature=rs.getDouble(2);
				ResultSet[a++] = SeekingDistance(feature);
			}
			SetResultPathSet();
	}

		public static  float SeekingDistance(double feature) {
			return (float) Math.abs((feature-ComprehensiveFeature));
		}

		/*
		 * 返回相似度集
		 */
		public static float[] ReturnSimilaritySet() {
			for (int i = 0; i < ResultSet.length; i++) {
				ResultSet[i] = ResultSet[i] / ResultSet[ResultSet.length - 1];
			}
			for (int i = 0; i < ResultSet.length; i++) {
				ResultSet[i] =(float) (1.0 - ResultSet[i]);
			}
			return ResultSet;
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

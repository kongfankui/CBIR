package image_processing;

import javax.imageio.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * 从图片库中遍历所有图片并调用函数将对应图片的特征值写入图片特征库
 * 两张表由imageID唯一标识联系起来
 */
public class GetImageColorFeature {
	
	public static void main(String[] args) throws IOException, SQLException {
		 insertHSIToDB();
	}
	
	public static void insertHSIToDB() throws IOException, SQLException{
		String result = null;
	    String imagePath = null,imageName = null;
	    int imageID = 0;
		
	    Connection con=DBUtil.getConnection();
	    Statement stmt = con.createStatement();
    	ResultSet rs = stmt.executeQuery("select * from image_library");
    	while (rs.next()) {
			imageID=rs.getInt(1);
			imagePath=rs.getString(2);
			imageName=rs.getString(3);
			float a[]=returnHSI_Mx(imagePath);
			String sql="insert into image_Color_Feature (imageID,HM1,HM2,HM3,SM1,SM2,SM3,IM1,IM2,IM3) values("+imageID+","+
					+a[0]+","+a[1]+","+a[2]+","+a[3]+","+a[4]+","+a[5]+","+a[6]+","+a[7]+","+a[8]+")";
	    	PreparedStatement ptmt=con.prepareStatement(sql);
	    	boolean is=ptmt.execute();
			if(is){
				result = "图片"+imageName+"颜色特征值插入操作失败！";
			}else {
				result =  "图片"+imageName+"颜色特征值插入操作成功！";
			}
			System.out.println(result);
		}
	}
	/*
	 * 获得一张图片的HSI三个参数的M1、M2、M3
	 */
	public static float[] returnHSI_Mx(String imagePath) throws IOException{
		float HSI_Mx[]=new float[9];
		BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
		int width=bufferedImage.getWidth();
		int height=bufferedImage.getHeight();
		int n=width*height;
		double H[] = new double[n],S[] = new double[n],I[] = new double[n];
		double []hsi=new double[3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				final int color = bufferedImage.getRGB(i, j);
				final int r = (color >> 16) & 0xff;
				final int g = (color >> 8) & 0xff;
				final int b = color & 0xff;
				hsi=RGBtoHSI(r,g,b);
				H[i*height+j]=hsi[0];
				S[i*height+j]=hsi[1];
				I[i*height+j]=hsi[2];
			}
		}
		
		double MH[]=GetCentralMoment(n,H);
		double MS[]=GetCentralMoment(n,S);
		double MI[]=GetCentralMoment(n,I);
		for(int a=0;a<3;a++){
			HSI_Mx[a]=(float) MH[a];
		}
		for(int a=0;a<3;a++){
			HSI_Mx[3+a]=(float)MS[a];
		}
		for(int a=0;a<3;a++){
			HSI_Mx[6+a]=(float)MI[a];
		}
		return HSI_Mx;
	}
	
	public static double[] RGBtoHSI(int r,int g,int b){
	    double temp = Math.sqrt((r-g)*(r-g)+(r-b)*(g-b));
	    double h, s, i;
	    temp = temp > 0?temp:0.01;
	    if(b<=g)
	        h = Math.acos(((r-g+r-b)/2.0)/temp);
	    else
	        h = 2*Math.PI - Math.acos(((r-g+r-b)/2.0)/temp);
	    temp = r+g+b>0?r+g+b:0.01;
	    s = 1.0-(3.0/temp)*Math.min(Math.min(r, g),b);
	    i = (r+g+b)/3.0;
	    double ret[]={h,s,i};
	    return ret;
	}
	
	public static double[] GetCentralMoment(int n,double sum[]){//求HSI的MI，M2，M3；
		double centralmoment[]={0,0,0};
		double temporary0=0,temporary1=0,temporary2=0;
		for (int i = 0; i < n; i++) {
			temporary0=temporary0+sum[i];
		}
		centralmoment[0]=temporary0/n;
		for(int j=0;j<n;j++){
			temporary1= temporary1+Math.pow(sum[j]-centralmoment[0],2);
			temporary2= temporary2+Math.pow(sum[j]-centralmoment[0],3);
		}
		centralmoment[1]=Math.sqrt(temporary1/n);
		centralmoment[2]=Math.cbrt(temporary2/n);
		return centralmoment;
	}
}

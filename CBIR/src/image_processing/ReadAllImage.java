package image_processing;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

/*
 * 遍历文件
 * 将文件地址下所有图片插入到图片库中
 */
public class ReadAllImage {

	public static void main(String[] args) throws Exception {
		// 遍历文件
		File mFile = new File("D:/CBIR/资料/101_ObjectCategories/101_ObjectCategories");
		getAllFile(mFile);
	}

	public static void insertimage(String imagePath, String imageName) throws Exception {
		Connection con = DBUtil.getConnection();
		String sql = "insert into image_library (imagePath,imageName) values('" + imagePath + "','" + imageName + "')";
		PreparedStatement ptmt = con.prepareStatement(sql);
		boolean is = ptmt.execute();
		String result;
		if (is) {
			result = "图片" + imageName + "插入操作失败！";
		} else {
			result = "图片" + imageName + "插入操作成功！";
		}
		System.out.println(result);
	}

	private static void getAllFile(File mFile) throws Exception {
		// 1.获取子目录
		File[] files = mFile.listFiles();
		// 2.判断files是否是空的 否则程序崩溃
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					getAllFile(file);// 调用递归的方式
				} else {
					// 4. 添加到集合中去
					String fileName = file.getName();
					if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif")
							|| fileName.endsWith(".BMP")) {
						// 如果是这几种图片格式就添加进去
						String imageName = fileName;
						String imagePath = file.getAbsolutePath();
						imagePath = imagePath.replace("\\", "\\/");
						insertimage(imagePath, imageName);
					}
				}
			}
		}
	}
}

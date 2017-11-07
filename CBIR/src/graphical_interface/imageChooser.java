package graphical_interface;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class imageChooser {
	private static String imageAbsolutePath, imageName;

	public void action() {
		JFileChooser jfc = new JFileChooser("D:/CBIR/资料/101_ObjectCategories");
		jfc.showDialog(new JLabel(), "选择");
		File file = jfc.getSelectedFile();
		imageAbsolutePath = file.getAbsolutePath();
		imageName = jfc.getSelectedFile().getName();
	}

	public String getImageAbsolutePath() {
		return imageAbsolutePath;
	}

	public String getImageName() {
		return imageName;
	}

}

package graphical_interface;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

import image_processing.ComprehensiveSearch;
import image_processing.GetRecordCount;
import image_processing.PictureColorSimilarityContrast;
import image_processing.PictureShapeSimilarityContrast;
import image_processing.PictureTextureSimilarityContrast;

import javax.swing.JLabel;

public class Graphical_interface {

	private JFrame frame;
	/*
	 * 按钮等配件统一申明
	 */
	private JButton FileButton = new JButton("文     件");
	private JButton ColourSearchButton = new JButton("基于颜色检索");
	private JButton ShapeSearchButton = new JButton("基于形状检索");
	private JButton TextureSearchButton = new JButton("基于纹理检索");
	JButton ComprehensiveSearchButton = new JButton("综合检测");
	private JButton HelpButton = new JButton("帮助");
	private JLabel FileLabel = new JLabel("系统等待中..........");
	private JButton ShowOriginalImageButton = new JButton("显示原图");
	private JLabel ShowOriginalImageLabel = new JLabel("",JLabel.CENTER);
	private final JLabel TitleLabel = new JLabel();
	private static JLabel ShowResultSetLabel = new JLabel();
	private static JLabel SimilarityDegreeLabel = new JLabel();
	private static JButton PreviousImageButton = new JButton("上一张");
	private static JButton NextImageButton = new JButton("下一张");
	private static JLabel previewImageLabel = new JLabel();

	private static String imageAbsolutePath, imageName;
	private static String[] ResultPathSet = new String[9144];
	private static float[] ResultSet = new float[9144];
	private static int RollNumber = 0,previewRollNumber=1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Graphical_interface window = new Graphical_interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Graphical_interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("基于内容的图像检索");
		frame.setBounds(50, 50, 1150, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		/*
		 * 按钮等配件的基本设置
		 */
		FileButton.setBounds(0, 0, 150, 30);
		FileButton.setFont(new java.awt.Font("Dialog", 1, 15));
		FileButton.setBackground(Color.lightGray);
		frame.getContentPane().add(FileButton);

		ColourSearchButton.setBounds(150, 0, 150, 30);
		ColourSearchButton.setFont(new java.awt.Font("Dialog", 1, 15));
		ColourSearchButton.setBackground(Color.lightGray);
		frame.getContentPane().add(ColourSearchButton);

		TextureSearchButton.setBounds(300, 0, 150, 30);
		TextureSearchButton.setFont(new java.awt.Font("Dialog", 1, 15));
		TextureSearchButton.setBackground(Color.lightGray);
		frame.getContentPane().add(TextureSearchButton);

		ShapeSearchButton.setBounds(450, 0, 150, 30);
		ShapeSearchButton.setFont(new java.awt.Font("Dialog", 1, 15));
		ShapeSearchButton.setBackground(Color.lightGray);
		frame.getContentPane().add(ShapeSearchButton);
		
		ComprehensiveSearchButton.setBounds(600, 0, 150, 30);
		ComprehensiveSearchButton.setFont(new java.awt.Font("Dialog", 1, 15));
		ComprehensiveSearchButton.setBackground(Color.lightGray);
		frame.getContentPane().add(ComprehensiveSearchButton);

		HelpButton.setBounds(750, 0, 150, 30);
		HelpButton.setFont(new java.awt.Font("Dialog", 1, 15));
		HelpButton.setBackground(Color.lightGray);
		frame.getContentPane().add(HelpButton);

		FileLabel.setBounds(0, 31, 1140, 50);
		FileLabel.setFont(new java.awt.Font("Dialog", 1, 15));
		FileLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		frame.getContentPane().add(FileLabel);

		ShowOriginalImageButton.setVisible(false);
		ShowOriginalImageButton.setBounds(0, 85, 120, 30);
		frame.getContentPane().add(ShowOriginalImageButton);

		ShowOriginalImageLabel.setBounds(0, 120, 400, 400);
		ShowOriginalImageLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		ShowOriginalImageLabel.setVisible(false);
		frame.getContentPane().add(ShowOriginalImageLabel);

		ShowResultSetLabel.setBounds(450, 120, 400, 400);
		ShowResultSetLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		ShowResultSetLabel.setVisible(false);
		frame.getContentPane().add(ShowResultSetLabel);

		PreviousImageButton.setBounds(480, 570, 120, 30);
		PreviousImageButton.setVisible(false);
		frame.getContentPane().add(PreviousImageButton);

		NextImageButton.setBounds(690, 570, 120, 30);
		NextImageButton.setVisible(false);
		frame.getContentPane().add(NextImageButton);

		SimilarityDegreeLabel.setBounds(620, 530, 200, 30);
		SimilarityDegreeLabel.setVisible(false);
		frame.getContentPane().add(SimilarityDegreeLabel);

		TitleLabel.setBounds(450, 85, 200, 30);
		TitleLabel.setFont(new java.awt.Font("Dialog", 1, 15));
		TitleLabel.setVisible(false);
		frame.getContentPane().add(TitleLabel);
		
		
		previewImageLabel.setBounds(850, 320, 200, 200);
        previewImageLabel.setVisible(false);
		previewImageLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		frame.getContentPane().add(previewImageLabel);
		

		
		/*
		 * 给文件按钮添加点击事件响应
		 */
		FileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageChooser ic = new imageChooser();
				ic.action();
				imageAbsolutePath = ic.getImageAbsolutePath();
				imageName = ic.getImageName();
				FileLabel.setText("<html>" + "文件名：" + imageName + " <br>" + "文件路径：" + imageAbsolutePath + "</html>");
				ShowOriginalImageButton.setVisible(true);
			}
		});

		/*
		 * 给显示原图按钮添加点击事件响应
		 */
		ShowOriginalImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowOriginalImageLabel.setVisible(true);
				ShowOriginalImageLabel.setIcon(new ImageIcon(imageAbsolutePath));
			}
		});

		/*
		 * 给帮助按钮添加点击事件响应
		 */
		HelpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ShowOriginalImageLabel.setVisible(true);
				ShowOriginalImageLabel.setText("<html><h1>" + "使用说明：" + " </h1>" + "<h3>1、点击文件,选择一张图片</h3>"
						+ "<h3>2、点击显示原图</h3>" + "<h3>3、选择检索方式</h3>" + "<h3>4、点击上、下一张换图</h3>" + "</html>");

			}
		});                

		/*
		 * 综合检测按钮添加点击事件响应
		 */
        ComprehensiveSearchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ResultPathSet = ComprehensiveSearch.ReturnPathSet(imageAbsolutePath);
					ResultSet = ComprehensiveSearch.ReturnSimilaritySet();
					RollNumber = 0;
					previewRollNumber=1;
					TitleLabel.setVisible(true);
					SearchButtonActionEvent(e);
					TitleLabel.setText("综合检测检索结果:");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/*
		 * 基于形状特征检索事件响应
		 */
		ShapeSearchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PictureShapeSimilarityContrast pssc = new PictureShapeSimilarityContrast();
					ResultPathSet = pssc.ReturnPathSet(imageAbsolutePath);
					ResultSet = PictureShapeSimilarityContrast.ReturnSimilaritySet();
					RollNumber = 0;
					previewRollNumber=1;
					TitleLabel.setVisible(true);
					SearchButtonActionEvent(e);
					TitleLabel.setText("基于形状性检索结果:");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		/*
		 * 基于纹理特征检索事件响应
		 */
		TextureSearchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PictureTextureSimilarityContrast ptsc = new PictureTextureSimilarityContrast();
					ResultPathSet = ptsc.ReturnPathSet(imageAbsolutePath);
					ResultSet = PictureTextureSimilarityContrast.ReturnSimilaritySet();
					RollNumber = 0;
					previewRollNumber=1;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				TitleLabel.setVisible(true);
				SearchButtonActionEvent(e);
				TitleLabel.setText("基于纹理性检索结果:");
			}
		});

		/*
		 * 基于颜色检索事件响应
		 */
		ColourSearchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PictureColorSimilarityContrast psc = new PictureColorSimilarityContrast();
					ResultPathSet = psc.ReturnPathSet(imageAbsolutePath);
					ResultSet = PictureColorSimilarityContrast.ReturnSimilaritySet();
					RollNumber = 0;
					previewRollNumber=1;
					SearchButtonActionEvent(e);
					TitleLabel.setVisible(true);
					TitleLabel.setText("基于颜色检索结果:");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		PreviousImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (RollNumber == 0) {
					PreviousImageButton.setEnabled(false);
				} else {
					RollNumber--;
					previewRollNumber--;
					NextImageButton.setEnabled(true);
					SearchButtonActionEvent(e);
				}
			}
		});

		NextImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (RollNumber == ResultPathSet.length) {
					previewRollNumber=0;
					NextImageButton.setEnabled(false);
				} else {
					RollNumber++;
					previewRollNumber++;
					PreviousImageButton.setEnabled(true);
					SearchButtonActionEvent(e);
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public static void SearchButtonActionEvent(ActionEvent e) {
		try {
			ShowResultSetLabel.setVisible(true);
			PreviousImageButton.setVisible(true);
			NextImageButton.setVisible(true);
			SimilarityDegreeLabel.setVisible(true);
			previewImageLabel.setVisible(true);
			String ExhibitionPath = ResultPathSet[RollNumber];
			String PreviousPath = ResultPathSet[previewRollNumber];
			ImageIcon Previousicon=new ImageIcon(PreviousPath);
			Previousicon.setImage(Previousicon.getImage().getScaledInstance(200, 120, Image.SCALE_DEFAULT));
			SimilarityDegreeLabel.setText("相似度：" + ResultSet[RollNumber]);
			ShowResultSetLabel.setIcon(new ImageIcon(ExhibitionPath));
			previewImageLabel.setIcon(Previousicon);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}

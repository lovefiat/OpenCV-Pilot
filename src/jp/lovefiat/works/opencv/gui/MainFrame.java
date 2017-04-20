package jp.lovefiat.works.opencv.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import jp.lovefiat.works.opencv.ImageOperation;
import jp.lovefiat.works.opencv.ImageOperation.Operator;
import jp.lovefiat.works.opencv.Pilot;

public class MainFrame {
	private static final String FIELD_THRESHOLD1 = "threshold1";

	private static final String FIELD_THRESHOLD2 = "threshold2";

	private static final String FIELD_RHO = "rho";

	private static final String FIELD_THRESHOLD = "threshold";

	private static final String FIELD_MIN_LINE_LENGTH = "minLineLength";

	private static final String FIELD_MAX_LINE_GAP = "maxLineGap";

	/**
	 * 
	 */
	static class MyCanvas extends Canvas {
		Image mImage;
		@Override
		public void paint(Graphics g) {
			if (mImage != null) {
				Graphics2D g2d = (Graphics2D)g;
				double imageWidth = mImage.getWidth(null);
				double imageHeight = mImage.getHeight(null);
				double paneWidth = this.getWidth();
				double paneHeight = this.getHeight();
				double x = (paneWidth / imageWidth);
				double y = (paneHeight / imageHeight);
				
				AffineTransform atf = AffineTransform.getScaleInstance(x, y);
				
				g2d.drawImage(mImage, atf, this);
			} else {
				super.paint(g);
			}
		}
	}
	
	Pilot mPilot;
	
	private String mTitle;
	
	private JFrame mFrame;
	private MyCanvas mMyCanvas;
	private JList<String> mImageList;
	private DefaultListModel<String> mModel;
	private Path mSourceDir;
	private JTabbedPane mTabbedPane;
	
	public MainFrame(String title) {
		mTitle = title;
	}
	public void setPilot(Pilot pilot) {
		mPilot = pilot;
	}
	/**
	 * イメージソースディレクトリを設定
	 * 
	 * @param dir
	 */
	public void setImageSourceDir(Path dir) {
		mSourceDir = dir.toAbsolutePath();
	}
	/**
	 * 表示開始
	 */
	public void start() {
		final int WIDTH = 800;
		final int HEIGHT = 600;
		// メインフレーム
		mFrame = new JFrame(mTitle);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.setBounds(100, 100, WIDTH, HEIGHT);
		// リストのソース
		mModel = new DefaultListModel<>();
		if (mSourceDir == null) {
			mSourceDir = new File("resources").toPath().toAbsolutePath();
		}
		if (mSourceDir != null) {
			updateImageSourceList(mSourceDir.toString());
		}
		// リスト
		mImageList = new JList<>(mModel);
		mImageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mImageList.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				@SuppressWarnings("unchecked")
				JList<String> list = (JList<String>)e.getSource();
				String selected = list.getSelectedValue().toString();
				if (e.getClickCount() == 2) {
					updateImageSourceList(selected);
				} else if (e.getClickCount() == 1) {
					if (testImageFile(selected)) {
						showImage(selected);
					}
				}
			}
		});
		// スクロールパン
		JScrollPane scrollPane = new JScrollPane(mImageList);
		scrollPane.setPreferredSize(new Dimension(150, HEIGHT - 30));
		JPanel panelFileList = new JPanel();
		panelFileList.add(scrollPane);
		// イメージ描画
		mMyCanvas = new MyCanvas();
		mMyCanvas.setSize(new Dimension(WIDTH - 150 - 10, HEIGHT));
		// 制御タブ
		mTabbedPane = new JTabbedPane();
		JPanel tabPanel;
		JLabel label;
		JTextField textField;
		JButton tabButton;
		
		tabPanel = new JPanel();
		tabButton = new JButton("run");
		tabButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageOperation operation = new ImageOperation(Operator.FACE_DETECT);
				accept(operation);
				String selected = mImageList.getSelectedValue();
				Path nextPath = mSourceDir.resolve(selected);
				operation.sourceFile = nextPath.toFile();
				mPilot.flight(operation);
				updateImageSourceList(null);
			}
		});
		tabPanel.add(tabButton, BorderLayout.WEST);

		mTabbedPane.addTab("Haar-Like", tabPanel);

		tabPanel = new JPanel();
		tabButton = new JButton("run");
		tabButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageOperation operation = new ImageOperation(Operator.EDGE_DETECT);
				accept(operation);
				String selected = mImageList.getSelectedValue();
				Path nextPath = mSourceDir.resolve(selected);
				operation.sourceFile = nextPath.toFile();
				mPilot.flight(operation);
				updateImageSourceList(null);
			}
		});
		tabPanel.add(tabButton, BorderLayout.WEST);
		//
		label = new JLabel("threshold1:");
		textField = new JTextField("150");
		textField.setName(FIELD_THRESHOLD1);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("threshold2:");
		textField = new JTextField("100");
		textField.setName(FIELD_THRESHOLD2);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);

		mTabbedPane.addTab("Canny", tabPanel);

		tabPanel = new JPanel();
		tabPanel.setPreferredSize(new Dimension(WIDTH, 70));
//		tabPanel1.setLayout(new FlowLayout());
//		JPanel tabPanel2 = new JPanel();
		tabButton = new JButton("run");
		tabButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageOperation operation = new ImageOperation(Operator.HOUGHT_LINES_P);
				accept(operation);
				String selected = mImageList.getSelectedValue();
				Path nextPath = mSourceDir.resolve(selected);
				operation.sourceFile = nextPath.toFile();
				mPilot.flight(operation);
				updateImageSourceList(null);
			}
		});
		tabPanel.add(tabButton, BorderLayout.WEST);
		//
		label = new JLabel("threshold1:");
		textField = new JTextField("200");
		textField.setName(FIELD_THRESHOLD1);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("threshold2:");
		textField = new JTextField("3");
		textField.setName(FIELD_THRESHOLD2);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("rho:");
		textField = new JTextField("1");
		textField.setName(FIELD_RHO);
		textField.setPreferredSize(new Dimension(30, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("threshold:");
		textField = new JTextField("80");
		textField.setName(FIELD_THRESHOLD);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("minLineLength:");
		textField = new JTextField("30");
		textField.setName(FIELD_MIN_LINE_LENGTH);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);
		//
		label = new JLabel("maxLineGap:");
		textField = new JTextField("10");
		textField.setName(FIELD_MAX_LINE_GAP);
		textField.setPreferredSize(new Dimension(50, textField.getPreferredSize().height));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		tabPanel.add(label, BorderLayout.WEST);
		tabPanel.add(textField, BorderLayout.WEST);

		mTabbedPane.addTab("HoughLinesP", tabPanel);
		
		tabPanel = new JPanel();
		tabButton = new JButton("run");
		tabButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageOperation operation = new ImageOperation(Operator.SIMPLE_BLOB_DETECT);
				accept(operation);
				String selected = mImageList.getSelectedValue();
				Path nextPath = mSourceDir.resolve(selected);
				operation.sourceFile = nextPath.toFile();
				mPilot.flight(operation);
				updateImageSourceList(null);
			}
		});
		tabPanel.add(tabButton, BorderLayout.WEST);
		mTabbedPane.addTab("SimpleBLOB", tabPanel);

		// レイアウト
		mFrame.getContentPane().add(panelFileList, BorderLayout.WEST);
		mFrame.getContentPane().add(mTabbedPane, BorderLayout.NORTH);
		mFrame.getContentPane().add(mMyCanvas, BorderLayout.EAST);
		// 表示
		mFrame.setVisible(true);
	}
	private void accept(ImageOperation imgOpe) {
		JPanel panel = (JPanel) mTabbedPane.getComponentAt(mTabbedPane.getSelectedIndex());
		
		for (Component comp : panel.getComponents()) {
			if (comp.getName() == null) {
				continue;
			}
			switch (comp.getName()) {
			case FIELD_THRESHOLD:
				imgOpe.mThresHold = getIntValue(((JTextField)comp).getText());
				break;
			case FIELD_THRESHOLD1:
				imgOpe.mThresHold1 = getDoubleValue(((JTextField)comp).getText());
				break;
			case FIELD_THRESHOLD2:
				imgOpe.mThresHold2 = getDoubleValue(((JTextField)comp).getText());
				break;
			case FIELD_RHO:
				imgOpe.mRho = getDoubleValue(((JTextField)comp).getText());
				break;
			case FIELD_MAX_LINE_GAP:
				imgOpe.mMaxLineGap = getDoubleValue(((JTextField)comp).getText());
				break;
			case FIELD_MIN_LINE_LENGTH:
				imgOpe.mMinLineLength = getDoubleValue(((JTextField)comp).getText());
				break;
			}
		}
	}
	private int getIntValue(String strValue) {
		int val = 0;
		if (strValue != null) {
			val = Integer.valueOf(strValue);
		}
		return val;
	}
	private double getDoubleValue(String strValue) {
		double val = Double.NaN;
		if (strValue != null) {
			val = Double.valueOf(strValue);
		}
		return val;
	}
	/**
	 * 画像を表示
	 * 
	 * @param selected
	 */
	void showImage(String selected) {
		Path nextPath = mSourceDir.resolve(selected).toAbsolutePath();
		File file = nextPath.toFile();

		try {
			Image img = ImageIO.read(file);
			mMyCanvas.mImage = img;
			mMyCanvas.invalidate();
			mMyCanvas.repaint();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	boolean testImageFile(String string) {
		Path nextPath = mSourceDir.resolve(string);
		File file = nextPath.toFile();
		return (file.exists() && file.isFile());
	}
	
	void updateImageSourceList(String selected) {
		Path nextPath;
		if (selected == null) {
			nextPath = mSourceDir;
		} else if (selected.equals("..")) {
			nextPath = mSourceDir.getParent();
		} else {
			nextPath = mSourceDir.resolve(selected);
		}
		
		File[] files = nextPath.toFile().listFiles();
		
		mModel.clear();
		mModel.addElement("..");
		mSourceDir = nextPath.toAbsolutePath();
		if (files == null) {
			return;
		}
		String name;
		for (File file : files) {
			name = file.getName();
			mModel.addElement(name);
		}
	}

}

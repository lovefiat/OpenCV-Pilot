package jp.lovefiat.works.opencv.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class MainFrame {
	
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
	
	private String mTitle;
	
	private JFrame mFrame;
	private MyCanvas mMyCanvas;
	private JList<String> mImageList;
	private DefaultListModel<String> mModel;
	private Path mSourceDir;
	
	public MainFrame(String title) {
		mTitle = title;
	}
	
	public void setImageSourceDir(Path dir) {
		mSourceDir = dir.toAbsolutePath();
	}
	
	public void start() {
		final int WIDTH = 800;
		final int HEIGHT = 600;
		// メインフレーム
		mFrame = new JFrame(mTitle);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.setBounds(100, 100, WIDTH, HEIGHT);
		//
		mModel = new DefaultListModel<>();
		if (mSourceDir != null) {
			updateImageSourceList(mSourceDir.toString());
		}
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
		JScrollPane scrollPane = new JScrollPane(mImageList);
		scrollPane.setPreferredSize(new Dimension(150, HEIGHT - 30));
		JPanel panel = new JPanel();
		panel.add(scrollPane);
		mMyCanvas = new MyCanvas();
		mMyCanvas.setSize(new Dimension(WIDTH - 150, HEIGHT - 40));
		mFrame.getContentPane().add(panel, BorderLayout.WEST);
		mFrame.getContentPane().add(mMyCanvas, BorderLayout.EAST);
		// 表示
		mFrame.setVisible(true);
	}
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
		Path nextPath = mSourceDir.resolve(selected);
		
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

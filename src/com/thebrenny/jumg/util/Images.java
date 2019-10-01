package com.thebrenny.jumg.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class Images {
	public static HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	public static final BufferedImage NULL_IMAGE;
	
	public static void addImage(String location, String ... names) {
		location = "/" + location.replace(".", "/") + "/";
		for(String name : names) {
			try {
				imageMap.put(name, ImageIO.read(Images.class.getResourceAsStream(location + name + ".png")));
				Logger.log("Registered image [" + name + ".png]");
			} catch(Exception e) {
				Logger.log("Uh oh... There was an error trying to load the following Image:");
				Logger.log("        Name: " + name + ".png");
				Logger.log("    Location: " + location);
				Logger.log("Here's the exception stack trace:");
				e.printStackTrace();
			}
		}
	}
	public static BufferedImage getImage(String name) {
		if(!imageMap.containsKey(name)) {
			Logger.log("Uh oh... I don't think this is right...");
			Logger.log("    You requested the image [" + name + "] but the imageMap doesn't contain that key!");
			Logger.log("    Are you sure you spelt it right?");
		}
		return imageMap.get(name);
	}
	public static BufferedImage getSubImage(BufferedImage image, int multiplier, int x, int y) {
		return getSubImage(image, multiplier, multiplier, x, y, multiplier, multiplier);
	}
	public static BufferedImage getSubImage(BufferedImage image, int multiplierX, int multiplierY, int x, int y, int width, int height) {
		if(multiplierX > image.getWidth() || multiplierX <= 0) {
			multiplierX = image.getWidth();
		}
		if(multiplierY > image.getHeight() || multiplierY <= 0) {
			multiplierY = image.getHeight();
		}
		return image.getSubimage(x * multiplierX, y * multiplierY, width, height);
	}
	public static BufferedImage getResizedImage(BufferedImage image, int width, int height) {
		return Images.toBufferedImage(image.getScaledInstance(width, height, Image.SCALE_FAST));
	}
	public static BufferedImage getResizedImage(BufferedImage image, float multiplier) {
		float newWidth = image.getWidth() * multiplier;
		float newHeight = image.getHeight() * multiplier;
		return Images.toBufferedImage(image.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_FAST));
	}
	public static BufferedImage encode(BufferedImage image, String encoding) {
		Image im = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		try {
			ImageOutputStream ios = ImageIO.createImageOutputStream(im);
			ImageIO.write(image, encoding, ios);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return Images.toBufferedImage(im);
	}
	public static BufferedImage toBufferedImage(Image image) {
		if(image instanceof BufferedImage) return (BufferedImage) image;
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bi;
	}
	public static final BufferedImage recolour(BufferedImage bi, Color color) {
		BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = newBi.createGraphics();
		g2d.drawImage(bi, 0, 0, null);
		g2d.setComposite(AlphaComposite.SrcAtop);
		g2d.setColor(color);
		g2d.fillRect(0, 0, newBi.getWidth(), newBi.getHeight());
		g2d.dispose();
		
		return newBi;
	}
	
	static {
		NULL_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		// I wanna set rendering hints so that it looks a little neater
	}
}

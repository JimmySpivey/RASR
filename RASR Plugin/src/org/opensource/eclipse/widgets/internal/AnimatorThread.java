package org.opensource.eclipse.widgets.internal;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class AnimatorThread extends Thread {
	
	private Color labelBgColor;
	private ImageLoader imageLoader;
	private Label label;
	private Display display;
	private Image image;
	private ImageData[] imageDataArray;
	
	private final boolean useGIFBackground = false;

	/**
	 * Thread for animating GIF graphics on SWT labels.
	 * 
	 * @param label
	 * @param pluginName eg: ATF_Recorder_Plugin
	 * @param imageBundleLoc eg: icons/image.gif
	 */
	public AnimatorThread(Label label, String pluginName, String imageBundleLoc) {
		super();
		
		this.label = label;
		
		display = label.getDisplay();
		labelBgColor = label.getBackground();
		image = null;
		imageLoader = new ImageLoader();
		try {
			imageDataArray = imageLoader.load(FileLocator.find(
					Platform.getBundle(pluginName), 
					new Path(imageBundleLoc), 
					null).openStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		/* Create an off-screen image to draw on, and fill it with the shell background. */
		Image offScreenImage = new Image(display, imageLoader.logicalScreenWidth, imageLoader.logicalScreenHeight);
		GC offScreenImageGC = new GC(offScreenImage);
		offScreenImageGC.setBackground(labelBgColor);
		offScreenImageGC.fillRectangle(0, 0, imageLoader.logicalScreenWidth, imageLoader.logicalScreenHeight);
		

		
		try {
			/* Create the first image and draw it on the off-screen image. */
			int imageDataIndex = 0;	
			ImageData imageData = imageDataArray[imageDataIndex];
			if (image != null && !image.isDisposed()) image.dispose();
			image = new Image(display, imageData);
			offScreenImageGC.drawImage(
				image,
				0,
				0,
				imageData.width,
				imageData.height,
				imageData.x,
				imageData.y,
				imageData.width,
				imageData.height);

			/* Now loop through the images, creating and drawing each one
			 * on the off-screen image before drawing it on the shell. */
			int repeatCount = imageLoader.repeatCount;
			while (imageLoader.repeatCount == 0 || repeatCount > 0) {
				switch (imageData.disposalMethod) {
				case SWT.DM_FILL_BACKGROUND:
					/* Fill with the background color before drawing. */
					Color bgColor = null;
					if (useGIFBackground && imageLoader.backgroundPixel != -1) {
						bgColor = new Color(display, imageData.palette.getRGB(imageLoader.backgroundPixel));
					}
					offScreenImageGC.setBackground(bgColor != null ? bgColor : labelBgColor);
					offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width, imageData.height);
					if (bgColor != null) bgColor.dispose();
					break;
				case SWT.DM_FILL_PREVIOUS:
					/* Restore the previous image before drawing. */
					offScreenImageGC.drawImage(
						image,
						0,
						0,
						imageData.width,
						imageData.height,
						imageData.x,
						imageData.y,
						imageData.width,
						imageData.height);
					break;
				}
									
				imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
				imageData = imageDataArray[imageDataIndex];
				image.dispose();
				image = new Image(display, imageData);
				offScreenImageGC.drawImage(
					image,
					0,
					0,
					imageData.width,
					imageData.height,
					imageData.x,
					imageData.y,
					imageData.width,
					imageData.height);
				
				/* Draw the off-screen image to the shell. */
				label.setImage(offScreenImage);
				
				/* Sleep for the specified delay time (adding commonly-used slow-down fudge factors). */
				try {
					int ms = imageData.delayTime * 10;
					if (ms < 20) ms += 30;
					if (ms < 30) ms += 10;
					Thread.sleep(ms);
				} catch (InterruptedException e) {
				}
				
				/* If we have just drawn the last image, decrement the repeat count and start again. */
				if (imageDataIndex == imageDataArray.length - 1) repeatCount--;
			}
		} catch (SWTException ex) {
			System.out.println("There was an error animating the GIF");
		} finally {
			if (offScreenImage != null && !offScreenImage.isDisposed()) offScreenImage.dispose();
			if (offScreenImageGC != null && !offScreenImageGC.isDisposed()) offScreenImageGC.dispose();
			if (image != null && !image.isDisposed()) image.dispose();
		}
	}
}
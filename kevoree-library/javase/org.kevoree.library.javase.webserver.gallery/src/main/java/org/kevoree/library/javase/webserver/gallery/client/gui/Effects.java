package org.kevoree.library.javase.webserver.gallery.client.gui;

import org.adamtacy.client.ui.effects.impl.Fade;

import com.google.gwt.user.client.ui.Image;

public class Effects extends GUI {

	public static void stopSlideshow() {
		
	}
	
	public static void startSlideshow() {
		
	}
	
	public static void ThumbnailMouseOver(Image Thumbnail) {
		Fade theFade = new Fade(Thumbnail.getElement());
		theFade.setStartOpacity(60);
		theFade.setEndOpacity(100);
		theFade.setDuration(0.4);
		theFade.play();
	}
	
	public static void ThumbnailMouseOut(Image Thumbnail) {
		Fade theFade = new Fade(Thumbnail.getElement());
		theFade.setStartOpacity(100);
		theFade.setEndOpacity(60);
		theFade.setDuration(0.4);
		theFade.play();
	}
	
}

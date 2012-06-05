package org.kevoree.library.javase.webserver.gallery.client.extensions;

import org.kevoree.library.javase.webserver.gallery.client.gui.Action;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

// TODO get slideshow workin
public class Slideshow extends Action {

	static int slideShowRunning;
	static Timer t = null;
	
	public static void handle() {
			if(slideShowRunning == 1) 
				stopSlideshow();
			else 
				startSlideshow();
	}
	
	static void startSlideshow() {
		slideShowRunning = 1;
		//SLIDESHOW.setVisible(true);
		SLIDESHOW.clear();
		SLIDESHOW.add(new Label("start"));
		
		t = new Timer() {
			public void run() {
				/* has got to stop as soon as all the images are done or whenever a event is fired - therefore check EventHandler */
				if(CONTENT.getVisibleWidget() != 0) {
					stopSlideshow();
				} else {
					Action.changeThumbnail("next");
				}
		    }
		};
		t.scheduleRepeating(4000);
	}
	
	public static void stopSlideshow() {
		slideShowRunning = 0;
		SLIDESHOW.clear();
		SLIDESHOW.add(new Label("stop"));
		
		if(t != null) {
			t.cancel();
		}
		
	}
	
}

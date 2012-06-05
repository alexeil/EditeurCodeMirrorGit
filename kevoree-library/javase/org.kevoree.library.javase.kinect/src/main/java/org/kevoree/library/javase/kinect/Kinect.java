package org.kevoree.library.javase.kinect;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;
import org.kevoree.framework.MessagePort;
import org.kevoree.framework.message.StdKevoreeMessage;
import org.openkinect.freenect.*;
import org.openkinect.freenect.util.Jdk14LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

/**
 * User: Erwan Daubert - erwan.daubert@gmail.com
 * Date: 16/08/11
 * Time: 17:42
 */
@MessageTypes({
		@MessageType(name = "BufferedImage", elems = {@MsgElem(name = "image", className = BufferedImage.class)}),
		@MessageType(name = "bytes", elems =
				{@MsgElem(name = "bytes", className = byte[].class),
						@MsgElem(name = "width", className = Integer.class),
						@MsgElem(name = "height", className = Integer.class),
						@MsgElem(name = "chroma", className = String.class),
						@MsgElem(name = "fps", className = Integer.class)}
		)
})
@Requires({
		@RequiredPort(name = "image", type = PortType.MESSAGE, optional = true, messageType = "BufferedImage"),
		@RequiredPort(name = "image_bytes", type = PortType.MESSAGE, optional = true, messageType = "bytes")
})
@Provides({
		@ProvidedPort(name = "motor", type = PortType.MESSAGE)
		//@ProvidedPort(name = "led", type = PortType.MESSAGE, filter = {"java.lang.Integer", "java.lang.String"}) TODO
})
@DictionaryType({
		@DictionaryAttribute(name = "FORMAT", defaultValue = "RGB", optional = true,
				vals = {"DEPTH_11BIT", "DEPTH_10BIT", "DEPTH_11BIT_PACKED", "DEPTH_10BIT_PACKED", "RGB",
						"IR_8BIT", "IR_10BIT", "IR_10BIT_PACKED", "BAYER", "YUV_RGB", "YUV_RAW"}),
		@DictionaryAttribute(name = "FPS", defaultValue = "30", optional = true, vals = {"1", "10", "15", "24", "30"}),
		@DictionaryAttribute(name = "DEVICE_ID", defaultValue = "0", optional = true,
				vals = {"0", "1", "2", "3", "4", "5"}),
		@DictionaryAttribute(name = "LOG_LEVEL", defaultValue = "ERROR", optional = true,
				vals = {"ERROR", "DEBUG", "INFO", "ALL"})
})
@Library(name = "JavaSE")
@ComponentType
public class Kinect extends AbstractComponentType {

	private Context ctx;
	private Device dev;

	private static NativeLibrary instance;
	private static int nbComponent;

	private static final Logger logger = LoggerFactory.getLogger(Kinect.class);

	@Start
	public void start () throws Exception {
		if (instance == null) {
			String path = KinectNativeLibraryLoader.configure();
			if (KinectNativeLibraryLoader.isMac()) {
				logger.debug("OSX load usb lib");
				NativeLibrary.addSearchPath("usb", path);
				NativeLibrary.getInstance("usb");
			}
			NativeLibrary.addSearchPath("freenect", path);
			instance = NativeLibrary.getInstance("freenect");
			nbComponent++;
			Native.register(Freenect.class, instance);
		}

		ctx = Freenect.createContext();
		ctx.setLogHandler(new Jdk14LogHandler());
		ctx.setLogLevel(getLogLevel());
		int devideId = this.getDeviceId();

		if (ctx.numDevices() > devideId) {
			dev = ctx.openDevice(devideId);
			dev.setLed(LedStatus.GREEN);
			if (isDepth()) {
				dev.setDepthFormat(buildDepthFormat());
				dev.startDepth(new DepthHandler() {
					BufferedImage image;
					byte[] bytes;
					long lastTimeStamp = 0;
					long delay = getDelay();

					@Override
					public synchronized void onFrameReceived (DepthFormat format, ByteBuffer frame, int timestamp) {
						if (lastTimeStamp + delay < System.currentTimeMillis()) {
							if (isPortBinded("image")) {
								// FIXME the cost is too high
								//int[] pixels = new int[format.getWidth() * format.getHeight()];
								if (image == null) {
									/*image = DirectBufferedImage
											.getDirectImageRGB(format.getWidth(), format.getHeight());*/
									image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
											.getDefaultConfiguration()
											.createCompatibleImage(format.getWidth(), format.getHeight());
								}
								//Graphics2D graphics = (Graphics2D)image.getGraphics();
								for (int y = 0; y < format.getHeight(); y++) {
									for (int x = 0; x < format.getWidth(); x++) {
										// FIXME is not totally good
										int offset = 2 * (y * format.getWidth() + x);

										short d0 = frame.get(offset);
										short d1 = frame.get(offset + 1);

										int pixel = d1 << 8 | d0;

										//pixel = depth2intensity(pixel);
										//int pixel = depth2rgb(d);
										//int pixel = buffer.get(offset);
										image.setRGB(x, y, pixel);
										// TODO use raster like the VideoHandler
									}
								}
								image.getGraphics().dispose();
								//image.setRGB(0, 0, format.getWidth(), format.getHeight(), pixels, 0, format.getWidth());
								StdKevoreeMessage msg = new StdKevoreeMessage();
								msg.putValue("image", image);
								getPortByName("image", MessagePort.class).process(msg);
//								getPortByName("image", MessagePort.class).process(image);
							}
							if (isPortBinded("image_bytes")) {
								if (bytes == null) {
									bytes = new byte[format.getHeight()*format.getWidth() * 2];
								}
								frame.get(bytes, 0, bytes.length);

								// TODO
							}
							lastTimeStamp = System.currentTimeMillis();
						}
						frame.position(0);
					}
				});
			} else {
				dev.setVideoFormat(buildVideoFormat());
				dev.startVideo(new VideoHandler() {
					BufferedImage image;
					WritableRaster raster;
					long lastTimeStamp = 0;
					long delay = getDelay();
					int[] bgr = new int[3];

					@Override
					public synchronized void onFrameReceived (VideoFormat format, ByteBuffer frame, int timestamp) {
						// timestamp is not a timestamp like the one returned by System.currentTimeMillis()
						if (lastTimeStamp + delay < System.currentTimeMillis()) {
							/*if (isPortBinded("raw")) {
								Map<String, Object> dic = new HashMap<String, Object>();
								dic.put("frame", frame);
								dic.put("format", format);
								getPortByName("raw", MessagePort.class).process(dic);
							}*/
							if (isPortBinded("image")) {
								if (image == null) {
									image = new BufferedImage(format.getWidth(), format.getHeight(),
											BufferedImage.TYPE_3BYTE_BGR);
									raster = image.getRaster();
								}
								for (int y = 0; y < format.getHeight(); y++) {
									for (int x = 0; x < format.getWidth(); x++) {
										int offset = 3 * (y * format.getWidth() + x);
										bgr[2] = frame.get(offset + 2) & 0xFF;
										bgr[1] = frame.get(offset + 1) & 0xFF;
										bgr[0] = frame.get(offset) & 0xFF;

										raster.setPixel(x, y, bgr);
									}
								}
								image.getGraphics().dispose();

								StdKevoreeMessage msg = new StdKevoreeMessage();
								msg.putValue("image", image);
								getPortByName("image", MessagePort.class).process(msg);
								//								getPortByName("image", MessagePort.class).process(image);
							}
							lastTimeStamp = System.currentTimeMillis();
						}
					}
				});
			}
		} else {
			logger.debug("Kinect not connected !");
			stop();
			throw new Exception("Fail to start " + this.getName());
		}
	}

	@Stop
	public void stop () {
		if (dev != null) {
			dev.setLed(LedStatus.OFF);
			if (isDepth()) {
				dev.stopDepth();
			} else {
				dev.stopVideo();
			}
		}
		if (ctx != null) {
			if (dev != null) {
				dev.close();
			}
			ctx.shutdown();
		}

		if (instance != null && nbComponent == 0) {
			Native.unregister(Freenect.class);
			instance.dispose();
		}
	}

	@Update
	public void update () throws Exception {
		stop();
		start();
	}

	@Port(name = "motor")
	public void onReceiveMessage (Object message) {

		try {

			int percentage = 50;
			if (message instanceof StdKevoreeMessage) {
				percentage = (Integer) ((StdKevoreeMessage) message).getValue("percent").get();
				move(percentage);
			}
			if (message instanceof Integer) {
				percentage = (Integer) message;
				move(percentage);
			}
			if (message instanceof String) {
				if (message.toString().startsWith("percent=")) {
					percentage = Integer.parseInt(message.toString().replace("percent=", ""));
				} else {
					percentage = Integer.parseInt((String) message);
				}
				move(percentage);
			}
		} catch (Exception e) {
			logger.debug("Bad message rec ", e);
		}
	}

	private int getDeviceId () {
		logger.debug("");
		return Integer.parseInt((String) this.getDictionary().get("DEVICE_ID"));


	}

	private LogLevel getLogLevel () {
		String logLevelAttribute = (String) this.getDictionary().get("LOG_LEVEL");
		if (logLevelAttribute.equals("ERROR")) {
			return LogLevel.ERROR;
		} else if (logLevelAttribute.equals("DEBUG")) {
			return LogLevel.DEBUG;
		} else if (logLevelAttribute.equals("INFO")) {
			return LogLevel.INFO;
		} else if (logLevelAttribute.equals("ALL")) {
			return LogLevel.SPEW;
		} else {
			return LogLevel.ERROR;
		}
	}

	private boolean isDepth () {
		String format = (String) this.getDictionary().get("FORMAT");
		return format != null && format.toLowerCase().contains("depth");
	}

	private DepthFormat buildDepthFormat () {
		String format = (String) this.getDictionary().get("FORMAT");
		if (format.equalsIgnoreCase("DEPTH_11BIT")) {
			return DepthFormat.D11BIT;
		} else if (format.equalsIgnoreCase("DEPTH_10BIT")) {
			return DepthFormat.D10BIT;
		} else if (format.equalsIgnoreCase("DEPTH_11BIT_PACKED")) {
			return DepthFormat.D11BIT_PACKED;
		} else if (format.equalsIgnoreCase("DEPTH_10BIT_PACKED")) {
			return DepthFormat.D10BIT_PACKED;
		} else {
			return DepthFormat.D11BIT;
		}
	}

	private VideoFormat buildVideoFormat () {
		String format = (String) this.getDictionary().get("FORMAT");
		if (format.equalsIgnoreCase("RGB")) {
			return VideoFormat.RGB;
		} else if (format.equalsIgnoreCase("IR_8BIT")) {
			return VideoFormat.IR_8BIT;
		} else if (format.equalsIgnoreCase("IR_10BIT")) {
			return VideoFormat.IR_10BIT;
		} else if (format.equalsIgnoreCase("IR_10BIT_PACKED")) {
			return VideoFormat.IR_10BIT_PACKED;
		} else if (format.equalsIgnoreCase("BAYER")) {
			return VideoFormat.BAYER;
		} else if (format.equalsIgnoreCase("YUV_RGB")) {
			return VideoFormat.YUV_RGB;
		} else if (format.equalsIgnoreCase("YUV_RAW")) {
			return VideoFormat.YUV_RAW;
		} else {
			return VideoFormat.RGB;
		}
	}

	private int getDelay () {
		String fps = (String) this.getDictionary().get("FPS");
		int delay = 0;
		if (!fps.equals("max")) {
			try {
				delay = Integer.parseInt(fps);
				delay = 1000 / delay;
			} catch (NumberFormatException e) {
				logger.warn("FPS attribute must be an int or \"max\"!");
			}
		}
		return delay;
	}

	private Image translateImage (VideoFormat format, ByteBuffer frame) {
		// TODO translate the various format
		return null;
	}

	private Image translateImage (DepthFormat format, ByteBuffer frame) {
		// TODO translate the various format
		return null;
	}

	private void move (int percentage) {
		if (percentage > 100) {
			percentage = 100;
		} else if (percentage < 0) {
			percentage = 0;
		}
		int angle = (int) ((percentage * ((float) 58 / 100)) - 29);
		dev.setTiltAngle(angle);
		while (dev.getTiltStatus() == TiltStatus.MOVING) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.warn("sleep the thread is not allowed here !");
			}
			dev.refreshTiltState();
		}
		dev.refreshTiltState();
		// TODO log
	}
}

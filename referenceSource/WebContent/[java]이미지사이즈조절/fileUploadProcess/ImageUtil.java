/**
 * 이미지 변환
 * 
 * 사용방법 : 변환할 이미지 파일경로, 저장할 파일경로, 길이, 너비 순으로 입력
 * 1. 현재 사이즈를 유지하려면 HOLD(-99) 값을 입력
 * 2. 너비 또는 높이 중 하나를 입력하고 남은 하나는 동일한 비율로 리사이즈하려면 RATIO(0) 값을 입력
 * 3. 지원가능한 OUTPUT_FORMAT jpg, png, gif
 * 
 * @since Book and Life  
 * @version 1.0 
 * @author  최초작성일: 2015. 7. 15. Administrator
 */

class ImageUtil {
	public static final int RATIO = 0;
	public static final int HOLD = -99;
	
	private static String OUTPUT_FORMAT = "jpg";
	private static String[] IO_READ_IMAGE = new String[] { "PNG", "GIF", "BMP" };


	public static void createNewImage(File src, File dest, int width, int height) throws IOException {
		Image image = null;
		String extention = src.getName().substring(src.getName().lastIndexOf('.') + 1).toUpperCase();
		boolean ioRead = false;
		for (int i = 0; i < IO_READ_IMAGE.length; i++) {
			if (extention.equals(IO_READ_IMAGE[i])) {
				ioRead = true;
				break;
			}
		}
		if (ioRead) {
			image = ImageIO.read(src);
		} else {
			image = new ImageIcon(src.toURL()).getImage();
		}

		int srcWidth = image.getWidth(null);
		int srcHeight = image.getHeight(null);

		int destWidth = -1, destHeight = -1;

		if (width == HOLD) {
			destWidth = srcWidth;
		} else if (width > 0) {
			destWidth = width;
		}

		if (height == HOLD) {
			destHeight = srcHeight;
		} else if (height > 0) {
			destHeight = height;
		}

		if (width == RATIO && height == RATIO) {
			destWidth = srcWidth;
			destHeight = srcHeight;
		} else if (width == RATIO) {
			double ratio = ((double) destHeight) / ((double) srcHeight);
			destWidth = (int) ((double) srcWidth * ratio);
		} else if (height == RATIO) {
			double ratio = ((double) destWidth) / ((double) srcWidth);
			destHeight = (int) ((double) srcHeight * ratio);
		}

		Image imgTarget = image.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
		int pixels[] = new int[destWidth * destHeight];
		PixelGrabber pixelGrabber = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth);
		try {
			pixelGrabber.grabPixels();
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		}
		BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
		destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);
		ImageIO.write(destImg, OUTPUT_FORMAT, dest);
	}



}
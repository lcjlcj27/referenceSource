/**
 * �̹��� ��ȯ
 * 
 * ����� : ��ȯ�� �̹��� ���ϰ��, ������ ���ϰ��, ����, �ʺ� ������ �Է�
 * 1. ���� ����� �����Ϸ��� HOLD(-99) ���� �Է�
 * 2. �ʺ� �Ǵ� ���� �� �ϳ��� �Է��ϰ� ���� �ϳ��� ������ ������ ���������Ϸ��� RATIO(0) ���� �Է�
 * 3. ���������� OUTPUT_FORMAT jpg, png, gif
 * 
 * @since Book and Life  
 * @version 1.0 
 * @author  �����ۼ���: 2015. 7. 15. Administrator
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
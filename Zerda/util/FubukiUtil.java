package tw.fubuki.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

public class FubukiUtil {
	
	public static String getExtendName(String completeFileName) {
		return completeFileName.substring(completeFileName.lastIndexOf("."), completeFileName.length());
	}
	
	public static String getExtendNameNoDot(String completeFileName) {
		return completeFileName.substring(completeFileName.lastIndexOf(".")+1, completeFileName.length());
	}
	
	public static String getFileNameNotWithExtend(String completeFileName) {
		return completeFileName.substring(0, completeFileName.lastIndexOf("."));
	}
	
	public static String getMd5String() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String insertExitsString(int index, String origin, String text) {
		StringBuffer szBuffer = new StringBuffer(origin);
		szBuffer.insert(index, text);
		return szBuffer.toString();
	}
	
	public static byte[] processImage(MultipartFile multipartFile) throws IOException {
		BufferedImage bufImage = ImageIO.read(multipartFile.getInputStream());
		if (bufImage == null) return null;
		
		String fileName = multipartFile.getOriginalFilename();
		return processImageSize(bufImage, getExtendNameNoDot(fileName));
	}
	
	public static byte[] processImageSize(BufferedImage bufImage, String extendFileName) throws IOException {
		
		final int MAXWIDTH = 2000;
		final int MAXHEIGHT = 2000;
		final int GAP = 10;
		final int originWidth = bufImage.getWidth();
		final int originHeight = bufImage.getHeight();
		int Width = originWidth;
		int Height = originHeight;

		if ( Width > MAXWIDTH || Height > MAXHEIGHT || Width-Height >= GAP || Height-Width >=GAP ) {
		
			if (Width > MAXWIDTH || Height > MAXHEIGHT) {
				if (Width > MAXWIDTH) Width = MAXWIDTH;
				if (Height > MAXHEIGHT) Height = MAXHEIGHT;
			}
			if (Width-Height >= GAP || Height-Width >=GAP) {
				if (Width > Height) Width = Height;
				else if ( Height > Width) Height = Width;
			}
			
			int x = (originWidth - Width) / 2;
			int y = (originHeight - Height) / 2;
			bufImage = bufImage.getSubimage(x, y, Width, Height);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufImage, extendFileName, out);
		return out.toByteArray();
	}

	public static void uploadStaticFile(String savePath, String filename, byte[] picture) throws IOException {
		System.out.println(savePath);
		File saveDir = new File(savePath);
		if ( !saveDir.exists() )
			saveDir.mkdir();

		File saveFile = new File(saveDir, filename);
		ByteArrayInputStream bais = new ByteArrayInputStream(picture);
		BufferedImage bi1 = ImageIO.read(bais); 
		ImageIO.write(bi1, FubukiUtil.getExtendNameNoDot(filename), saveFile);
	}
	
	public static void deleteStaticFile(String savePath, String filename) {

		File saveFile = new File(savePath, filename);
		if (!saveFile.delete())
			System.out.println("刪除失敗: " + filename);

	}
	
	public static java.sql.Date getCurrentSqlDate() {
		Date utilDate = new Date();
		return new java.sql.Date(utilDate.getTime());
	}
	
	public static String generateUUIDfileName(String originName) {
		String tempExtendName = FubukiUtil.getExtendName(originName);
		String updateName = FubukiUtil.getMd5String();
		return updateName.concat(tempExtendName);
	}
	
	public static byte[] createThumbnail(String 桜みこ, byte[] 湊あくあ, final int うなぎ) throws IOException {
		final char ドン = '.';final byte 一 = 1;final byte 零 = 0;final double 簾 = 0.0f;
		ByteArrayInputStream 白上フブキ = new ByteArrayInputStream(湊あくあ);
		BufferedImage 角巻わため = ImageIO.read(白上フブキ);
		String 紫咲シオン = 桜みこ.substring(桜みこ.lastIndexOf(ドン) + 一, 桜みこ.length());
		
		int 広さ = 角巻わため.getWidth();
		int 高さ = 角巻わため.getHeight();
		double 倍率 = 簾;
		if (広さ > うなぎ) 倍率 = (double) うなぎ / 広さ;
		else if (高さ > うなぎ) 倍率 = (double) うなぎ / 高さ;
		
		if( 倍率 > 簾 ) {
			BufferedImage 夏色まつり = new BufferedImage((int) (広さ * 倍率), (int) (高さ * 倍率), 角巻わため.getType());
		    Graphics2D 絵器 = 夏色まつり.createGraphics();
		    絵器.drawImage(角巻わため, 零, 零, (int) (広さ * 倍率), (int) (高さ * 倍率), null);
		    絵器.dispose();
		    ByteArrayOutputStream 兎田ぺこら = new ByteArrayOutputStream();
			ImageIO.write(夏色まつり, 紫咲シオン, 兎田ぺこら);
			return 兎田ぺこら.toByteArray();
		} else 
			return 湊あくあ;
	}
}


















//try {
//MessageDigest md;
//md = MessageDigest.getInstance("MD5");
//md.update(origin.getBytes());
//return new BigInteger(1, md.digest()).toString(16);
//} catch (NoSuchAlgorithmException e) {
//e.printStackTrace();
//}
//return null;

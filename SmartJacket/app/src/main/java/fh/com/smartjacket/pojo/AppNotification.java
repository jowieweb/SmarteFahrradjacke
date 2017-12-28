package fh.com.smartjacket.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by nils on 21.12.17.
 */

public class AppNotification implements Serializable {
	private String appName;
	private String appPackageName;
	private String appIcon;

	public AppNotification(String appName, String appPackageName, Drawable appIcon) {
		this.appName = appName;
		this.appPackageName = appPackageName;
		this.appIcon = encodeToBase64(drawableToBitmap(appIcon));
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppPackageName() {
		return this.appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	public Bitmap getAppIcon() {
		//return this.appIcon;
		return decodeBase64(appIcon);
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = encodeToBase64(drawableToBitmap(appIcon));
	}

	/**
	 * SOURCE https://stackoverflow.com/a/10600736
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitmap (Drawable drawable) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if(bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}


		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		//return bitmap;



		int [] allpixels = new int [bitmap.getHeight() * bitmap.getWidth()];

		bitmap.getPixels(allpixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		for(int i = 0; i < allpixels.length; i++)
		{
			int c = allpixels[i];
			if(allpixels[i] == Color.TRANSPARENT)
			{
				allpixels[i] = Color.WHITE;
			}
		}

		bitmap.setPixels(allpixels,0,bitmap.getWidth(),0, 0, bitmap.getWidth(),bitmap.getHeight());

		return  bitmap;
	}







	/**
	 * source https://stackoverflow.com/a/20656758
	 * @param image
	 * @return
	 */
	private String encodeToBase64(Bitmap image)
	{
		Bitmap immagex=image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);


		return imageEncoded;
	}

	private Bitmap decodeBase64(String input)
	{
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}

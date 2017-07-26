package com.example.retrievevideo.emergencymessaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class blurImageFunction {
	 private static final float BITMAP_SCALE = 0.4f;
	 private static final float BLUR_RADIUS = 7.5f;
	 
	  
	 public  Bitmap blur(Context context, Bitmap image) {
	        int width = Math.round(image.getWidth() * BITMAP_SCALE);
	        int height = Math.round(image.getHeight() * BITMAP_SCALE);

	        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
	        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

	        RenderScript rs = RenderScript.create(context);
	        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
	        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
	        theIntrinsic.setRadius(BLUR_RADIUS);
	        theIntrinsic.setInput(tmpIn);
	        theIntrinsic.forEach(tmpOut);
	        tmpOut.copyTo(outputBitmap);

	        return outputBitmap;
	    }
	 /*public Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {

	        try {
	            smallBitmap = RGB565toARGB888(smallBitmap);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }


	        Bitmap bitmap = Bitmap.createBitmap(
	                smallBitmap.getWidth(), smallBitmap.getHeight(),
	                Bitmap.Config.ARGB_8888);

	        RenderScript renderScript = RenderScript.create(context);

	        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
	        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

	        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
	                Element.U8_4(renderScript));
	        blur.setInput(blurInput);
	        blur.setRadius(radius); // radius must be 0 < r <= 25
	        blur.forEach(blurOutput);

	        blurOutput.copyTo(bitmap);
	        renderScript.destroy();

	        return bitmap;

	    }
	 private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
	        int numPixels = img.getWidth() * img.getHeight();
	        int[] pixels = new int[numPixels];

	        //Get JPEG pixels.  Each int is the color values for one pixel.
	        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

	        //Create a Bitmap of the appropriate format.
	        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

	        //Set RGB pixels.
	        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
	        return result;
	    }*/
	 
}

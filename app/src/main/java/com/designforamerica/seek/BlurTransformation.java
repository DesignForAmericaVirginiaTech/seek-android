package com.designforamerica.seek;

/**
 * Code taken from here:
 * https://github.com/TannerPerrien/picasso-transformations
 *
 * I could not get the maven deployer to work so instead of grabbing their library I had to go in
 * and just take this one file
 *
 * All credit to TannerPerrien
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {

    private Context mContext;

    private int mRadius;

    /**
     * Constructor.
     *
     * @param context The context.
     * @param radius The blur radius: (0, 25]
     */
    public BlurTransformation(Context context, int radius) {
        mContext = context;
        mRadius = radius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (Build.VERSION.SDK_INT < 17) {
            return source;
        }

        RenderScript rs = RenderScript.create(mContext);
        Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(mRadius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(source);
        return source;
    }

    @Override
    public String key() {
        return BlurTransformation.class.getCanonicalName() + "-" + mRadius;
    }

}
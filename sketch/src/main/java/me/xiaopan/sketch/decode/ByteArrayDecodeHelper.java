/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.util.SketchUtils;

public class ByteArrayDecodeHelper implements DecodeHelper {
    protected String logName = "ByteArrayDecodeHelper";

    private byte[] data;
    private LoadRequest loadRequest;

    public ByteArrayDecodeHelper(byte[] data, LoadRequest loadRequest) {
        this.data = data;
        this.loadRequest = loadRequest;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Override
    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }

        BitmapRegionDecoder regionDecoder;
        try {
            regionDecoder = BitmapRegionDecoder.newInstance(data, 0, data.length, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = regionDecoder.decodeRegion(srcRect, options);
        regionDecoder.recycle();
        return bitmap;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, int outWidth, int outHeight, String outMimeType, int inSampleSize) {
        if (Sketch.isDebugMode()) {
            StringBuilder builder = new StringBuilder(logName)
                    .append(". decodeSuccess");
            if (bitmap != null && loadRequest.getOptions().getMaxSize() != null) {
                MaxSize maxSize = loadRequest.getOptions().getMaxSize();
                ImageSizeCalculator sizeCalculator = loadRequest.getSketch().getConfiguration().getImageSizeCalculator();
                builder.append(". originalSize=").append(outWidth).append("x").append(outHeight);
                builder.append(", targetSize=").append(maxSize.getWidth()).append("x").append(maxSize.getHeight());
                builder.append(", targetSizeScale=").append(sizeCalculator.getTargetSizeScale());
                builder.append(", inSampleSize=").append(inSampleSize);
                builder.append(", finalSize=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            } else {
                builder.append(". unchanged");
            }
            builder.append(". ").append(loadRequest.getId());
            Log.d(Sketch.TAG, builder.toString());
        }
    }

    @Override
    public void onDecodeError() {
        if (Sketch.isDebugMode()) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName, ". decode failed", ". ", loadRequest.getId()));
        }
    }

    @Override
    public SketchGifDrawable getGifDrawable() {
        try {
            return new SketchGifDrawable(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

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

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.Resize;

public class ResizeImageProcessor implements ImageProcessor {

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append("ResizeImageProcessor");
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        if (resize == null || (bitmap.getWidth() == resize.getWidth() && bitmap.getHeight() == resize.getHeight())) {
            return bitmap;
        }

        ResizeCalculator resizeCalculator = sketch.getConfiguration().getResizeCalculator();
        ResizeCalculator.Result result = resizeCalculator.calculator(bitmap.getWidth(), bitmap.getHeight(),
                resize.getWidth(), resize.getHeight(), resize.getScaleType(), forceUseResize);
        if (result == null) {
            return bitmap;
        }

        Bitmap.Config newBitmapConfig = bitmap.getConfig();
        if (newBitmapConfig == null) {
            newBitmapConfig = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        }
        Bitmap newBitmap = Bitmap.createBitmap(result.imageWidth, result.imageHeight, newBitmapConfig);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, null);
        return newBitmap;
    }
}

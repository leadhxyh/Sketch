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

package me.xiaopan.sketch.display;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;

import me.xiaopan.sketch.request.ImageViewInterface;
import pl.droidsonroids.gif.GifDrawable;

/**
 * 过渡效果的图片显示器
 */
public class TransitionImageDisplayer implements ImageDisplayer {
    protected String logName = "TransitionImageDisplayer";

    private int duration;
    private boolean alwaysUse;

    public TransitionImageDisplayer(int duration) {
        this.duration = duration;
    }

    public TransitionImageDisplayer() {
        this(400);
    }

    @Override
    public void display(ImageViewInterface imageViewInterface, Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        if (newDrawable instanceof GifDrawable) {
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(newDrawable);
        } else {
            Drawable oldDrawable = imageViewInterface.getDrawable();
            if (oldDrawable == null) {
                oldDrawable = new ColorDrawable(Color.TRANSPARENT);
            }
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldDrawable, newDrawable});
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(duration);
        }
    }

    @SuppressWarnings("unused")
    public TransitionImageDisplayer setAlwaysUse(boolean alwaysUse) {
        this.alwaysUse = alwaysUse;
        return this;
    }

    @Override
    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName)
                .append("(")
                .append("duration=").append(duration)
                .append(", alwaysUse=").append(alwaysUse)
                .append(")");
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

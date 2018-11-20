package com.example.pbose.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;

import java.util.List;

/**
 * Created by pbose on 4/3/16.
 */
public class BulletTextUtil {

    /**
     * Returns a CharSequence containing a bulleted and properly indented list.
     *
     * @param leadingMargin In pixels, the space between the left edge of the bullet and the left edge of the text.
     * @param lines An array of CharSequences. Each CharSequences will be a separate line/bullet-point.
     * @return
     */
    public CharSequence makeBulletList(int leadingMargin, List<String> lines) {
        SpannableStringBuilder sb = new SpannableStringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            CharSequence line = lines.get(i) + (i < lines.size()-1 ? "\n" : "");
            Spannable spannable = new SpannableString(line);
            spannable.setSpan(new BulletSpan(leadingMargin), 0, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sb.append(spannable);
        }
        return sb;
    }

}

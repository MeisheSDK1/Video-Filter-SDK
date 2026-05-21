package com.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsFx;

import java.util.Arrays;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/3/10 10:29
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class FaceAvatarView extends LinearLayout {
    public static final boolean IS_SHOW = false;
    private TextView textLeftValue;
    private TextView textRightValue;

    public FaceAvatarView(Context context) {
        this(context, null);
    }

    public FaceAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextView(context);
    }

    private void initTextView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        TextView textLeftName = new TextView(context);
        createNameText(textLeftName);

        textLeftValue = new TextView(context);
        createValueText(textLeftValue);

        TextView textRightName = new TextView(context);
        createNameText(textRightName);

        textRightValue = new TextView(context);
        createValueText(textRightValue);
        StringBuilder leftSb = new StringBuilder();
        StringBuilder rightSb = new StringBuilder();
        for (int i = 0; i < mFaceList.size(); i++) {
            String name = mFaceList.get(i);
            if (name.length() > 8) {
                name = name.substring(0, 8) + "...";
            }
            if (i % 2 == 0) {
                leftSb.append(name).append("\n");
            } else {
                rightSb.append(name).append("\n");
            }
        }
        textLeftName.setText(leftSb.toString());
        textRightName.setText(rightSb.toString());
        addView(textLeftName);
        addView(textLeftValue);
        addView(textRightName);
        addView(textRightValue);
    }

    private void createNameText(TextView textView) {
        int textSize = 10;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1.2F;
        textView.setTextSize(textSize);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.START);
        textView.setLayoutParams(params);
    }

    private void createValueText(TextView textView) {
        int textSize = 10;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 0.8F;
        textView.setTextSize(textSize);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.END);
        textView.setLayoutParams(params);
    }

    public void setArSceneEffect(NvsFx mArScene) {
        if (null == mArScene) {
            return;
        }
        NvsARSceneManipulate manipulate = mArScene.getARSceneManipulate();
        if (null == manipulate) {
            return;
        }
        StringBuilder leftValue = new StringBuilder();
        StringBuilder rightValue = new StringBuilder();
        manipulate.setARSceneCallback(new NvsARSceneManipulate.NvsARSceneManipulateCallback() {
            @Override
            public void notifyFaceBoundingRect(List<NvsARSceneManipulate.NvsFaceBoundingRectInfo> list) {

            }

            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void notifyFaceFeatureInfos(List<NvsARSceneManipulate.NvsFaceFeatureInfo> list) {
                textLeftValue.post(() -> {
                    if (list.isEmpty()) {
                        return;
                    }
                    NvsARSceneManipulate.NvsFaceFeatureInfo info = list.get(0);
                    if (null == info) {
                        return;
                    }
                    List<Float> avatarExpressions = info.getAvatarExpressions();
                    if ((null == avatarExpressions) || avatarExpressions.isEmpty()) {
                        return;
                    }
                    leftValue.delete(0, leftValue.length());
                    rightValue.delete(0, rightValue.length());
                    for (int i = 0; i < avatarExpressions.size(); i++) {
                        float value = avatarExpressions.get(i);
                        if (i % 2 == 0) {
                            leftValue.append(String.format("%.3f", value)).append("\n");
                        } else {
                            rightValue.append(String.format("%.3f", value)).append("\n");
                        }
                    }
                    textLeftValue.setText(leftValue.toString());
                    textRightValue.setText(rightValue.toString());
                });
            }

            @Override
            public void notifyCustomAvatarRealtimeResourcesPreloaded(boolean b) {

            }

            @Override
            public void notifyDetectionTimeCost(float v) {

            }

            @Override
            public void notifyTotalTimeCost(float v) {

            }

            @Override
            public void notifyHandFeatureInfos(List<NvsARSceneManipulate.NvsHandFeatureInfo> list) {

            }
        });

    }

    private final List<String> mFaceList = Arrays.asList("右眼闭合",
            "右眼下看",
            "右眼向内看(向左看)",
            "右眼向外看(向右看)",
            "右眼向上看",
            "右眼眯眼",
            "右眼圆睁",
            "左眼闭合",
            "左眼下看",
            "左眼向内看(向右看)",
            "左眼向外看(向左看)",
            "左眼上看",
            "左眼眯眼",
            "左眼圆睁",
            "下颚前突(嘴闭合)",
            "下颚右移(嘴闭合)",
            "下颚左移(嘴闭合)",
            "下颚向下张开(嘴自然张开)",
            "下颚下降",
            "嘴形自然闭合(下颚向下张开)",
            "嘟嘴，嘴唇往前突",
            "撅嘴，嘴唇往外翘",
            "嘴巴鼓气",
            "上下嘴唇右移",
            "上下嘴唇左移",
            "右嘴角向上扬",
            "左嘴角向上扬",
            "右嘴角向下撇",
            "左嘴角向下撇",
            "右嘴角向后撇",
            "左嘴角向后撇",
            "右嘴角水平向外移(右移)",
            "左嘴角水平向外移(左移)",
            "嘴角微微收拢",
            "下嘴唇内卷",
            "上嘴唇内卷",
            "下嘴唇外翻",
            "上嘴唇外翻",
            "下嘴唇右上翘",
            "下嘴唇左上翘",
            "下嘴唇右下垂",
            "下嘴唇左下垂",
            "上嘴唇右上翘",
            "上嘴唇左上翘",
            "微张嘴",
            "右眉毛外垂",
            "左眉毛外垂",
            "双眉向上内挑",
            "双眉向下内垂",
            "右眉外挑",
            "左眉外挑",
            "双面颊前突",
            "右面颊上提",
            "左面颊上提",
            "右鼻子上提",
            "左鼻子上提",
            "双鼻张开",
            "舌头伸出");
}

package com.meishe.sdkdemo.edit.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meishe.sdkdemo.R;

public class InputDialog extends Dialog implements View.OnClickListener {

    private EditText m_userInput;
    private Button m_cancel;
    private Button m_ok;
    private Context mContext;
    private OnCloseListener m_listener;
    private String m_content;
    /**
     * 是否支持emoji
     * emoji support or not
     */
    private boolean noEmoji = false;
    private int cursorPos;
    private String inputAfterText;
    private TextWatcher textWatcher;

    public InputDialog(Context context, int themeResId, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.m_listener = listener;
    }

    public void setNoEmoji(boolean noEmoji) {
        this.noEmoji = noEmoji;
    }

    public void setOkButtonEnable(boolean enable) {
        m_ok.setEnabled(enable);

        if (enable) {
            m_ok.setAlpha(1f);
        } else {
            m_ok.setAlpha(0.3f);
        }
    }

    public String getUserInputText() {
        return m_userInput.getText().toString();
    }

    public void setUserInputText(String str) {
        m_content = str;
        if (m_userInput != null) {
            m_userInput.removeTextChangedListener(textWatcher);
            m_userInput.setText(str);
            m_userInput.setSelection(str.length());
            m_userInput.addTextChangedListener(textWatcher);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_dialog);
        setCanceledOnTouchOutside(false);
        initView();
        if (m_content != null) {
            m_userInput.setText(m_content);
            m_userInput.setSelection(m_content.length());
        }

        m_userInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_userInput.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) m_userInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(m_userInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    private void initView() {

        m_userInput = (EditText) findViewById(R.id.user_input);
        m_ok = (Button) findViewById(R.id.ok);
        m_cancel = (Button) findViewById(R.id.cancel);

        m_ok.setOnClickListener(this);
        m_cancel.setOnClickListener(this);

        setOkButtonEnable(false);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (noEmoji) {
                    cursorPos = m_userInput.getSelectionEnd();
                    inputAfterText = charSequence.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (noEmoji) {
                    if (count > 0) {
                        int end = cursorPos + count;
                        if (cursorPos >= 0 && end <= charSequence.length()) {
                            CharSequence input = charSequence.subSequence(cursorPos, end);
                            if (containsEmoji(input.toString())) {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_support_emoji), Toast.LENGTH_SHORT).show();
                                //是表情符号就将文本还原为输入表情符号之前的内容
                                //The emoji restores the text to what it was before the emoji was entered
                                m_userInput.removeTextChangedListener(textWatcher);
                                m_userInput.setText(inputAfterText);
                                CharSequence text = m_userInput.getText();
                                if (text instanceof Spannable) {
                                    Spannable spanText = (Spannable) text;
                                    Selection.setSelection(spanText, text.length());
                                }
                                m_userInput.addTextChangedListener(textWatcher);
                            }
                        }
                    }
                }
                if (charSequence.toString().isEmpty()) {
                    setOkButtonEnable(false);
                } else {
                    setOkButtonEnable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        m_userInput.addTextChangedListener(textWatcher);
    }

    /**
     * 检测是否有emoji表情
     * Detect if there are emojis
     *
     * @param inputStr
     * @return
     */
    private boolean containsEmoji(String inputStr) {
        int len = inputStr.length();
        for (int i = 0; i < len; i++) {
            char codePoint = inputStr.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情 If it doesn't match, the character is an Emoji
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     * Determine if it is an Emoji
     *
     * @param codePoint             比较的单个字符 A single character for comparison
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            if (m_listener != null) {
                m_listener.onClick(this, false);
            }
        } else if (id == R.id.ok) {
            if (m_listener != null) {
                m_listener.onClick(this, true);
            }
        }

        InputMethodManager inputManager = (InputMethodManager) m_userInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(m_userInput.getWindowToken(), 0);
        this.dismiss();
    }


    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean ok);
    }
}

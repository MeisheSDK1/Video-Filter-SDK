package com.meishe.makeup.net;

import java.io.Serializable;

public class Translation implements Serializable {
    private String originalText;
    private String targetLanguage;
    private String targetText;

    public String getOriginalText() {
        return originalText;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }
}

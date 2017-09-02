package com.kingsun.evalvoice.entity;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class LineResult implements Serializable {
    private String sample;//输入的标准文本
    private String usertext;//用户实际朗读的文本（语音识别结果）
    private double begin;//开始时间，单位为秒
    private double end;//结束时间，单位为秒
    private double score;//分值
    private List<WordResult> words;//每个词的评测结果

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getUsertext() {
        return usertext;
    }

    public void setUsertext(String usertext) {
        this.usertext = usertext;
    }

    public double getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        try {
            this.begin = Double.parseDouble(begin);
        } catch (Exception e) {
            this.begin = 0;
        }
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(String end) {
        try {
            this.end = Double.parseDouble(end);
        } catch (Exception e) {
            this.end = 0;
        }
    }

    public double getScore() {
        return score;
    }

    public void setScore(String score) {
        try {
            this.score = Double.parseDouble(score);
        } catch (Exception e) {
            this.score = 0;
        }
    }

    public List<WordResult> getWords() {
        return words;
    }

    public void setWords(List<WordResult> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "LineResult [sample=" + sample + ", userText=" + usertext
                + ", begin=" + begin + ", end=" + end + ", score=" + score
                + ", words=" + words + "]";
    }

}

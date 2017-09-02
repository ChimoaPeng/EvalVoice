package com.kingsun.evalvoice.entity;

import java.io.Serializable;

public class WordResult implements Serializable {
    private int ID;//自增ID
    private int dialogueID;//所属对白顺序号
    private String dialogueText;//所属文本
    private int videoID;//所属视频ID
    private int bookID;//所属课程ID
    private String userID;//所属用户ID
    private int Sort;//单词在对白内顺序号
    private String text;//词的字符串，使用”sil”表示语音中的静音段
    private int type;//类型，共有6种类型，分别是:0 多词，1 漏词，2 正常词， 3错误词，4 静音，5 重复词
    private double begin;//开始时间，单位为秒
    private double end;//结束时间，单位为秒
    private double volume;//音量
    private double score;//分值

    public WordResult() {
        super();
    }

    public int getDialogueID() {
        return dialogueID;
    }

    public void setDialogueID(int dialogueID) {
        this.dialogueID = dialogueID;
    }

    public String getDialogueText() {
        return dialogueText;
    }

    public void setDialogueText(String dialogueText) {
        this.dialogueText = dialogueText;
    }

    public int getVideoID() {
        return videoID;
    }

    public void setVideoID(int videoID) {
        this.videoID = videoID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBegin(double begin) {
        this.begin = begin;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(String type) {
        try {
            this.type = Integer.parseInt(type);
        } catch (Exception e) {
            this.type = 3;
        }
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

    public double getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        try {
            this.volume = Double.parseDouble(volume);
        } catch (Exception e) {
            this.volume = 0;
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

    @Override
    public String toString() {
        return "WordResult [ID=" + ID + ", dialogueID=" + dialogueID
                + ", dialogueText=" + dialogueText + ", videoID=" + videoID
                + ", bookID=" + bookID + ", userID=" + userID + ", Sort="
                + Sort + ", text=" + text + ", type=" + type + ", begin="
                + begin + ", end=" + end + ", volume=" + volume + ", score="
                + score + "]";
    }

}

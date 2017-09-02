package com.kingsun.evalvoice.entity;

import java.util.List;

public class EvaluateResult {
    private String version;//结果格式版本及版本号
    private List<LineResult> lines;//每行输入文本的评测结果
    private String reordUrl;//录音地址

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<LineResult> getLines() {
        return lines;
    }

    public void setLines(List<LineResult> lines) {
        this.lines = lines;
    }

    public String getReordUrl() {
        return reordUrl;
    }

    public void setReordUrl(String reordUrl) {
        this.reordUrl = reordUrl;
    }

    @Override
    public String toString() {
        return "EvaluateResult [version=" + version + ", lines=" + lines + "]";
    }


}

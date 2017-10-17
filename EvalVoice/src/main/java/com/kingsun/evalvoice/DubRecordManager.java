package com.kingsun.evalvoice;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kingsun.evalvoice.entity.EvaluateResult;
import com.kingsun.evalvoice.entity.LineResult;
import com.kingsun.evalvoice.entity.WordResult;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK.EndReason;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK.ICallback;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK.OfflineSDKError;
import com.unisound.edu.oraleval.sdk.sep15.SDKError;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 录音评测
 *
 * @author Chimoa Peng
 */
public class DubRecordManager implements ICallback {
    private final static String TAG = "DubRecordManager";
    private Context context;
    private VoiceEvaluate evaluate;
    private DubRecordListener recordListener;
    private Object params;

    private String dialogueText;
    private int CurrentVolume = 0;

    private String recordPath = "";
    private FileOutputStream audioFileOut;
    private ArrayList<Integer> volumeList = new ArrayList<Integer>();

    public DubRecordManager(Context context, DubRecordListener recordListener) {
        evaluate = new VoiceEvaluate(context.getApplicationContext());
        evaluate.setCallback(this);
        this.context = context;
        this.recordListener = recordListener;

    }

    /**
     * 0.6-1.9,1.9最容易
     */
    public void setScoreAdjuest(float value) {
        evaluate.set_scoreAdjuest(value);
    }

    public synchronized void recording(final String dialogueText, Object params, String recordPath) {
        recording(dialogueText, params, recordPath, false);
    }

    public synchronized void recording(final String dialogueText, Object params, String recordPath,
                                       boolean isSilentEnd) {
        if (!(dialogueText != null && !dialogueText.equals(""))) {
            onError(evaluate.getIOralEvalSDK(), new SDKError(null, -99, new Exception("评测文本不能为空")), null);
            return;
        }
        this.dialogueText = dialogueText;
        this.recordPath = recordPath;
        this.params = params;
        // 设置评测内容
        evaluate.evaluate(dialogueText, isSilentEnd);
        volumeList.clear();
    }


    public synchronized void stop() {
        if (dialogueText != null && !dialogueText.equals("")) {
            try {
                evaluate.stopIOralEvalSDK();// 结束评测
            } catch (Exception e) {
                e.printStackTrace();
                onError(evaluate.getIOralEvalSDK(), new SDKError(), null);
            }
        }
    }

    @Override
    public void onAudioData(IOralEvalSDK iOralEvalSDK, byte[] bytes, int offset, int len) {
        try {
            File sound = new File(recordPath);
            if (audioFileOut == null) {
                audioFileOut = new FileOutputStream(sound);
            }
            audioFileOut.write(bytes, offset, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(IOralEvalSDK arg0, SDKError err, OfflineSDKError arg2) {
        closeAudioFileOut();
        onlineSDKError(context, err);
        recordListener.onError(err, params);
    }

    @Override
    public void onStart(IOralEvalSDK arg0, int arg1) {

    }

    @Override
    public void onStop(IOralEvalSDK arg0, String s, boolean offline, String url, EndReason stopType) {
        closeAudioFileOut();
        if (s != null) {
            int num = 0, end = 0;
            if (volumeList != null && volumeList.size() > 0) {
                for (int i = 0; i < volumeList.size(); i++) {
                    num += volumeList.get(i);
                }
                end = num / volumeList.size();
            }
            EvaluateResult evaluateResult = parseJsonResult(s);
            if (evaluateResult.getLines() == null || evaluateResult.getLines().size() <= 0) {
                onError(evaluate.getIOralEvalSDK(), new SDKError(null, -99, new Exception("评测结果内容为空")), null);
                return;
            }
            LineResult result = evaluateResult.getLines().get(0);
            recordListener.onSuccess(result, params, end, url, stopType);
        }
    }

    @Override
    public void onVolume(IOralEvalSDK arg0, int arg1) {
        if (arg1 > CurrentVolume) {
            CurrentVolume = arg1;
        }
        volumeList.add(arg1);
    }

    private void closeAudioFileOut() {
        if (audioFileOut != null) {
            try {
                audioFileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioFileOut = null;
        }
    }


    @Override
    public void onAsyncResult(IOralEvalSDK arg0, String arg1) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onOpusData(IOralEvalSDK arg0, byte[] arg1, int arg2, int arg3) {

    }

    public static EvaluateResult parseJsonResult(String result) {
        EvaluateResult evaluateResult = new EvaluateResult();
        try {
            JSONObject jsonObject = new JSONObject(result);
            evaluateResult.setVersion(jsonObject.getString("version"));
            Gson gson = new Gson();
            List<LineResult> lines = new ArrayList<LineResult>();
            lines = gson.fromJson(jsonObject.getString("lines"),
                    new TypeToken<List<LineResult>>() {
                    }.getType());
            evaluateResult.setLines(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evaluateResult;

    }

    public static void onlineSDKError(final Context context, final SDKError error) {
        if (error != null && error.errno != -99) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (SDKError.Category.valueOf(error.category.toString())) {
                            case Device:
                                Toast.makeText(context, "录音设备错误,确认开启录音权限", Toast.LENGTH_SHORT).show();
                                break;
                            case Network:
                                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                                break;
                            case Server:
                                Toast.makeText(context, "服务器错误。 遇到此错误联系云知声", Toast.LENGTH_SHORT).show();
                                break;
                            case Unknown_word:
                                Toast.makeText(context, "非法评测内容", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }
    }

    /**
     * @Title: setViewTextColor @Description:
     * (设置跟读评测结果单词对应颜色显示) @param @param textView @param @param wordResults
     * 设定文件 @return void 返回类型 @throws
     * <p>
     * 4个级别的不同的颜色值
     * // 正黑，perfect
     * int color1 = textView.getContext().getResources().getColor(R.color.black);
     * // excellent
     * int color2 = textView.getContext().getResources().getColor(R.color.text_color_wonderful);
     * // great
     * int color3 = textView.getContext().getResources().getColor(R.color.text_color_great);
     * // good
     * int color4 = textView.getContext().getResources().getColor(R.color.text_color_good);
     */
    public static void setViewTextColor(TextView textView, List<WordResult> wordResults, int color1, int color2, int color3, int color4) {
        if (wordResults == null) {
            return;
        }
        String text = textView.getText().toString().trim();
        SpannableString sp = new SpannableString(textView.getText().toString().trim());
        for (int i = 0; i < wordResults.size(); i++) {
            if (!wordResults.get(i).getText().equals("sil")) {
                // 不等于分隔符
                int start = text.indexOf(wordResults.get(i).getText());
                if (start == -1)
                    continue;
                int end = start + wordResults.get(i).getText().length();
                String replaceMent = createLengthString(end - start);
                text = text.replaceFirst(wordResults.get(i).getText(), replaceMent);

                if (wordResults.get(i).getScore() >= 9d) {
                    sp.setSpan(new ForegroundColorSpan(color1), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                if (wordResults.get(i).getScore() >= 8d) {
                    sp.setSpan(new ForegroundColorSpan(color2), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                if (wordResults.get(i).getScore() >= 6d) {
                    sp.setSpan(new ForegroundColorSpan(color3), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    sp.setSpan(new ForegroundColorSpan(color4), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
        }
        textView.setText(sp);
    }

    public static String createLengthString(int length) {
        String str = "";
        for (int i = 0; i < length; i++) {
            str += "#";
        }
        return str;
    }
}

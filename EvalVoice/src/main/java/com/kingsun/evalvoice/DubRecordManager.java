package com.kingsun.evalvoice;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kingsun.evalvoice.entity.EvaluateResult;
import com.kingsun.evalvoice.entity.LineResult;
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

    public static void onlineSDKError(Context context, SDKError error) {
        if (error != null && error.errno != -99) {
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
    }
}

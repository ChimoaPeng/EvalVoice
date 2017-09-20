package com.kingsun.evalvoice;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK.OfflineSDKError;
import com.unisound.edu.oraleval.sdk.sep15.OralEvalSDKFactory;

public class VoiceEvaluate {
    private static final String TAG = "VoiceEvaluate";
    static final boolean USE_OFFLINE_SDK_IF_FAIL_TO_SERVER = false;
    private Context context;
    private IOralEvalSDK _oe;
    private IOralEvalSDK.ICallback callback = null;

    private float _scoreAdjuest = 1.8f;
    private String serviceType = "A";


    public VoiceEvaluate(Context context) {
        this.context = context;
        initialize();
    }

    public void set_scoreAdjuest(float _scoreAdjuest) {
        this._scoreAdjuest = _scoreAdjuest;
    }

    private void initialize() {
        if (USE_OFFLINE_SDK_IF_FAIL_TO_SERVER) {
            Log.i(TAG, "start init offline sdk");
            OfflineSDKError err = OralEvalSDKFactory.initOfflineSDK(context.getApplicationContext(), null);
            Log.i(TAG, "end init offline sdk");
            if (err != OfflineSDKError.NOERROR) {
                Log.i(TAG, "init sdk failed:" + err);
            }
        }
    }

    private OralEvalSDKFactory.StartConfig getCfg(String txt, boolean isSilentEnd) {
        OralEvalSDKFactory.StartConfig cfg = null;
        cfg = new OralEvalSDKFactory.StartConfig(txt);
        if (isSilentEnd) {
            cfg.setVadEnable(true);
            cfg.setVadAfterMs(15000);
            cfg.setVadBeforeMs(15000);
        } else {
            cfg.setVadEnable(false);
            cfg.setVadAfterMs(15000);
            cfg.setVadBeforeMs(15000);
        }
        if (USE_OFFLINE_SDK_IF_FAIL_TO_SERVER) {
            cfg.set_useOfflineWhenFailedToConnectToServer(true);
        }
        cfg.setMp3Audio(true);//use mp3 in onAudioData() callback, or pcm output
        cfg.setScoreAdjuest(_scoreAdjuest);
        cfg.setServiceType(serviceType);
        return cfg;
    }

    public void evaluate(String str, boolean isSilentEnd) {
        if (_oe == null) {
            if (str == null) {
                Toast.makeText(context, "评测文本为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            OralEvalSDKFactory.StartConfig cfg = getCfg(str, isSilentEnd);
            if (cfg == null) {
                return;
            }
            _oe = OralEvalSDKFactory.start(this.context, cfg, callback);
        } else {
            stopIOralEvalSDK();
        }
    }

    public void stopIOralEvalSDK() {
        if (_oe != null) {
            _oe.stop();
            _oe = null;
        }
    }

    public IOralEvalSDK getIOralEvalSDK() {
        return _oe;
    }

    public void setCallback(IOralEvalSDK.ICallback callback) {
        this.callback = callback;
    }


}

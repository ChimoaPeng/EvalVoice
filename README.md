### How to use
---
dependencies {
    compile 'com.github.ChimoaPeng:EvalVoice:1.0.2'
}

<pre>
First
private DubRecordManager recordManager;
private void createAudioRecord() {
    recordManager = new DubRecordManager(this, new DubRecordListener() {
        @Override
        public void onError(com.unisound.edu.oraleval.sdk.sep15.SDKError sdkError, Object o) {
            //TODO received error result in non-ui-thread
        }

        @Override
        public void onSuccess(LineResult lineResult, Object o, int i, String s, com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK.EndReason endReason) {
            Log.e("recordManager", "onSuccess " + lineResult);
             //TODO received success result in non-ui-thread
        }
    });
}

Second
File cacheFile = ...;
recordManager.recording("I am testing english", new Object(), cacheFile.getAbsolutePath());

Third
recordManager.stop();



</pre>


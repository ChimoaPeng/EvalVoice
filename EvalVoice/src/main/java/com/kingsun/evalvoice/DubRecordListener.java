package com.kingsun.evalvoice;

import com.kingsun.evalvoice.entity.LineResult;
import com.unisound.edu.oraleval.sdk.sep15.IOralEvalSDK;
import com.unisound.edu.oraleval.sdk.sep15.SDKError;

/**
 * Created by Chimoa Peng on 2017/9/2.
 */

public interface DubRecordListener {

    /**
     * 评测错误
     *
     * @param err    评测错误描述
     * @param params 评测请求参数，评测时传入，结果时原样返回
     */
    void onError(SDKError err, Object params);


    /**
     * 评测成功
     *  @param evaluateResults 评测结果
     * @param params          评测请求参数，评测时传入，结果时原样返回
     * @param end
     * @param url
     * @param stopType
     */
    void onSuccess(LineResult evaluateResults, Object params, int end, String url, IOralEvalSDK.EndReason stopType);
}

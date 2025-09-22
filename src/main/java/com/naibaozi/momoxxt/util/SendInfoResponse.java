/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:30:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:32
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/SendInfoResponse.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:30:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:32
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/SendInfoResponse.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.util;

import com.alibaba.fastjson.JSON;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class SendInfoResponse {


    /**
     * 发送错误响应
     */
    public void Error(PrintWriter out, int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        out.write(JSON.toJSONString(error));
    }



    // 新增成功响应工具方法
    public void Success(PrintWriter out, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", message);
        out.write(JSON.toJSONString(result));
    }
}

/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-20 17:02:59
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 17:03:00
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/ResultUtil.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.util;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果工具类
 */
public class ResultUtil {

    /**
     * 成功响应
     */
    public static String success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", data);
        return JSON.toJSONString(result);
    }

    /**
     * 错误响应
     */
    public static String error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        return JSON.toJSONString(result);
    }
}

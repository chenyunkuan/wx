package com.jfinal.weixin.share;

import com.jfinal.kit.HashKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.JsTicketApi.JsApiType;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.util.WeixinUtil;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsTicketController extends ApiController {

    private final Logger logger = Logger.getLogger(getClass());

    /**
     * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
     * ApiConfig 属性值
     */
    public ApiConfig getApiConfig() {
        return WeixinUtil.getApiConfig();
    }

    public void index() {
        JsTicket jsApiTicket = JsTicketApi.getTicket(JsApiType.jsapi);
        String jsapi_ticket = jsApiTicket.getTicket();
        String nonce_str = create_nonce_str();
        // 注意 URL 一定要动态获取，不能 hardcode.
        String url = "http://" + getRequest().getServerName() // 服务器地址
                // + ":"
                // + getRequest().getServerPort() //端口号
                + getRequest().getContextPath() // 项目名称
                + getRequest().getServletPath();// 请求页面或其他地址
        String qs = getRequest().getQueryString(); // 参数

        if (qs != null) {
            url = getRequest().getParameter("url");
        }
        try {
            url = URLDecoder.decode(url,"utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        logger.info("url>>>>" + url);
        String timestamp = create_timestamp();
        // 这里参数的顺序要按照 key 值 ASCII 码升序排序
        //注意这里参数名必须全部小写，且必须有序
        String str = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;

        String signature = HashKit.sha1(str);

        logger.info("appId " + ApiConfigKit.getApiConfig().getAppId()
                + "  nonceStr " + nonce_str + " timestamp " + timestamp);
        logger.info("url " + url + " signature " + signature);
        logger.info("nonceStr " + nonce_str + " timestamp " + timestamp);
        logger.info(" jsapi_ticket " + jsapi_ticket);
        logger.info("nonce_str  " + nonce_str);

        Map<String, String> map = new HashMap<String, String>();

        map.put("appId", ApiConfigKit.getApiConfig().getAppId());
        map.put("nonceStr", nonce_str);
        map.put("timestamp", timestamp);
        map.put("url", url);
        map.put("signature", signature);
        map.put("jsapi_ticket", jsapi_ticket);

        renderJson(map);

    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        // logger.info(createNonceStr().length());
        System.out.println(System.currentTimeMillis());
        System.out.println(
                HashKit.sha1("jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=http://mp.weixin.qq.com?params=value"));
    }
}

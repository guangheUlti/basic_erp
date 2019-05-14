package io.guangsoft.erp.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lucene")
public class LuceneController {

    @RequestMapping(value = "search")
    public String search(String keyWords) {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("data", "");
        return result.toString();
    }
}
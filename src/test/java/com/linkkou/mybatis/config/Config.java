package com.linkkou.mybatis.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class Config {
    @Bean
    public Map createMap() {
        Map map = new HashMap();
        map.put("datasource.url", "jdbc:mysql://localhost:3306/cms?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useOldAliasMetadataBehavior=true&autoReconnect=true&failOverReadOnly=false&serverTimezone=Asia/Shanghai");
        return map;
    }
}
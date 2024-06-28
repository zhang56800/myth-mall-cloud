package com.myth.mall.cloud.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.dto.ResultGenerator;
import com.myth.mall.cloud.pojo.AdminUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * {@link  }.
 *
 * @author Jarvis
 * @version v1.0
 * @date 2024/6/27-22:19
 */

@Component
public class ValidTokenGlobalFilter implements GlobalFilter, Ordered{

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if ("/users/admin/login".equals(path)){
            return chain.filter(exchange);
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();

        if (headers.isEmpty()){
            //无token发送至服务端
            return wrapErrorResponse(exchange,chain);
        }

        String token = headers.getFirst("token");
        if (!StringUtils.hasText(token)) {
            // 返回错误提示
            return wrapErrorResponse(exchange, chain);
        }
        ValueOperations<String,AdminUserToken> getToken = redisTemplate.opsForValue();
        AdminUserToken tokenObject = getToken.get(token);
        if (tokenObject == null){
            //token无效
            return wrapErrorResponse(exchange,chain);
        }

        //放行*****************************注意当前return位置
        // return null 如果return 为null就会报错 The Mono returned by the supplier is null
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    Mono<Void> wrapErrorResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        Result result = ResultGenerator.genErrorResult(419, "无权限访问");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.valueToTree(result);
        byte[] bytes = resultNode.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().writeWith(Flux.just(dataBuffer));
    }
}

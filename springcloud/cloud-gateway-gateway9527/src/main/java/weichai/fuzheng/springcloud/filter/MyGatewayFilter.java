package weichai.fuzheng.springcloud.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MyGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("********进入全局过滤器**********");
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        if (uname == null){
            System.out.println("非法用户");
            //退出
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        //合法用户放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        //执行filter的顺序，数值越少执行优先
        return 1;
    }
}

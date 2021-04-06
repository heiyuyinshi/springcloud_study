[TOC]


# 1. springcloud基础

## 1.1 概述

微服务架构

独立进程、轻量级、独立部署

springcloud一系列技术的组合

### 版本选型：

springboot2.x和springcloud H版

springboot版本选择：

一定选2以上

boot和cloud版本依赖关系

Hoxton——2.2.x

更详细版本查看方式

https://start.spring.io/actuator/info

课程版本选择

springcloud：Hoxton.SR1/SR10

springboot：2.2.2.RELEASE/2.3.8.RELEASE

cloud alibaba：2.1.0.RELEASE

java：java8

maven：3.5及以上

MySQL：5.7及以上

### 组件停更和升级

停更不停用被动修复bug、不再接受合并请求、不再发布新版本

对于这些，停课不停学

明细条目

停更——》替代

* 服务注册中心

eureka——》zookeeper、consul、nacos（推荐）

* 服务调用

ribbon（更新较慢）——》loadBalancer

feign——》openFeign

* 服务降级

Hystrix——》resilience4j（国外）、alibaba sentinel（国内）

* 服务网关

zuul——》gateway

* 服务配置

config——》nacos

* 服务总线

bus——》nacos

## 1.2 工程创建

### 总父工程

父工程pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>weichai.fuzheng</groupId>
    <artifactId>springcloud</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

<!--    统一管理jar包版本-->
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>4.12</junit.version>
        <log4j.version>1.2.17</log4j.version>
        <lombok.version>1.16.18</lombok.version>
        <mysql.version>8.0.22</mysql.version>
        <druid.version>1.1.22</druid.version>
        <mybatis.spring.boot.version>2.1.4</mybatis.spring.boot.version>
    </properties>
<!--管理子模块依赖-->
<!--注意springboot、cloud和alibaba cloud-->
    <dependencyManagement>
        <dependencies>
<!--            springboot版本-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.2.2.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
<!--            springcloud版本-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Hoxton.SR1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
<!--            alibaba版本-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2.1.0.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>${mysql.version}</version>
                <scope>runtime</scope>
            </dependency>
            <!-- druid-->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>${druid.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis.spring.boot.version}</version>
            </dependency>
            <!--junit-->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>${junit.version}</version>
            </dependency>
            <!--log4j-->
            <dependency>
                <groupId>log4j</groupId>
                <artifactId>log4j</artifactId>
                <version>${log4j.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork>
                    <addResources>true</addResources>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 支付模块构建

rest微服务构建

客户端消费者order——》微服务提供者payment

基本步骤：

1. 建module
2. 改pom
3. 写yaml

```yaml
#微服务一定指定端口号
server:
  port: 8001

# 服务名称
spring:
  application:
    name: cloud-payment-service
  # 数据库配置
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/study
    username: fuzheng
    password: heiyuyinshi

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: weichai,fuzheng.springcloud.entities #所有entity别名类所在包
```
4. 主启动
5. 业务类

建表

```sql
CREATE TABLE `payment`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`serial` VARCHAR(200) DEFAULT '',
PRIMARY KEY(`id`)
) CHARSET=utf8;
```

实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    private Long id;
    private String serial;
}
```

json封装结果

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;
    
    //通用的返回
    public CommonResult(Integer code, String message){
        this(code, message, null);
    }
}
```

dao接口

```java
@Mapper
public interface PaymentDao {
    public Integer create(Payment payment);

    public Payment getPaymentById(@Param("id") Long id);
}
```

接口对应mapper

```xml
<mapper namespace="weichai.fuzheng.springcloud.dao.PaymentDao">
    <insert id="create" parameterType="Payment" useGeneratedKeys="true" keyProperty="id">
        insert into payment(serial) values(#{serial})
    </insert>

    <resultMap id="BaseResultMap" type="weichai.fuzheng.springcloud.entities.Payment">
        <id column="id" property="id" jdbcType="BIGINT"/>
        <id column="serial" property="serial" jdbcType="VARCHAR"/>
    </resultMap>
    <select id="getPaymentById" parameterType="Long" resultMap="BaseResultMap">
        select * from payment where id=#{id}
    </select>
</mapper>
```

service接口

```java
public interface PaymentService {
    public Integer create(Payment payment);

    public Payment getPaymentById(Long id);
}
```

service接口实现

```java
@Service
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private PaymentDao paymentDao;

    @Override
    public Integer create(Payment payment) {
        return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}
```

controller层

```java
@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @PostMapping(value = "/payment/create")
    public CommonResult creat(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("**************插入结果："+ result);
        if (result > 0){
            return new CommonResult(200,"插入数据成功",result);
        }else {
            return new CommonResult(444,"插入数据失败",null);
        }
    }

    @GetMapping(value = "/payment/find/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment result = paymentService.getPaymentById(id);
        log.info("**************插入结果："+ result);
        if (result != null){
            return new CommonResult(200,"查询数据成功",result);
        }else {
            return new CommonResult(444,"查询数据失败",null);
        }
    }
}
```

6. 测试

postman测试

### 消费者订单模块

步骤基本相同

1. 建module
2. 修改pom
3. 建application.yaml
4. 主启动类
5. 业务类
6. 测试

消费者订单模块，需要调用支付模块的函数

1. 添加实体类entitie
2. 使用RestTemplate

编辑config/ApplicationContextConfig

```java
@Configuration
public class ApplicationContextConfig {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

编辑controller

```java
@RestController
@Slf4j
public class OrderController {

    public static final String PAYMENT_URL = "http://localhost:8001";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
    }
}
```

### 工程重构

entities部分重复

1. 新建cloud-api-common工程

```xml
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.0.3</version>
        </dependency>
    </dependencies>
```

2. 创建entities，并添加对应类

3. maven命令clean、install
4. 在其余模块导入自己的jar包

```xml
        <dependency>
            <groupId>weichai.fuzheng</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

5. 删除原模块中多余的类

# 2. 服务注册中心

## 1.eureka

### 基础知识

服务治理，传统rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，需要服务治理

管理服务之间的依赖关系：实现服务调用、负载均衡、容错等，实现服务发现与注册

eureka包含两个组件：eureka Server、eureka Client

### 单机安装

1. idea生成eurekaServer端服务注册中心，创建模块cloud-eureka-server7001
2. 改pom，添加

```xml
    <dependencies>
<!--        eureka server-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>weichai.fuzheng</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

3. 写yaml

```yaml
server:
  port: 7001

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false # 不向注册中心注册自己
    fetch-registry: false # false标明自己就是注册中心，职责是维护服务实例，不需要检索服务
    service-url:
      default-zone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

4. 主启动

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaMain {
    public static void main(String[] args) {
        SpringApplication.run(EurekaMain.class);
    }
}
```

5. 测试

测试网址：[Eureka](http://localhost:7001/)

### 将服务入驻

修改8001payment

1. pom导入

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

2. 修改yaml

```yaml
# 添加eureka配置
eureka:
  client:
    register-with-eureka: true #将自己注册为eureka服务
    fetch-registry: true # 是否从eurekaServer中抓取已有的注册信息，默认为truue，单节点无所谓，集群必须设置为true，才能使用ribbon负载均衡
    service-url:
      defaultZone: http://localhost:7001/eureka #defaultZone不能写为default-zone 
```

3. 主启动

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaMain {
    public static void main(String[] args) {
        SpringApplication.run(EurekaMain.class);
    }
}
```

服务80入驻

与8001相同

### eureka集群

互相注册，相互守望

eureka-server7001配置

```yaml
server:
  port: 7001
eureka:
  instance:
    hostname: eureka7001.com
  client:
    register-with-eureka: false # 不向注册中心注册自己
    fetch-registry: false # false标明自己就是注册中心，职责是维护服务实例，不需要检索服务
    service-url:
      defaultZone: http://eureka7002.com:7002/eureka # 最后这写7002，7002守望7001
```

eureka-server7002配置

```yaml
server:
  port: 7002

eureka:
  instance:
    hostname: eureka7002.com
  client:
    register-with-eureka: false # 不向注册中心注册自己
    fetch-registry: false # false标明自己就是注册中心，职责是维护服务实例，不需要检索服务
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka # 最后这写7001，7001守望7002
```

### 服务配置到集群

修改配置文件

```yaml
# 添加eureka配置
eureka:
  client:
    register-with-eureka: true #将自己注册为eureka服务
    fetch-registry: true # 是否从eurekaServer中抓取已有的注册信息，默认为truue，单节点无所谓，集群必须设置为true，才能使用ribbon负载均衡
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
```

### 支付服务提供者集群配置

payment模块集群配置

payment8002基本拷贝自8001

1. 配置文件：application name保持相同
2. 服务订阅者，修改地址为服务名

```java
public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";
```

3. ApplicationContextConfig添加负载均衡注解

```java
@Configuration
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

### actuator微服务信息完善

1. 主机名修改

```yaml
eureka:
  instance:
    instance-id: payment8001
```

2. 添加访问信息显示ip

```yaml
eureka:
  instance:
    prefer-ip-address: true
```

### 服务发现

对于注册进eureka里面的微服务，可以通过服务发现获得该服务信息

修改controller，添加内容

```java
@RestController
@Slf4j
public class PaymentController {
    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping("/payment/discovery")
    public Object discovery(){
        //服务清单列表
        List<String> services = discoveryClient.getServices();
        for (String service: services
             ) {
            log.info(service);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort() + "\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
}
```

主启动类

```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class Payment8001 {
    public static void main(String[] args) {
        SpringApplication.run(Payment8001.class);
    }
}
```

### eureka自我保护机制

eureka尝试保护服务注册信息，不再删除微服务信息

某时刻微服务不可用了，eureka不会立刻清理，依旧会对服务信息进行保存

如何禁用自我保护

```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 2000 # 失去心跳最长时间间隔
```

客户端

```yaml
eureka:
  instance:
    instance-id: payment8001
    # eureka客户端向服务端发送心跳的时间间隔，单位为s（默认30s）
    lease-renewal-interval-in-seconds: 1
    # eureka服务端在收到最后一次心跳后等待时间上限，单位为s（默认90s），超时剔除服务
    lease-expiration-duration-in-seconds: 2
```

## 2. zookeeper

把eureka变为zookeeper

1. 新建工程
2. pom

```xml
<!--        springboot整合zookeeper客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
        </dependency>
```

3. yml

添加

```yaml
# 服务名称
spring:
  cloud:
    zookeeper:
      connect-string: 172.17.143.131:2181
```

4. 主启动类

```java
@SpringBootApplication
@EnableDiscoveryClient
public class Payment8004 {
    public static void main(String[] args) {
        SpringApplication.run(Payment8004.class);
    }
}
```

生成的节点属于临时节点，服务关闭，zookeeper就会把节点删除

消费端

从集群中读取服务

```yaml
server:
  port: 84
spring:
  application:
    name: cloud-zookeeper-order
  cloud:
    zookeeper:
      connect-string: 172.17.143.131:2181,172.17.143.132:2181,172.17.143.133:2181
```

调用

```java
@RestController
public class TestController {

    //使用服务名称
    public static final String INVOKE_URL = "http://cloud-payment-service";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(INVOKE_URL + "/payment/get/" + id, CommonResult.class);
    }
}
```

## 3. consul

服务发现和配置管理系统，go语言开发

特性

* 服务发现
* 健康监测
* kv存储
* 多数据中心
* 可视化web界面

## 三个注册中心比较

CAP理论

C：consistency强一致性

A：availability可用性

P：partition tolerance分区容错性



eureka满足AP

consul和zookeeper满足CP



P一定要满足

重要的数据优先满足C，不重要的优先A

AP架构：系统可以暂时返回旧值，保证系统的可用性

# 3. riboon

负载均衡，服务调用

基于Netflix ribbon实现的一套客户端——负载均衡工具

主要功能：提供客户端的软件负载均衡算法和服务调用

现在已经停止维护，未来可能使用loadbalance替代

* 集中式负载均衡：nginx，客户端所有请求交给nginx，由nginx实现转发请求，负载均衡由服务端实现
* 本地式负载均衡：调用微服务接口时，会在注册中心上获取注册信息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术

ribbon提供策略

* 轮询
* 随机
* 根据响应时间加权

新版eureka内部整合了ribbon

### restTemplate的使用

getForObject：返回对象为响应体中数据转化成的对象，基本可以理解为json

getForEntity：返回对象为ResponseEntity对象，包含了响应中的一些重要信息，比如响应头、响应状态码、响应体等

### ribbon核心组件IRule

ribbon自带方式

* RoundRobinRule：轮询
* RandomRule：随机
* RetryRule：先按照RoundRobinRule策略获取服务，如果获取失败指定时间会重试
* WeightedResponseTimeRule：对RoundRobinRule的扩展，响应速度越快的实例选择权重越大，越容易被选择
* BestAvailableRule：先过滤由于多次访问故障而处理断路跳闸状态的服务，然后选择一个并发量最小的服务
* AvailabilityFilteringRule：先过滤故障实例，再选择并发较小的实例
* ZoneAvoidanceRule：默认规则，复合判断server所在区域的性能和server的可用性选择服务

规则替换

注意：自定义配置类不能放在`@ComponentScan`所扫描的当前包和子包下，否则自定义配置类会被所有的ribbon客户端共享，达不到特殊化定制的目的

**则不能放在application所在包**

1. 添加规则类

```java
@Configuration
public class MyselfRule {
    @Bean
    public IRule myRule(){
        return new RandomRule();//定义为随机
    }
}
```

2. 主启动类修改

```java
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "CLOUD-PAYMENT-SERVICE", configuration = MyselfRule.class)
public class OrderMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class);
    }
}
```

RibbonClient中name与controller中服务名称相同

轮询实现原理：

总机器数3台

List = 3个实例

对访问次数取余，根据余数选择list中的实例

# 4. openfeign

服务接口调用

声明式的web服务客户端，让编写web服务客户端变得简单，只需创建接口并在接口上添加注解

## 基本使用

1. 新建工程cloud-consumer-feign-order80
2. pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```

3. yaml文件

```yaml
server:
  port: 80

eureka:
  client:
    register-with-eureka: false
    service-url: 
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
```

4. 主启动类

```java
@SpringBootApplication
@EnableFeignClients
public class OrderFeign {
    public static void main(String[] args) {
        SpringApplication.run(OrderFeign.class);
    }
}
```

5. 编写service接口

```java
@Component
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")//选择调用的微服务
public interface PaymentFeignService {
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id);
}
```

6. 编写controller

```java
@RestController
@Slf4j
public class OrderFeignController {
    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }
}
```

feign自带负载均衡功能

## 超时控制

默认等待1秒，没等到结果就会报错

有时需要设置等待时间

```yaml
ribbon:
  # 建立连接后从服务器读取可用资源所用时间
  ReadTimeout: 5000
  # 建立连接所用时间
  ConnectTimeout: 5000
```

## 日志打印功能

日志级别

* NONE
* BASIC
* HEADERS
* FULL

```java
@Configuration
public class FeignConfig {
    
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
```

yaml配置

```yaml
logging:
  level:
    # 以debug形式，打印PaymentFeignService日志
    weichai.fuzheng.springcloud.service.PaymentFeignService: debug
```

# 5. Hystrix

断路器

分布式系统问题：链路太长，中间出问题可能性变大

hystrix延迟容错的开源库，保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性

**Hystrix已经停止更新进入维护**

国内大多使用sentinel替代

## 重要概念

* 服务降级fallback

假设对方系统不可用了，需要有个兜底的挽救方案，返回一个预先约定好的，可处理的解决方案

发生降级的情况：程序异常、超时、服务熔断触发服务降级、线程池/信号量打满

* 服务熔断break

达到最大访问量后，服务器拒绝访问，然后调用服务降级并返回友好提示

* 服务限流flowlimit

秒杀等高并发操作，严禁一窝蜂进来。一秒N个，有序进行

## 工程构建

1. 建立工程cloud-provider-hystrix-payment8001
2. pom
3. yaml
4. 编写业务

service层

```java
@Service
public class PaymentService {
    //正常访问OK的方法
    public String paymentInfo_OK(Integer id){
        return "线程池：" + Thread.currentThread().getName() + "payment_OK, id = " + id;
    }
    //模拟出错的方法，自带3s延迟
    public String paymentInfo_Err(Integer id){
        int timeunit = 3;
        try{
            TimeUnit.SECONDS.sleep(timeunit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() + "payment_Timeout, id = " + id;
    }
}
```

JMeter高并发测试

[Apache JMeter - User's Manual: Getting Started](https://jmeter.apache.org/usermanual/get-started.html)

并发量大出现问题：

* 超时导致服务器变慢
* 出错(宕机或程序运行错误)

解决

* 超时：不再等待，服务降级
* 出错：要有兜底，服务降级
* 由于本身而出故障的，自身进行降级

## 服务降级

### 支付侧fallback（服务侧）

降级配置@HystrixCommand

设置自身调用超时时间的峰值，峰值内可以正常运行

超过了需要由兜底的方法进行处理，进行服务降级fallback

1. pom引入

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
```

2. 主启动类

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrix {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrix.class);
    }
}
```

3. service层对应方法添加@HystrixCommand

```java
    //模拟出错的方法
    @HystrixCommand(fallbackMethod = "errHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3050")
    })//设置自身超时时间、兜底的方法
    public String paymentInfo_Err(Integer id){
        int timeunit = 4;
        try{
            TimeUnit.SECONDS.sleep(timeunit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() + "payment_Timeout, id = " + id;
    }
    //错误处理的方法，名称要相同，参数要相同
    public String errHandler(Integer id){
        return "线程池：" + Thread.currentThread().getName() + "超时了，不好意思";
    }
```

当前服务不可用了（超时或出错），都会调用错误处理

### 消费端fallback

1. 主启动类

```java
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class OrderMainHystrix {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainHystrix.class);
    }
}
```

2. 业务类controller层

```java
@RestController
@Slf4j
public class OrderController {
    @Resource
    private PaymentService paymentService;

    @GetMapping("/order/ok/{id}")
    public String Payment_OK(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_OK(id);
    }

    @GetMapping("/order/err/{id}")
    @HystrixCommand(fallbackMethod = "errHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500")
    })//设置自身超时时间、兜底的方法
    public String Payment_Err(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_Err(id);
    }

    //错误处理，参数要一致
    public String errHandler(@PathVariable("id") Integer id){
        return "线程池：" + Thread.currentThread().getName() + " 80服务超时了，不好意思";
    }
}
```

### 全局服务降级

问题：

1. 代码膨胀
2. 业务逻辑代码和故障处理代码混杂

解决

1. 设置全局通用的解决方案

```java
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "global_fallback")//设置全局错误处理方法名
public class OrderController {
    @Resource
    private PaymentService paymentService;

    @GetMapping("/order/ok/{id}")
    public String Payment_OK(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_OK(id);
    }

    @GetMapping("/order/err/{id}")
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String Payment_Err(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_Err(id);
    }

    //全局fallback
    public String global_fallback(){
        return "全局错误处理";
    }
}
```

2. 为feign客户端定义的接口添加一个服务降级处理的实现类

新建类实现feign的service接口

```java
@Component
public class PaymentFallbackService implements PaymentService{
    @Override
    public String paymentInfo_OK(Integer id) {
        return "fallback ok";
    }

    @Override
    public String paymentInfo_Err(Integer id) {
        return "fallback timeout";
    }
}
```

对应接口添加fallback

```java
@Service
@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT", fallback = PaymentFallbackService.class)
public interface PaymentService {
    @GetMapping("/payment/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/payment/err/{id}")
    public String paymentInfo_Err(@PathVariable("id") Integer id);
}
```

## 服务熔断

达到最大访问量，直接拒绝访问

当检测该节点微服务调用响应正常后，**恢复调用链路**

hutool：[入门和安装 (hutool.cn)](https://www.hutool.cn/docs/#/)

相关参数查看：[Home · Netflix/Hystrix Wiki · GitHub](https://github.com/Netflix/Hystrix/wiki)

+HystrixCommandProperties

```java
    //----------服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),//开启熔断
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),//请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//时间窗口
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")//失败率
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        if (id < 0){
            throw new RuntimeException("id不能为负");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + " 调用成功，流水号:"+ serialNumber;
    }
    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id){
        return "id 不能为负数，请重试";
    }
```

服务的3中状态open、half-open、close

熔断过程：open——》close——》half-open——》open

服务限流：交给sentinel

工作流程图

![img](https://github.com/Netflix/Hystrix/wiki/images/hystrix-command-flow-chart-640.png)

## hystrix图形化

dashBoard

服务监控

1. 新建模块
2. pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
```

3. yaml

```yaml
server:
  port: 9001
```

4. 主启动类

```java
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardMain {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardMain.class);
    }
}
```

测试

访问网址：[Hystrix Dashboard](http://localhost:9001/hystrix)

8001端口主启动类修改

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrix {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrix.class);
    }

    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
```

输入监控网址：http://localhost:8001/hystrix.stream

# 6. 服务网关

限流、分配

gateway新一代网关

spring自研

## 概念简介

提供一种简单有效的方式对api进行路由，以提供一些强大的过滤器功能，例如：熔断、限流、重试等

基于webFlux框架实现的，webFlux框架底层使用了Reactor模式通讯框架netty

请求进来顺序：

外部请求——》负载均衡——》网关——》微服务模块

gateway基于异步非阻塞模型

## 核心概念

* route

路由，构建网关的基本模块，由id、目标URI、一系列的断言和过滤器组成

* predicate

断言，开发人员可以匹配http请求中所有内容（请求头或请求参数），如果请求与断言相匹配则进行路由

* filter

过滤，可以在请求被路由前或者之后对请求进行修改

核心逻辑：路由转发+执行过滤器链

## 工程搭建

1. 新建模块cloud-gateway-gateway9527
2. pom（不需要web的依赖）

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
```

3. yaml

```yaml
server:
  port: 9527

spring:
  application:
    name: cloud-gateway

eureka:
  instance:
    hostname: cloud-gateway-service
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url: 
      defaultZone: http://eureka7001.com:7001/eureka
```

4. 主启动类

```java
@SpringBootApplication
@EnableEurekaClient
public class GatewayMain {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMain.class);
    }
}
```

目标：希望在8001端口外套一层9527

yaml修改

```yaml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      routes: # 可以添加多个路由
        - id: payment_route            # 路由id，没有固定规则，要求唯一，建议配合服务名
          uri: http://localhost:8001   # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/get/**    # 断言，路径相匹配的进行路由
```
访问说明：
添加路由前http://localhost:8001/payment/get/1
添加路由后http://localhost:9527/payment/get/1

### 配置路由的两种方式

* yaml配置

```yaml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      routes: # 可以添加多个路由
        - id: payment_route            # 路由id，没有固定规则，要求唯一，建议配合服务名
          uri: http://localhost:8001   # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/get/**    # 断言，路径相匹配的进行路由
```

* 代码注入RouterLocator的Bean

```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        //可以添加多个
        routes.route("path_route", 
                r -> r.path("/payment/get/**").uri("http://localhost:8001")).build();
        return routes.build();
    }
}
```

### 动态路由配置

```yaml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  # 开启从注册中心动态创建路由的功能，利用微服务进行路由
      routes:
        - id: payment_route            # 路由id，没有固定规则，要求唯一，建议配合服务名
          uri: lb://cloud-payment-service   # 提供服务名称
          predicates:
            - Path=/payment/get/**    # 断言，路径相匹配的进行路由
```

### gateway常用的predicate

[Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/2.2.7.RELEASE/reference/html/#gateway-request-predicates-factories)

* after

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        - After=2017-01-20T17:42:47.789-07:00[America/Denver]
```

请求时间在这之后

得到时间的方式

```java
public class TimeTest {
    @Test
    public void test(){
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println(now);
    }
}
```

* before

```yaml
        predicates:
        - Before=2017-01-20T17:42:47.789-07:00[America/Denver]
```

请求时间在这之前

* between

```yaml
predicates:
  - Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
```

* cookie

```yaml
      routes:
      - id: cookie_route
        uri: https://example.org
        predicates:
        - Cookie=chocolate, ch.p
```

请求cookie需要有chocolate，其值对应ch.p的正则表达式

* header

```yaml
        predicates:
        - Header=X-Request-Id, \d+
```

请求头有X-Request-Id，其值满足\d+表达式

* host

```yaml
        predicates:
        - Host=**.somehost.org,**.anotherhost.org
```

host需要满足指定要求

* method

```yaml
        predicates:
        - Method=GET,POST
```

确定请求方式

* path
* query

```yaml
        predicates:
        - Query=green
```

请求要有green参数

## filter

[Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/2.2.7.RELEASE/reference/html/#gatewayfilter-factories)

在请求路由前后对请求进行修改

生命周期

* pre
* post

种类

* GatewayFilter
* GlobalFilter

自定义全局过滤器

实现接口GlobalFilter，Ordered

```java
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
```

# 7. 服务配置中心和服务总线

## 服务配置中心config

工程越多，配置越多

配置文件需要统一管理

* 集中管理配置文件
* 不同环境不同配置，动态化配置更新，分环境部署，比如dev/test/prod/beta/release
* 运行期间动态调整配置
* 配置发生变化时不需要重启
* 将配置信息以rest接口的形式暴露

### config server配置

* 新建模块cloud-config-center-3344
* pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
```

* yaml

```yaml
server:
  port: 3344

spring:
  application:
    name: cloud-config-center
  cloud:
    config:
      server:
        git:
          uri: https://github.com/heiyuyinshi/spider.git # github中git仓库的名字
          search-paths:
            - spider
          username: heiyuyinshi # github账户
          password: fz8023koukai # github密码
          skipSslValidation: true # 跳过SSL
      label: master # 读取分支

eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
```
* 主启动类
```java
@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer
public class ConfigMain {
    public static void main(String[] args) {
        SpringApplication.run(ConfigMain.class);
    }
}
```
网址访问方式
```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
label：分支
application-profile文件
示例：http://localhost:3344/master/config-dev.yml

### config 客户端

* 新建cloud-config-client3355
* pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```

* yaml配置文件，bootstrap.yml

bootstrap文件是系统级的，优先级更高

application是用户级的资源配置项

```yaml
server:
  port: 3355

spring:
  application:
    name: config-client
  cloud:
    config:
      label: master # 分支名称
      name: config # 配置文件名称
      profile: dev # 读取后缀名称  上述3个综合，读取master分支上的config-dev.yml文件
      uri: http://localhost:3344  # 配置文件中心地址

eureka:
  client:
    service-url: 
      defaultZone: http://localhost:7001/eureka
```

测试

```java
@RestController
public class TestController {
    @Value("${info.file}")
    private String filename;

    @GetMapping("/config/file")
    public String getFilename(){
        return filename;
    }
}
```



### 动态刷新

* pom引入图形化监控

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

* 修改yml，暴露监控的端口

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

* 业务类添加注解@RefreshScope
* 运维工程师发送post请求刷新3355

`curl -X POST "http://localhost:3355/actuator/refresh"`

需求：要广播通知，告知所有微服务配置更新——》消息总线

## 消息总线

分布式自动刷新配置功能

bus支持两种消息代理：rabbitMQ和Kafka

管理和传播分布式系统间的消息

基本原理：configClient会监听MQ中同一个topic（默认是springCloudBus），当一个服务刷新数据时，会把该信息放入topic中，这样其他监听同一个topic的服务就能得到通知，然后去更新自身配置

### rabbitMQ配置

* 安装erlang：[Erlang Programming Language](https://www.erlang.org/downloads)
* 安装rabbitMQ：[Installing on Windows — RabbitMQ](https://www.rabbitmq.com/install-windows.html#installer)

* 进入rabbitMQ的sbin目录下执行`rabbitmq-plugins enable rabbitmq_management`
* 访问地址：localhost:15672，默认账号和密码guest

### bus动态广播

以3355为模板制作3366

两种设计思想

* 利用消息总线触发一个客户端/bus/refresh，从而刷新所有客户端的配置
* 利用消息总线触发一个服务端ConfigServer的/bus/refresh端点，从而刷新所有客户端的配置

第二种方式更加适合：

* 微服务本身是业务模块，不需要承担其他职责
* 破坏了相同节点的对称性
* 有一定的局限性

#### 服务端

1. 添加消息总线支持

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

2. yaml

```yaml
server:
  port: 3344

spring:
  application:
    name: cloud-config-center
  cloud:
    config:
      server:
        git:
          uri: https://github.com/heiyuyinshi/spider.git # github中git仓库的名字
          search-paths:
            - spider
          username: heiyuyinshi # github账户
          password: fz8023koukai # github密码
      label: master # 读取分支
  # rabbitmq相关配置
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
# 暴露bus-refresh
management:
  endpoints:
    web:
      exposure:
        include: 'bus-refresh'
```

#### 客户端

1. pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
```

2. yaml

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

运行，修改后，运维工程师发布消息给总线

`curl -X POST "http://localhost:3344/actuator/bus-refresh"`

#### 定点动态刷新

只刷新特定的客户端

`curl -X POST "http://localhost:{配置中心端口号}/actuator/bus-refresh/{destination}"`

示例，只通知3355

`curl -X POST "http://localhost:3344/actuator/bus-refresh/config-client:3355"`

# 8. stream

## 简介

消息驱动

常见消息中间件

ActiveMQ、RabbitMQ、RocketMQ、Kafka

一个系统中可能存在多个MQ，切换、维护存在问题

需要一种技术，不再关注MQ的细节，自动在各种MQ切换

屏蔽底层消息中间件的差异，降低切换成本，统一消息的编程模型

SpringCloud Stream构建消息驱动的微服务框架

应用程序通过inputs或者outputs来与Spring Cloud Stream中的**binder**对象交互

binder对象负责与消息中间件交互

目前仅支持RabbitMQ和Kafka

### 设计思想

标准的MQ

Pub—message—》Broker—message—》Sub

* 生产者和消费者按消息媒介传递消息内容
* 消息必须走特定通道

input对应消费者

output对应生产者

Stream遵循发布订阅模式：topic主题进行广播

* 在rabbitMQ中是Exchange
* 在Kafka中是topic

### 常用注解

Middleware：中间件，只支持RabbitMQ和Kafka

Binder：绑定器，应用与消息中间件之间的封装

@Input：注解标识输入通道，发布的消息进入应用程序

@Output：注解标识输出通道，消息离开应用程序

@StreamListener：监听队列，用户消费者的队列消息接收

@EnableBinding：channel和exchange绑定

## 工程创建

### 消息生产者

1. 创建工程cloud-stream-rabbitMQ-provider8801，pom

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
```

2. yaml

```yaml
server:
  port: 8801

spring:
  application:
    name: cloud-stream-provider
  cloud:
    stream:
      binders: # 配置要绑定的rabbitmq的服务信息
        defaultRabbit: # 表示定义的名称，用于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关环境配置
            spring:
              rabbitmq: 
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        output: # 一个通道的名称
          destination: studyExchange # 要使用exchange的名称定义
          content-type: application/json #消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置
eureka:
  client:
    service-url: 
      defaultZone: http://localhost:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2
    lease-expiration-duration-in-seconds: 5
    instance-id: send-8801.com
    prefer-ip-address: true
```

3. 主启动
4. 业务类

发送消息的接口

```java
public interface IMessageProvider {
    public String send();
}
```

接口实现类

```java
@EnableBinding(Source.class)//定义消息的推送管道
public class MessageProviderImpl implements IMessageProvider {
    @Resource
    private MessageChannel output;//消息发送管道

    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        System.out.println("*******serial:" + serial);
        return serial;
    }
}
```

controller

```java
@RestController
public class SendMessageController {
    @Resource
    private IMessageProvider messageProvider;

    @GetMapping(value = "/send")
    public String sendMessage(){
        return messageProvider.send();
    }
}
```

### 消息消费者

1. 创建工程cloud-stream-rabbitMQ-consumer8802
2. pom，与生产者相同
3. yml

```yaml
server:
  port: 8802

spring:
  application:
    name: cloud-stream-provider
  cloud:
    stream:
      binders: # 配置要绑定的rabbitmq的服务信息
        defaultRabbit: # 表示定义的名称，用于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        input: # 一个通道的名称
          destination: studyExchange # 要使用exchange的名称定义
          content-type: application/json #消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2
    lease-expiration-duration-in-seconds: 5
    instance-id: send-8802.com
    prefer-ip-address: true
```

4. 主启动
5. 业务代码

```java
@Component
@EnableBinding(Sink.class)
public class ReceiveMessageController {
    @Value("${server.port}")
    private String serverPort;

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        System.out.println("消费者1号，----》" + message.getPayload() + "\t port: " + serverPort);
    }
}
```

### 重复消费问题

发送一次，多个端会同时收到

通过消息分组解决

不同组可以消费同一条信息，而同一个组内会发生竞争关系，只有一个消费

yml设置分组

```yaml
      bindings: # 服务的整合处理
        input: # 一个通道的名称
          destination: studyExchange # 要使用exchange的名称定义
          content-type: application/json #消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置
          group: groupA
```

group相同的，组内每次只有一个消费者（默认轮询）

### 持久化

未设置group时，消费者宕机时，生产者生产的消息会丢失

因此一定要设置group

# 9. sleuth

分布式请求链路跟踪

微服务框架中，一个请求会在后端系统中经过多个不同的服务节点调用来协同产生最后的请求结果，会形成复杂链路

——》搭建链路监控

搭建步骤

1. 下载zipkin，使用`java -jar jar包`命令运行

原理：一条链路通过trace id唯一标识，span标识发起的请求信息，各span通过parent id关联起来

2. 跟踪的项目pom引入

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```

3. yml

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1 # 采样率介于0-1之间，1表示全采集，一般0.5即可
```

# 10. springcloud alibaba

## 背景

netflix大部分项目进入维护模式

springcloud alibaba增加内容

* 服务限流降级
* 服务注册与发现
* 分布式配置管理
* 消息驱动能力
* 阿里云对象存储
* 分布式任务调度

pom内容

```xml
<!--            alibaba版本-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2.1.0.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
```

## nacos

服务注册和配置中心

对应原来eureka+config+bus

官网：[home (nacos.io)](https://nacos.io/zh-cn/index.html)

1. 下载：[Release 1.4.1 (Jan 15, 2021) · alibaba/nacos · GitHub](https://github.com/alibaba/nacos/releases/tag/1.4.1)

2. 解压，进入bin，cmd输入`startup.cmd -m standalone`，以单机模式运行

3. 运行成功，访问localhost:8848/nacos，默认账号密码为nacos

### 服务提供者注册

使用手册：[Spring Cloud Alibaba Reference Documentation (spring-cloud-alibaba-group.github.io)](https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/en-us/index.html)

1. 新建module，cloudalibba-provider-payment9011
2. pom

```xml
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```

3. yaml

```yaml
server:
  port: 9011

spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 # 配置nacos地址
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

4. 主启动类

```java
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain9001.class);
    }
}
```

5. 业务类

```java
@RestController
public class PaymentController {
    @Value("${server.port}")
    private String serverPort;
    
    @GetMapping(value = "/payment/nacos/{id}")
    public String getPayment(@PathVariable("id") Integer id){
        return "nacos discovery, server port = " + serverPort + "\t id = " + id;
    }
}
```








# 1. dubbo和zookeeper

## 1.1 简介与安装
### 1.1.1 dubbo
java RPC框架
remote procedure call：远程过程调用
A，B两台服务器，A要调用B的方法或数据

架构
![image-20210214153638246](分布式.assets/image-20210214153638246.png)
provider：暴露服务的服务提供方
consumer：调用远程服务的服务消费方
registry：服务注册与发现的注册中心（官方推荐zookeeper）
monitor：统计服务的调用次数和调用时间的监控中心
container：服务运行容器

### 1.1.2 zookeeper安装
* 下载安装包
* 解压
`sudo tar -zxvf apache-zookeeper-3.6.1-bin.tar.gz -C /usr/local/`
* 进入解压目录，创建data文件夹，用于存放数据
* 进入conf目录，重命名zoo_sample.cfg——》zoo.cfg
`cp zoo_sample.cfg zoo.cfg`
* 修改zoo.cfg，主要修改dataDir
```shell
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
dataDir=/usr/local/apache-zookeeper-3.6.1-bin/data/
# the port at which the clients will connect
clientPort=2181
```
### 1.1.3 zookeeper启动
进入bin目录
`./zkServer.sh start`
查看运行状态
`./zkServer.sh status`
Mode: standalone说明正在运行
停止服务
`./zkServer.sh stop`

## 1.2 dubbo快速入门
### 1.2.1 服务提供方
* 创建maven工程
* 配置pom
前面需要配置spring
```xml
<!--    dubbo相关配置-->
<!--    dubbo相关配置-->
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>dubbo</artifactId>
      <version>2.6.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.zookeeper</groupId>
      <artifactId>zookeeper</artifactId>
      <version>3.6.1</version>
    </dependency>
    <dependency>
      <groupId>com.101tec</groupId>
      <artifactId>zkclient</artifactId>
      <version>0.10</version>
    </dependency>
    <dependency>
      <groupId>org.apache.curator</groupId>
      <artifactId>curator-framework</artifactId>
      <version>2.8.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.curator</groupId>
      <artifactId>curator-recipes</artifactId>
      <version>2.8.0</version>
    </dependency>
```
* service发布应用编写
```java
//添加dubbo提供的注解com.alibaba.dubbo.config.annotation.Service
//发布为服务
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello" + name;
    }
}
```
* web.xml编写
```xml
<web-app>
  <display-name>Archetype Created Web Application</display-name>

  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext*.xml</param-value>
  </context-param>
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
</web-app>
```
* applicationContext.xml编写
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd
http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd
http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

<!--    dubbo应用，每个dubbo都指定唯一名称-->
    <dubbo:application name="provider"/>
<!--    指定服务注册中心-->
    <dubbo:registry address="zookeeper://172.17.143.131:2181"></dubbo:registry>
<!--    配置协议和端口:默认为20880-->
    <dubbo:protocol name="dubbo" port="20880"/>
<!--    包扫描，用于发布服务-->
    <dubbo:annotation package="weichai.fuzheng.serviceImpl"/>
</beans>
```
### 1.2.2 服务消费方
* pom配置
与提供方相同
tomcat端口改为8082
防止冲突

# C2C 二手电商系统微服务架构



> 业务背景

所谓的C2C二手电商平台，就是用户可以在上面作为卖家发布自己的二手商品，然后等待买家来谈，来购买，平台就是作为中间服务方提供一些列的平台功能支持。

用户是买家也是卖家，但是会有一个问题，**可能卖家会上传违规商品**（侵犯版权的盗版商品、假冒伪劣商品、诈骗团伙上传的虚假商品、违法的商品），买家和卖家进行留言互动（通过IM系统进行私聊，对商品还可以进行评论）可能会进行一些**人身攻击之类的违规行为**，所以本质上这个电商平台从卖家到买家，都是用户，那怎么进行社会化的治理呢？

如果是平台方建立规模庞大的审核团队，那么可能会导致每一件商品以及发表的评论都需要进行预审，预审通过了平台才会给你显示出来，但是要知道一个问题，大型的C2C二手电商平台，可能里面日活用户达到数百万，甚至上千万，每天发表的评论可能也多达上千万，如果全部由自建的预审团队一条一条审核，那这个人力成本是无法接受的

这就需要有一个专门的社会化治理平台

通过一个平台，以技术的手段，将有人举报违规的内容推送给部分用户，让用户参与到平台治理中来，用户投票决定某个商品或者评论等内容是否违规，这样平台仅仅作为一个桥梁，让用户进行社会化自治



> 核心玩法

开发一个系统，很多非法举报进行社会化治理，所谓社会化治理，就是把每个举报都**圈定一部分用户作为评审员**，让他们进行**投票**，如果过半数判定举报成立，就成立，同时为了激励用户参与进行评审，可以给他们一些**奖励**，比如说奖励一些虚拟货币，后续可以在专门的积分兑换商城里兑换一些奖品



> 常见场景

（1） 通常这类平台会做一些用于社交的社区/论坛之类的，可以发帖之类的，所以这类帖子内容如果有一些不良言论，可能会被举报

（2） 有的人挂出的二手商品本身就可能是非法商品，可能会被举报

（3） 一般有人挂出自己的二手商品之后，别人可以进行留言提问，然后留言交互过程中可能会涉及到侮辱性的语言，此时留言可能会被举报

（4） 一般这类平台都会提供买卖双方进行私聊的IM功能，这个私聊对话可能涉及不良言论，可能会被举报

（5） 还有时候商品不光是涉及留言和私聊，对这个卖家售出的历史商品，买家可能是可以进行评论的，这个评论是卖家个人积累的一个信誉评价，但是可能出现不良言论，此时可能会被举报



> 抽取服务

针对上述场景，全部都可以在有人进行举报的时候，对应的功能模块（比如说商品、论坛、IM、评论，等等），就可以接入和调用社会化治理平台，我们对外提供的应该首先是一个举报服务，作为一个入口（可以做成一个MQ，与其他系统解耦，方便后期的扩展）

1. **举报服务**

投票制度管理（可以针对不同的举报类型，定义不同的投票制度，比如5人3胜，3人2胜，最大等待时长，替补评审员机制，等等），提交举报接口，调用评审员服务圈定评审员，PUSH管理，举报查询接口，投票生命周期管理（发起投票、过程监控、超时等待、候补评审员管理、投票结果），调用奖励服务

2. **评审员服务**

评审员管理（根据用户画像的标签，由运营去圈定一波人做评审员，其实核心在于圈定那些每周都至少会来逛一次的活跃用户，这个规则可以自行配置），评审员圈选，评审员状态管理，评审员过滤，疲劳度控制，评审员自动调整，候补评审员选择，评审结果接收

3. **奖励服务**

奖励规则配置，奖励发放，奖励兑换



> 架构设计图

![社会化治理平台架构设计](https://tva1.sinaimg.cn/large/e6c9d24egy1h52w0mv2jmj21a00kmq5b.jpg)



> 技术选型
>
> zookeeper 与 eureka 的对比
>
> zookeeper 是基于Raft协议来实现的，有leader和Follower角色，leader节点负责读写，而Follower负责读，leader写数据之后，会同步给follower。而且zookeeper是偏向于CP，会牺牲掉部分的可用性，来换取强一致性，如果leader宕机之后，会导致服务有一点时间不可用，这对于现代的微服务来说，是有点接受不了的
>
> eureka是 Peer to Peer模型，所有角色都是同等的，并且每个节点都是存有全量数据。内部原理也是基于三层缓存来实现高吞吐量，不过缺点就是，同步数据的时间间隔会比较长，默认设置要30秒，可以根据自己线上的需求，进行设置调优，让eureka整体的性能都有极大的提升。并且eureka某个节点挂了，并不会影响集群的使用，它是偏向于AP，会牺牲一些一致性，来换取高可用性。

![服务注册中心选型对比](https://tva1.sinaimg.cn/large/e6c9d24egy1h52w1ni5hoj21jt0j0wgj.jpg)

> Nacos 底层原理

Nacos是实现了spring cloud alibaba下面的ServiceRegistry的接口，NacosServiceRegistry就是实现类，实现了**register、deregister、close、setStatus、getStatus**之类的方法

然后通过spring boot的自动装配技术，将一些核心组件自动初始化，然后对将服务包装起来，调用register接口进行注册。

在这期间，还会初始化一些重要线程池，比如维护心跳的schedule线程池，源码如下：

```java
//BeatTask心跳任务
//beatInfo.getPeriod 心跳的时间间隔
this.exeutorService.schedule(new BeatReactor.BeatTask(beatInfo), beatInfo.getPeriod(), TimeUnit.MILLISECONDS)
```

注册好的实例，会存放在nacos server 里面的 ConcurrentHashMap 注册表，直接构造一个Service放在map里面，然后Service去addInstance 添加一个个实例。

注册之后，会将注册表的信息同步给其他nacos server，这个同步机制是基于raft协议。

nacos各个实例之间，会启动一个定时任务去增量拉取注册表。



> Feign + Ribbon、 dubbo 、 gRPC 选型对比

feign + ribbon, 是基于 spring cloud netflix技术栈，feign负责远程调用，会按照http协议来组装你的请求数据，数据格式都是按照http协议里的请求来做的，http请求还还必须做一个序列化，序列化成二进制的字节流，通过底层的tcp连接发送过去；ribbon负责负载均衡。比较轻量级的框架，性能相对来说比较一般。

dubbo 自己使用一套自定义协议，不是http协议，去组装请求数据，然后做序列化，变成二进制数组或者字节流，通过底层网络传输协议把数据发送过去。

gRPC 最大的特点就是跨平台，首先自己定义好 服务+接口的文件，然后去生成不同平台的gRPC类，然后通过gRPC底层的协议进行传输，最终实现跨平台。而且gRPC也有自己序列化，性能相对来说比较好。



![几种RPC框架技术对比](https://tva1.sinaimg.cn/large/e6c9d24egy1h52x0qegdqj20u0139acw.jpg)



> 项目实战

社会化治理(项目名称)：social govern

举报服务：report

评审员服务：reviewer

奖励服务：reward



> 框架整合





![image-20220811164326285](https://tva1.sinaimg.cn/large/e6c9d24egy1h52x4fql8bj20o20gozlk.jpg)



启动report项目，发现报 No provider available for the service com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService 错误，开始排查错误产生

具体错误如下：

```java
Failed to check the status of the service com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService. No provider available for the service com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService from the url dubbo://192.168.43.98/com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService?application=c2c-social-govern-report&dubbo=2.0.2&init=false&interface=com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService&methods=testRPC,selectReviewers,finishVote&pid=27655&qos.enable=false&register.ip=192.168.43.98&release=2.7.15&side=consumer&sticky=false&timestamp=1660347413034 to the consumer 192.168.43.98 use dubbo version 2.7.15

```

![image-20220812225251844](https://tva1.sinaimg.cn/large/e6c9d24egy1h54df6kbcij22j60k2teb.jpg)

github上面的issue：https://github.com/alibaba/spring-cloud-alibaba/issues/2007

主要解决，是版本依赖问题，具体可以参考spring cloud alibaba社区的博客

* [dubbo博客](https://dubbo.apache.org/zh/blog/java/codeanalysis/3.0.8/)

这里贴上我最新的pom包依赖

```xml
<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<maven.compiler.source>8</maven.compiler.source>
		<maven.compiler.target>8</maven.compiler.target>

		<!-- springcloud Alibaba 全家桶-->
		<spring-boot.version>2.2.4.RELEASE</spring-boot.version>
		<spring-cloud.version>Hoxton.SR1</spring-cloud.version>
		<spring-cloud-alibaba.version>2.2.0.RELEASE</spring-cloud-alibaba.version>

		<!-- Apache Dubbo -->
		<dubbo.version>2.7.10</dubbo.version>
		<curator.version>4.0.1</curator.version>

		<!-- Apache RocketMQ -->
		<rocketmq.starter.version>2.0.4</rocketmq.starter.version>
		<rocketmq.version>4.9.3</rocketmq.version>

		<knife4j.version>3.0.2</knife4j.version>
		<hutool-all.version>5.4.3</hutool-all.version>
		<druid-spring.version>1.1.23</druid-spring.version>
	</properties>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-parent</artifactId>
				<version>${spring-boot.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>com.alibaba.cloud</groupId>
				<artifactId>spring-cloud-alibaba-dependencies</artifactId>
				<version>${spring-cloud-alibaba.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>

			<!-- 引入 Spring Cloud Alibaba Dubbo 相关依赖，实现呢 Dubbo 进行远程调用，并实现对其的自动配置 -->
			<dependency>
				<groupId>com.alibaba.cloud</groupId>
				<artifactId>spring-cloud-starter-dubbo</artifactId>
			</dependency>

			<!-- mybatis代码自动生成插件 -->
			<dependency>
				<groupId>com.baomidou</groupId>
				<artifactId>mybatis-plus-generator</artifactId>
				<version>3.5.2</version>
			</dependency>
			<!--模板引擎依赖，MyBatis-Plus 支持 Velocity（默认）、Freemarker、Beetl，这里使用 Velocity 引擎-->
			<dependency>
				<groupId>org.apache.velocity</groupId>
				<artifactId>velocity-engine-core</artifactId>
				<version>2.3</version>
			</dependency>

			<dependency>
				<groupId>com.baomidou</groupId>
				<artifactId>mybatis-plus-boot-starter</artifactId>
				<version>3.5.2</version>
			</dependency>

			<!--hutool 工具类-->
			<dependency>
				<groupId>cn.hutool</groupId>
				<artifactId>hutool-all</artifactId>
				<version>${hutool-all.version}</version>
			</dependency>

			<!-- Druid数据连接池 -->
			<dependency>
				<groupId>com.alibaba</groupId>
				<artifactId>druid-spring-boot-starter</artifactId>
				<version>${druid-spring.version}</version>
			</dependency>

		</dependencies>
	</dependencyManagement>
```

* [参考大佬 ''芋道源码'' 的文章](https://www.iocoder.cn/Spring-Cloud-Alibaba/Dubbo/?self)





> 开发测试流程

![开发测试流程](https://tva1.sinaimg.cn/large/e6c9d24egy1h52xhnzm97j216m0e8jta.jpg)



> 互联网公司部署方案：蓝绿部署、灰度发布以及滚动发布

滚动发布：

最原始的滚动发布，就是一个服务/系统都会部署在多台机器上，部署的时候，要不然是手动依次部署，比如说每台服务器上放一个tomcat，每台机器依次停机tomcat，然后把新的代码放进去，再重新启动tomcat，各个机器逐渐重启，这就是最简单的滚动发布
现在中大型公司基本都集成了Jenkins，你在上面指定对一个服务，指定一个git仓库的代码分支，然后指定一个环境，指定一批机器，发布系统自动到git仓库拉取代码到本地，编译打包，然后在你指定环境的机器上，依次停止当前运行的进程，然后依次重启你新代码的服务进程

灰度发布：

指的就是说，不要上线就滚动全部发布到所有机器，一般就是会部署在比如1台机器上，采用新版本，然后切比如10%的流量过去，观察那10%的流量在1台机器上运行一段时间，比如运行个几天时间，观察日志、异常、数据，是否一切正常，如果验证发现全部正常，那么此时就可以全量发布了

蓝绿部署：

同时准备两个集群，一个集群放新版本代码，一个集群放老版本代码，然后新版本代码的集群准备好了过后，直接线上流量切到新版本集群上去，跑一段时间来验证，如果发现有问题，回滚就是立马把流量切回老版本集群，回滚是很快速的

如果新版本集群运行一段时间感觉没问题了，此时就可以把老版本集群给下线了



> MySQL的事务机制



**事务的ACID:**

A（Atomic）原子性：在事务内的一系列SQL，如果有一个SQL执行失败，所有的SQL都执行失败，要么所有都成功，要么所有都失败

C（Consistency）一致性：事务执行之前，数据是准确的，执行之后数据也是准确。

I（Isolation）隔离性：这个就是说多个事务在跑的时候不能互相干扰

D（Durability）持久性：事务成功了，就必须对数据的修改是永久生效的。



**事务的隔离机制：**

读未提交（Read Uncommitted）脏读：事务A在执行一个修改数据的操作，将name改成李四，此时还没提交，事务B过来读数据时候，就已经读到了name=李四的数据，会导致脏读产生。

读已提交（Read Committed）：这个级别可以解决脏读的问题，事务A在执行一个修改数据的操作，将name改成李四，此时还没提交，事务B过来读数据时候，读到的数据还是之前的数据 name=张三。但会造成不可重复读，比如在同一个事务中，多次读到的结果是不一样的。

可重复读（Read Repeatable）：这个级别可以解决不可重复读的问题，事务A在执行的过程中，无论执行多少次，读到的数据都是一样的，哪怕事务B修改了数据，并提交，事务A读到的数据，还是事务开启之前的数据。但会造成幻读问题，比如在查询范围的数据select count(*) from user; 此时读到的数据有3条，当别的事务添加1条数据并提交的时候，此时该事务再查，就会变成4条，明明在同一个事务内，多次读到的数据条数确实不一样的。

串行化：（Serialized）:这个可以解决幻读的问题，当一个事务没有执行完，另外一个事务是不可以执行的，就相当于流水线一样，一个个排队去执行，不过这样的效率是最低的。

MySQL默认的隔离机制就是Read Repeatable可重复读。就是说每个事务都会开启一个自己要操作的某个数据的快照，事务期间，读到的都是这个数据的快照罢了，对一个数据的多次读都是一样的。



> MySQL的MVCC机制：多版本并发控制，multi-version concurrency control
>
> id 	name 	创建事务id	删除事务id
>
> 1 		张三		 120				122
>
> 2		李四		  119				 空
>
> 2		小李四	  122				 空
>
> 你的事务其实对某行记录的查询，始终都是查找的之前的那个快照，因为之前的那个快照的创建时间小于等于自己事务id，然后删除时间的事务id比自己事务id大，所以这个事务运行期间，会一直读取到这条数据的同一个版本。
>
> 查询的条件：创建事务id <= 当前事务id，当前事务id < 删除事务id
>
> MVCC就是根据这个机制，来实现各种不同的隔离机制。





**Spring事务传播机制**

REQUIRED（Spring默认的事务传播机制）：如果当前没有事务，则自己新建一个事务，如果当前存在事务，则加入这个事务，如果事务内有任何地方报错，会导致所有的操作回滚。

SUPPORTS：当前存在事务，则加入当前事务，如果当前没有事务，就以非事务方法执行

MANDATORY：当前存在事务，则加入当前事务，如果当前事务不存在，则抛出异常。

REQUIRES_NEW：创建一个新事务，如果存在当前事务，则挂起该事务

NOT_SUPPORTED：始终以非事务方式执行,如果当前存在事务，则挂起当前事务

NEVER：不使用事务，如果当前事务存在，则抛出异常

NESTED：如果当前事务存在，则在嵌套事务中执行，否则REQUIRED的操作一样（开启一个事务）



> 分布式事务

**XA规范：**

就是定义好的那个（Transaction Manager，事务管理器）与RM（Resource Manager，资源管理器）之间的接口规范，就是管理分布式事务的那个组件跟各个数据库之间通信的一个接口

![05_XA规范与2PC协议](https://tva1.sinaimg.cn/large/e6c9d24egy1h5nf0umpufj20ze0g0wh5.jpg)

**2PC理论：**

其实就是基于XA规范，让分布式事务可以落地，定义了很多实现分布式事务过程中的一些细节

* 准备阶段：TM先发送个prepare消息给各个数据库，让各个库先把分布式事务里要执行的各种操作，先准备执行，其实此时各个库会差不多先执行好，然后各个数据库都返回一个响应消息给事务管理器，如果成功了就发送一个成功的消息，如果失败了就发送一个失败的消息
* 提交阶段：假如TM收到所有数据库返回的成功消息，则发送消息通知各个数据库说提交；反之，如果有收到某一个数据库返回失败的消息或者网络抖动迟迟没有收到消息，则通知所有的数据库，全部回滚事务。



![06_2PC的缺陷](https://tva1.sinaimg.cn/large/e6c9d24egy1h5nfg8ageyj20ua0dujs2.jpg)

**2PC的一些缺点：**

* 同步阻塞：在阶段一里执行prepare操作会占用资源，一直到整个分布式事务完成，才会释放资源，这个过程中，如果有其他人要访问这个资源，就会被阻塞住
* 单点故障：TM是个单点，一旦挂掉就完蛋了
* 事务状态丢失：即使把TM做成一个双机热备的，一个TM挂了自动选举其他的TM出来，但是如果TM挂掉的同时，接收到commit消息的某个库也挂了，此时即使重新选举了其他的TM，压根儿不知道这个分布式事务当前的状态，因为不知道哪个库接收过commit消息
* 脑裂问题：在阶段二中，如果发生了脑裂问题，那么就会导致某些数据库没有接收到commit消息，那就完蛋了，有些库收到了commit消息，结果有些库没有收到，这时候会导致数据不一致的问题。



**TCC理论：**

![12_TCC方案细节](https://tva1.sinaimg.cn/large/e6c9d24egy1h5nfxdrbx2j20r90iujt6.jpg)



* 主业务服务：相当于举报任务中心的服务，他就是TCC事务的主控服务，主要控制的服务，负责整个分布式事务的编排和管理，执行，回滚，都是他来控制

* 从业务服务：相当于我们的评审员服务、奖励服务，主要就是提供了3个接口，try-confirm-cancel，try接口里是锁定资源，confirm是业务逻辑，cancel是回滚逻辑

* 业务活动管理器：管理具体的分布式事务的状态，分布式事务中各个服务对应的子事务的状态，包括就是他会负责去触发各个从业务服务的confirm和cancel接口的执行和调用。。。

TCC分为三个阶段：

* try阶段：资源的锁定，锁定的资源，就会被阻塞住，不允许被操作
* confirm阶段：将各个操作提交到数据，最终把数据更新了
* cancel阶段：try阶段任何一个服务有问题的话，那么就cancel掉，将一切操作回滚。



**执行流程：**

（1）主业务服务会先在本地开启一个本地事务（这个本地事务说白了，就是你的主业务服务是不是也可能会干点儿什么事儿）

（2）主业务服务向业务活动管理器申请启动一个分布式事务活动，主业务服务向业务活动管理器注册各个从业务活动

（3）接着主业务服务负责调用各个从业务服务的try接口

（4）如果所有从业务服务的try接口都调用成功的话，那么主业务服务就提交本地事务，然后通知业务活动管理器调用各个从业务服务的confirm接口

（5）如果有某个服务的try接口调用失败的话，那么主业务服务回滚本地事务，然后通知业务活动管理器调用各个从业务服务的cancel接口

（6）如果主业务服务触发了confirm操作，但是如果confirm过程中有失败，那么也会让业务活动管理器通知各个从业务服务cancel

（7）最后分布式事务结束



> 分布式框架 seata学习

GitHub地址：https://github.com/seata/seata

用户使用文档：https://seata.io/zh-cn/docs/ops/deploy-guide-beginner.html

seata sample的GitHub地址：https://github.com/seata/seata-samples



> 微服务的限流和熔断框架：Hystrix 与 Sentinel



**Hystrix学习：**

Hystrix是netflix公司旗下的产品，功能性都不错，不过源码写得很烂，这个是参考石衫老师说的。

工作原理：Hystrix 通过[线程池](https://github.com/Netflix/Hystrix/wiki/How-it-Works#benefits-of-thread-pools)的方式，来对依赖(在我们的概念中对应资源)进行了隔离。这样做的好处是资源和资源之间做到了最彻底的隔离。缺点是除了增加了线程切换的成本，还需要预先给各个资源做线程池大小的分配。

学习文档：https://github.com/Netflix/Hystrix/wiki/



**Sentinel学习：**

随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 是面向分布式、多语言异构化服务架构的流量治理组件，主要以流量为切入点，从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度来帮助开发者保障微服务的稳定性。

Sentinel 和 Hystrix 的原则是一致的: 当调用链路中某个资源出现不稳定，例如，表现为 timeout，异常比例升高的时候，则对这个资源的调用进行限制，并让请求快速失败，避免影响到其它的资源，最终产生雪崩的效果。

![](https://tva1.sinaimg.cn/large/e6c9d24egy1h5nhnz1yzej20vg0jomyw.jpg)



学习文档：https://sentinelguard.io/zh-cn/docs/introduction.html



> 项目整合Sentinel

* 引入依赖：

```xml
<dependency>
  <groupId>com.alibaba.cloud</groupId>
  <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

* 定义资源：

```java
 //用SentinelResource来定义资源，value就是资源的名称，blockHandlerClass指定限流的类，fallbackClass指定降级的类
@SentinelResource(value = "selectReviewers",
            blockHandlerClass = DefaultBlockRequestHandler.class,
            fallbackClass = DefaultBlockExceptionHandler.class)
    public List<Long> selectReviewers(Long taskId) {
        return reviewerService.selectReviewers(taskId);
    }
```



* 配置规则：

```java
@Configuration
public class SentinelWebConfig {

    @Bean
    public BlockExceptionHandler sentinelBlockExceptionHandler() {
        return (request, response, e) -> {
            // 429 Too Many Requests
            response.setStatus(429);

            PrintWriter out = response.getWriter();
            out.print("Oops, blocked by Sentinel: " + e.getClass().getSimpleName());
            out.flush();
            out.close();
        };
    }
}
```



* 查看控制台

http://localhost:7070/

账号密码：sentinel/sentinel

![image-20220829152552261](https://tva1.sinaimg.cn/large/e6c9d24egy1h5no1b93pij21o70u0n1l.jpg)



> 配置中心选型对比：Spring cloud Config ， Apollo， Nacos

Spring cloud Config ，是spring cloud全家桶 Netflix里面的技术栈之一，很好整合了Spring cloud那套规范，如果前期用了Spring cloud全家桶，推荐使用这套config

学习地址：https://github.com/spring-cloud/spring-cloud-config

Apollo：是携程开源出来的项目，功能非常完善，社区活跃，而且是国产开源，对中国的开发者非常友好。不过运维成本高，需要部署多台服务器完成高可用。

学习地址：https://www.apolloconfig.com/#/zh/README GitHub地址：https://github.com/apolloconfig/apollo

Nacos： Spring cloud Alibaba全家桶的技术栈，功能完善，社会活跃，使用简单，对开发者开箱即用，非常推荐使用（本项目就是使用Nacos作为配置中心）

学习地址：https://nacos.io/zh-cn/docs/what-is-nacos.html   GitHub地址：https://github.com/alibaba/nacos



> 项目整合nacos实现配置中心的功能

* 添加依赖：

```xml
   <!-- alibaba nacos整合 配置中心-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

```



* 添加bootstrap.yml配置

```yml
spring:
  application:
    name: c2c-social-govern-report
  cloud:
    nacos:
      #配置中心
      config:
        enabled: true
        server-addr: 127.0.0.1:8848
        ext-config:
          - dataId: spring-druid.yml
            refresh: true
```

* 在nacos控制台添加 spring-druid.yml配置

![image-20220829154348841](https://tva1.sinaimg.cn/large/e6c9d24egy1h5nojyop5oj21dz0u0jv5.jpg)

* 启动项目



> 监控中心技术选型：Zabbix、Falcon、Prometheus

**选型背景**

这次采用开源监控系统主要是为了监控一些自定义业务并进行告警，所以重点关注了上传自定义数据、监控、显示、存储、告警四个方面，由于公司运维已经有监控机器指标的工具，所以监控机器指标这方面不是重点。

### Zabbix

官方文档：[https://www.zabbix.com/documentation/3.4/zh/manual](https://links.jianshu.com/go?to=https%3A%2F%2Fwww.zabbix.com%2Fdocumentation%2F3.4%2Fzh%2Fmanual)

官方文档比较详细，中英文都有。

后端用C开发，界面用PHP开发。

图形化界面比较成熟，界面上基本能完成全部的配置操作。

### Prometheus

官方文档：[https://prometheus.io](https://links.jianshu.com/go?to=https%3A%2F%2Fprometheus.io%2F)

第三方中文文档：[https://songjiayang.gitbooks.io/prometheus/content/introduction/what.html](https://links.jianshu.com/go?to=https%3A%2F%2Fsongjiayang.gitbooks.io%2Fprometheus%2Fcontent%2Fintroduction%2Fwhat.html)

go语言开发，前端支持Grafana展示

抓取数据是通过node_exporter插件，这些插件有官方的也有第三方的。引入插件的同时必须配置监控的指标。

与其他监控框架显著的不同是支持灵活的查询语句（PromQL）



### open-Falcon

官方文档:[http://book.open-falcon.com/zh_0_2/dev/support_grafana.html](https://links.jianshu.com/go?to=http%3A%2F%2Fbook.open-falcon.com%2Fzh_0_2%2Fdev%2Fsupport_grafana.html)

后台是go语言开发，前端支持Grafana展示

抓取数据通过脚本插件抓取日志或调用CLI，脚本组装数据发送给主服务端，配置是可选项，在脚本插件处进行配置。



文章出自[Sunrise95](https://www.jianshu.com/u/fdc768540af0)：https://www.jianshu.com/p/210dc70b493e



> 链路追踪框架选型：Skywalking，Pinpoint， Cat， zipkin

**各自对比：**

* CAT是一个更综合性的平台,提供的监控功能最全面，国内几个大厂生产也都在使用。但研发进度及版本更新相对较慢。

* Zipkin由Twitter开源，调用链分析工具，基于spring-cloud-sleuth得到广泛使用，非常轻量，使用部署简单。

* Skywalking专注于链路和性能监控，国产开源，埋点无侵入，UI功能较强。能够加入Apache孵化器，设计思想及代码得到一定认可，后期应该也会有更多的发展空间及研发人员投入。目前使用厂商最多。版本更新较快。

* Pinpoint专注于链路和性能监控，韩国研发团队开源，埋点无侵入，UI功能较强，但毕竟是小团队，不知道会不会一直维护着，目前版本仍在更新中

文章出自：https://zhuanlan.zhihu.com/p/60436915



重点可以关注 Skywalking 这个框架，现在是国内的主流。

Skywalking学习文档：https://skywalking.apache.org/zh/2020-04-19-skywalking-quick-start/

GitHub地址：https://github.com/apache/skywalking

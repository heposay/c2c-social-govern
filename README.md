# C2C 二手电商系统微服务架构学习



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

> Nacos 注册中心

![Nacos架构原理](https://tva1.sinaimg.cn/large/e6c9d24egy1h52w9jncmtj210m0kkgnq.jpg)

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







> 开发测试流程

![开发测试流程](https://tva1.sinaimg.cn/large/e6c9d24egy1h52xhnzm97j216m0e8jta.jpg)





> MySQL的事务机制
>
> 事务的ACID:
>
> A（Atomic）原子性：在事务内的一系列SQL，如果有一个SQL执行失败，所有的SQL都执行失败，要么所有都成功，要么所有都失败
>
> C（Consistency）一致性：事务执行之前，数据是准确的，执行之后数据也是准确。
>
> I（Isolation）隔离性：这个就是说多个事务在跑的时候不能互相干扰
>
> D（Durability）持久性：事务成功了，就必须对数据的修改是永久生效的。
>
> 事务的隔离机制：
>
> 读未提交（Read Uncommitted）脏读：事务A在执行一个修改数据的操作，将name改成李四，此时还没提交，事务B过来读数据时候，就已经读到了name=李四的数据，会导致脏读产生。
>
> 读已提交（Read Committed）：这个级别可以解决脏读的问题，事务A在执行一个修改数据的操作，将name改成李四，此时还没提交，事务B过来读数据时候，读到的数据还是之前的数据 name=张三。但会造成不可重复读，比如在同一个事务中，多次读到的结果是不一样的。
>
> 可重复读（Read Repeatable）：这个级别可以解决不可重复读的问题，事务A在执行的过程中，无论执行多少次，读到的数据都是一样的，哪怕事务B修改了数据，并提交，事务A读到的数据，还是事务开启之前的数据。但会造成幻读问题，比如在查询范围的数据select count(*) from user; 此时读到的数据有3条，当别的事务添加1条数据并提交的时候，此时该事务再查，就会变成4条，明明在同一个事务内，多次读到的数据条数确实不一样的。
>
> 串行化：（Serialized）:这个可以解决幻读的问题，当一个事务没有执行完，另外一个事务是不可以执行的，就相当于流水线一样，一个个排队去执行，不过这样的效率是最低的。
>
> MySQL默认的隔离机制就是Read Repeatable可重复读。就是说每个事务都会开启一个自己要操作的某个数据的快照，事务期间，读到的都是这个数据的快照罢了，对一个数据的多次读都是一样的。



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
> 查询的条件：创建事务id <= 当前事务id，当前事务id < 删除事务id
>
> MVCC就是根据这个机制，来实现各种不同的隔离机制。






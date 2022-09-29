package com.hepo.c2c.social.govern.mall;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.hepo.c2c.social.govern.mall.domain.Shop;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.domain.UserInfo;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.service.IUserInfoService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.service.impl.ShopServiceImpl;
import com.hepo.c2c.social.govern.mall.utils.RedisIdWorker;
import com.hepo.c2c.social.govern.mall.utils.UserInfoGeneUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest(classes = MallApplication.class)
public class AppTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    RedisIdWorker redisIdWorker;

    @Resource
    RedissonClient redissonClient;

    RLock lock;

    @BeforeEach
    public void setup() {
        lock = redissonClient.getLock("voucher");
    }

    /**
     * 加载商户geo数据到redis
     */
    @Test
    public void loadShopData() {
        //1.查询店铺信息
        List<Shop> shopList = shopService.list();

        //2.把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> typeShopMap = shopList.stream().collect(Collectors.groupingBy(Shop::getTypeId));

        //3.分批写入redis
        for (Map.Entry<Long, List<Shop>> entry : typeShopMap.entrySet()) {
            //3.1组装redis key
            Long typeId = entry.getKey();
            String redisKey = SHOP_GEO_KEY + typeId;
            //3.2获取同类型的店铺的集合
            List<Shop> shops = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(shops.size());
            for (Shop shop : shops) {
                locations.add(new RedisGeoCommands.GeoLocation<>(shop.getId().toString(), new Point(shop.getX(), shop.getY())));
            }
            stringRedisTemplate.opsForGeo().add(redisKey, locations);
        }
    }

    @Test
    public void redisTest() {
        System.out.println(stringRedisTemplate.opsForValue().get("k1"));
    }

    @Test
    public void testSaveRedis() {

        for (int i = 0; i < 1000; i++) {
            stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + i, i + "");
        }

    }

    @Resource
    private IUserService userService;
    @Resource
    private IUserInfoService userInfoService;

    @Test
    public void testBatchAddUser() {
        List<User> userData = new ArrayList<>(1000);
        List<UserInfo> userInfoData = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            Map<String, String> dataMap = UserInfoGeneUtils.getAddress();
            User user = new User();
            long userId = redisIdWorker.nextId("user");
            user.setId(userId);
            user.setPhone(dataMap.get("tel"));
            user.setNickName(dataMap.get("name"));
            userData.add(user);

            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setFans(0);
            userInfo.setGender("男".equals(dataMap.get("sex")) ? 1 : 2);
            userInfo.setCredits(0);
            userInfo.setFollowee(0);
            userInfo.setIntroduce(dataMap.get("road"));
            userInfoData.add(userInfo);
        }
        userService.saveBatch(userData);
        userInfoService.saveBatch(userInfoData);
    }

    @Test
    public void testUserTokenSaveBatch2Redis() throws InterruptedException {
        List<User> userList = userService.query().list();
        List<String> tokenList = new ArrayList<>();
        for (User user : userList) {
            String token = UUID.randomUUID().toString(true);
            //5.2将user转化成map存储
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO);
            tokenList.add(token);
            //5.3存储到redis
            stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        }
        FileUtil.writeLines(tokenList, "/Users/linhaibo/Documents/devtool/jmeter/bin/token.csv", "UTF-8");
    }


    @Test
    public void testRedisIdWorker() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {

            long id = redisIdWorker.nextId("order");
        }
        long end = System.currentTimeMillis();
        System.out.println("生成1000000个id，耗时：" + (end - begin));
    }

    @Test
    public void method1() throws InterruptedException {
        //尝试获取锁
        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock) {
            System.out.println("method1获取锁失败");
            return;
        }

        try {
            System.out.println("method1获取锁成功！");
            method2();
            System.out.println("method1执行业务完毕");
        } finally {
            System.out.println("method1开始释放锁！");
            lock.unlock();
        }
    }

    public void method2() {
        //尝试获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            System.out.println("method2获取锁失败");
            return;
        }

        try {
            System.out.println("method2获取锁成功！");
            Thread.sleep(10);
            System.out.println("method2执行业务完毕");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("method2开始释放锁！");
            lock.unlock();
        }
    }

}

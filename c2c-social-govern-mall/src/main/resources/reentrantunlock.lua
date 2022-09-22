--可重入释放锁脚本
local key = KEYS[1]   --锁的key
local threadId = ARGV[1] --锁唯一标识
local releaseTime = ARGV[2]  --锁的自动释放时间
--判断锁是否存在
if(redis.call('hexists', key, threadId) == 0) then
   return nil; --说明不是自己持有的锁，直接返回
end
local count = redis.call('hincrby', key, threadId, -1);
--判断是否重入次数为0
if(count > 0) then
    --大于0说明不能直接释放锁，重置锁的有效期然后返回
    redis.call('expire', key, releaseTime);
    return nil;
else
    -- 等于0说明可以释放锁，直接删除
    redis.call('del', key);
    return nil;
end
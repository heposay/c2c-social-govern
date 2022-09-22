-- 可重入锁脚本
local key = KEYS[1]   --锁的key
local threadId = ARGV[1] --锁唯一标识
local releaseTime = ARGV[2]  --锁的自动释放时间

--判断所是否存在
if(redis.call('exists', key) == 0) then
    --不存在，获取锁
    redis.call('hset', key, threadId, '1');
    --设置锁的有效期
    redis.call('expire', key, releaseTime);
    return 1;  --返回结果
end
--存在，判断threadId是否是自己
if(redis.call('hexists', key, threadId) == 1) then
    --是自己，获取锁，重入次数+1
    redis.call('hincrby', key, threadId, '1');
    --设置锁的有效期
    redis.call('expire', key, releaseTime);
    return 1;
end
return 0;   --获取锁失败
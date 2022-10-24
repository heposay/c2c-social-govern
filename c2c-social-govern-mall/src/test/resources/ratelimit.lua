--获取当前redis令牌桶的最后请求时间和当前令牌的数量
local ratelimit_info = redis.pcall('HMGET', KEYS[1], 'last_time', 'current_token')
local last_time = ratelimit_info[1]
local current_token = tonumber(ratelimit_info[2])

-- 令牌桶的最大数量
local max_token = tonumber(ARGV[1])
-- 令牌桶生成速率
local token_rate = tonumber(ARGV[2])
-- 当前时间
local current_time = tonumber(ARGV[3])
-- 生成一个令牌所需的时间
local reverse_time = 1000 / token_rate

--如果当前令牌为空，则进行初始化
if current_token == nil then
    current_token = max_token
    last_time = current_time
else
    -- 计算出两次请求之间的时间间隔
    local past_time = current_time - last_time
    -- 计算这段时间内生成token的个数
    local reverse_token = math.floor(past_time / reverse_time)
    -- 将这些token与当前token个数相加，
    current_token = current_token + reverse_token
    last_time =  current_time
    if current_token > max_token then
        current_token = max_token
    end
end
-- 消耗令牌
local result = 0
if (current_token > 0) then
    result = 1
    current_token = current_token - 1
end
--将结果保存到redis
redis.call('HMSET', KEYS[1], 'last_time', last_time, 'current_token', current_token)
--设置过期时间
redis.call("PEXPIRE", KEYS[1], math.ceil(reverse_time * (max_token - current_token) + (current_time - last_time)))

return result
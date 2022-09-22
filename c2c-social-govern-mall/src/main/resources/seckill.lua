-- 优惠券id
local voucherId = ARGV[1]
-- 用户id
local userId = ARGV[2]
-- 订单id
--local orderId = ARGV[3]

--库存key
local stockKey = "seckill:stock:" .. voucherId
--订单key
local orderKey = "seckill:order:" .. voucherId

--业务逻辑
--1.判断库存是否充足
if(tonumber(redis.call('get', stockKey)) <= 0) then
    --1.1库存不足，返回1
    return 1
end
--2.判断用户是否已下单
if(redis.call('sismember', orderKey, userId) == 1) then
    --存在，说明重复下单，返回2
    return 2
end
--3.扣减库存
redis.call('incrby', stockKey, -1)
return 0
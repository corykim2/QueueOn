-- KEYS[1] = 대기열 (queue:{scheduleId})
-- KEYS[2] = 카운터 (queue:{scheduleId}:seq)
-- ARGV[1] = userId

-- 1. 카운터에서 진입 번호 받기
local seq = KEYS[2]

-- 2. 진입 번호를 점수로 대기열에 넣기 (이미 있으면 점수만 갱신 = 맨 뒤로)
redis.call('ZADD', KEYS[1], seq, ARGV[1])

-- 3. 반환 (필요하면)
return redis.call('ZRANK', KEYS[1], ARGV[1])
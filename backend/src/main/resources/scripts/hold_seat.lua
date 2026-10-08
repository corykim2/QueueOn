-- hold_seat.lua
-- KEYS[1] = hold 키      (hold:{scheduleId}:{seatNo})
-- KEYS[2] = blocked 키   (blocked:{scheduleId})
-- ARGV[1] = userId
-- ARGV[2] = 좌석번호
-- ARGV[3] = TTL(초)

-- 1. hold에 없을 때만 저장 (NX), 만료 시간 지정 (EX)
local result = redis.call('SET', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[3])

-- 2. 성공/실패와 상관없이 blocked에 좌석 추가
redis.call('SADD', KEYS[2], ARGV[2])

-- 3. 1번 결과로 분기
if result then
    return 1   -- 선점 성공
end

return 0       -- 이미 선점됨
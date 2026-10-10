import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'

// 좌석 배치 응답 (백엔드 SeatLayoutResponse랑 맞춤)
interface SeatLayout {
  scheduleId: number
  seatCount: number
  blockedSeats: number[]   // 막힌 좌석 번호들 (내 선점/확정 포함)
}

// 409 응답 본문 (백엔드 ErrorResponse랑 맞춤)
interface ErrorResponse {
  code: string
  message: string
}

// 테스트용 유저 (지금은 하드코딩, 나중에 로그인)
const USER_ID = '1'
const API = 'http://localhost:8080/api/schedules'

function SeatPage() {
  // 1. URL에서 scheduleId 꺼내기 (/schedules/1/seats → "1")
  const { scheduleId } = useParams()

  // 2. 좌석 데이터
  const [layout, setLayout] = useState<SeatLayout | null>(null)

  // 3. 내가 선점한 좌석들 (프론트에서만 기억, 새로고침하면 사라짐)
  const [myHolds, setMyHolds] = useState<number[]>([])

  // 4. 내가 확정한 좌석들 (화면 표시용)
  const [myConfirmed, setMyConfirmed] = useState<number[]>([])

  // 5. 좌석 배치 불러오기 (RESV-01)
  const load = () => {
    fetch(`${API}/${scheduleId}/seats`)
      .then((res) => res.json())
      .then((data: SeatLayout) => setLayout(data))
      .catch((err) => console.error('좌석 불러오기 실패:', err))
  }

  // 화면 뜰 때 한 번 불러오기
  useEffect(() => {
    load()
  }, [scheduleId])

  // 6. 좌석 클릭 → 선점 (RESV-02)
  const holdSeat = (seatNumber: number) => {
    fetch(`${API}/${scheduleId}/seats/${seatNumber}/hold`, {
      method: 'POST',
      headers: { 'X-User-Id': USER_ID },
    })
      .then((res) => {
        if (res.status === 201) {
          setMyHolds((prev) => [...prev, seatNumber])   // 내 선점으로 기억
          alert(`${seatNumber}번 좌석 선점 성공! 아래에서 확정하세요.`)
        } else if (res.status === 409) {
          alert(`${seatNumber}번은 이미 선점된 좌석입니다`)
        } else {
          alert(`선점 실패 (status: ${res.status})`)
        }
        load()   // 성공이든 실패든 최신 상태 다시 조회
      })
      .catch((err) => console.error('선점 요청 실패:', err))
  }

  // 7. 확정 (RESV-03)
  const confirmSeat = (seatNumber: number) => {
    fetch(`${API}/${scheduleId}/seats/${seatNumber}/confirm`, {
      method: 'POST',
      headers: { 'X-User-Id': USER_ID },
    })
      .then(async (res) => {
        if (res.status === 201 || res.status === 200) {
          // 201: 새로 확정 / 200: 이미 확정 → 둘 다 성공
          setMyHolds((prev) => prev.filter((n) => n !== seatNumber))
          setMyConfirmed((prev) => (prev.includes(seatNumber) ? prev : [...prev, seatNumber]))
          alert(res.status === 201
            ? `${seatNumber}번 좌석 예매 확정!`
            : `${seatNumber}번은 이미 확정된 좌석입니다`)
        } else if (res.status === 409) {
          const err: ErrorResponse = await res.json()
          // 확정 실패면 내 선점 목록에서도 제거 (만료됐거나 남의 좌석)
          setMyHolds((prev) => prev.filter((n) => n !== seatNumber))
          alert(err.message)
        } else {
          alert(`확정 실패 (status: ${res.status})`)
        }
        load()
      })
      .catch((err) => console.error('확정 요청 실패:', err))
  }

  // 아직 데이터 안 왔으면 로딩
  if (layout === null) {
    return <div>불러오는 중...</div>
  }

  // 막힌 좌석을 빠르게 조회하려고 Set으로
  const blocked = new Set(layout.blockedSeats)

  // 8. 좌석 색상: 빈자리 / 선택 불가 두 가지
  const seatColor = (n: number) => (blocked.has(n) ? '#bbb' : '#7c4dff')

  return (
    <div style={{ padding: 20 }}>
      <Link to="/">← 목록으로</Link>
      <h1>좌석 선택 (회차 {layout.scheduleId})</h1>
      <p>전체 {layout.seatCount}석 · 막힘 {layout.blockedSeats.length}석</p>

      {/* 색상 안내 */}
      <div style={{ display: 'flex', gap: 12, fontSize: 13, marginTop: 8 }}>
        <span style={{ color: '#7c4dff' }}>■ 빈자리</span>
        <span style={{ color: '#bbb' }}>■ 선택 불가</span>
      </div>

      {/* 좌석판 (1 ~ seatCount) */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(10, 40px)',   // 한 줄에 10개
          gap: 6,
          marginTop: 16,
        }}
      >
        {Array.from({ length: layout.seatCount }, (_, i) => i + 1).map((n) => {
          const isBlocked = blocked.has(n)
          return (
            <button
              key={n}
              disabled={isBlocked}
              onClick={() => holdSeat(n)}
              style={{
                height: 40,
                cursor: isBlocked ? 'not-allowed' : 'pointer',
                background: seatColor(n),
                color: 'white',
                border: 'none',
                borderRadius: 4,
              }}
            >
              {n}
            </button>
          )
        })}
      </div>

      {/* 내 선점 좌석 + 확정 버튼 */}
      {myHolds.length > 0 && (
        <div style={{ marginTop: 24 }}>
          <h3>선점한 좌석</h3>
          {myHolds.map((n) => (
            <div key={n} style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 8 }}>
              <span>{n}번 좌석</span>
              <button
                onClick={() => confirmSeat(n)}
                style={{
                  padding: '6px 16px',
                  background: '#2e7d32',
                  color: 'white',
                  border: 'none',
                  borderRadius: 4,
                  cursor: 'pointer',
                }}
              >
                예매 확정
              </button>
            </div>
          ))}
        </div>
      )}

      {/* 내 확정 좌석 */}
      {myConfirmed.length > 0 && (
        <p style={{ marginTop: 16, color: '#2e7d32' }}>
          확정된 좌석: {myConfirmed.join(', ')}번
        </p>
      )}
    </div>
  )
}

export default SeatPage
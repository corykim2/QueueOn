import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'

// 좌석 배치 응답 (백엔드 SeatLayoutResponse랑 맞춤)
interface SeatLayout {
  scheduleId: number
  seatCount: number
  blockedSeats: number[]   // 막힌 좌석 번호들
}

// 테스트용 유저 (지금은 하드코딩, 나중에 로그인)
const USER_ID = '1'

function SeatPage() {
  // 1. URL에서 scheduleId 꺼내기 (/schedules/1/seats → "1")
  const { scheduleId } = useParams()

  // 2. 좌석 데이터 박스
  const [layout, setLayout] = useState<SeatLayout | null>(null)

  // 3. 좌석 배치 불러오는 함수 (조회 = RESV-01)
  const load = () => {
    fetch(`http://localhost:8080/api/schedules/${scheduleId}/seats`)
      .then((res) => res.json())
      .then((data: SeatLayout) => setLayout(data))
      .catch((err) => console.error('좌석 불러오기 실패:', err))
  }

  // 4. 화면 뜰 때 한 번 불러오기
  useEffect(() => {
    load()
  }, [scheduleId])

  // 5. 좌석 클릭 → 선점 요청 (RESV-02)
  const holdSeat = (seatNumber: number) => {
    fetch(
      `http://localhost:8080/api/schedules/${scheduleId}/seats/${seatNumber}/hold`,
      {
        method: 'POST',
        headers: { 'X-User-Id': USER_ID },   // 유저 식별 (하드코딩)
      }
    )
      .then((res) => {
        if (res.status === 201) {
          alert(`${seatNumber}번 좌석 선점 성공!`)
        } else if (res.status === 409) {
          alert(`${seatNumber}번은 이미 선점된 좌석입니다`)
        } else {
          alert(`선점 실패 (status: ${res.status})`)
        }
        load()   // 성공이든 실패든 최신 상태 다시 조회
      })
      .catch((err) => console.error('선점 요청 실패:', err))
  }

  // 6. 아직 데이터 안 왔으면 로딩
  if (layout === null) {
    return <div>불러오는 중...</div>
  }

  // 7. 막힌 좌석을 빠르게 조회하려고 Set으로
  const blocked = new Set(layout.blockedSeats)

  // 8. 좌석판 그리기 (1 ~ seatCount)
  return (
    <div style={{ padding: 20 }}>
      <Link to="/">← 목록으로</Link>
      <h1>좌석 선택 (회차 {layout.scheduleId})</h1>
      <p>전체 {layout.seatCount}석 · 막힘 {layout.blockedSeats.length}석</p>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(10, 40px)',   // 한 줄에 10개
          gap: 6,
          marginTop: 16,
        }}
      >
        {/* 1부터 seatCount까지 좌석 버튼 만들기 */}
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
                background: isBlocked ? '#bbb' : '#7c4dff',  // 막힘=회색, 빈=보라
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
    </div>
  )
}

export default SeatPage
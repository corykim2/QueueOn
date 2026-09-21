import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'

// 회차 (백엔드 ScheduleResponse랑 맞춤)
interface Schedule {
  id: number
  performanceDate: string
}

// 상세 응답 (백엔드 ShowDetailResponse랑 맞춤)
interface ShowDetail {
  id: number
  name: string
  seatCount: number
  bookingOpenAt: string
  bookable: boolean
  schedules: Schedule[]
}

function ShowDetailPage() {
  // 1. URL에서 showId 꺼내기 (/shows/3 → "3")
  const { showId } = useParams()

  // 2. 상세 데이터 박스 (아직 없으면 null)
  const [show, setShow] = useState<ShowDetail | null>(null)

  // 3. 화면 뜰 때 상세 API 호출
  useEffect(() => {
    fetch(`http://localhost:8080/api/shows/${showId}`)
      .then((res) => res.json())
      .then((data: ShowDetail) => {
        setShow(data)
      })
      .catch((err) => console.error('상세 불러오기 실패:', err))
  }, [showId])

  // 4. 아직 데이터 안 왔으면 로딩 표시
  if (show === null) {
    return <div>불러오는 중...</div>
  }

  // 5. 데이터 왔으면 상세 그리기
  return (
    <div>
      <Link to="/">← 목록으로</Link>
      <h1>{show.name}</h1>
      <p>좌석 수: {show.seatCount}개</p>
      <p>예매 오픈: {show.bookingOpenAt}</p>

      <h2>회차</h2>
      <ul>
        {show.schedules.map((schedule) => (
          <li key={schedule.id}>{schedule.performanceDate}</li>
        ))}
      </ul>
    </div>
  )
}

export default ShowDetailPage
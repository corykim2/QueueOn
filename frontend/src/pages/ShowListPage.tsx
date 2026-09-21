import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'

interface Show {
  id: number
  name: string
  seatCount: number
  bookingOpenAt: string
  bookable: boolean
}

interface ShowCursorResponse {
  shows: Show[]
  nextCursor: number | null
  hasNext: boolean
}

function ShowListPage() {
  const [shows, setShows] = useState<Show[]>([])

  useEffect(() => {
    fetch('http://localhost:8080/api/shows?size=10')
      .then((res) => res.json())
      .then((data: ShowCursorResponse) => {
        setShows(data.shows)
      })
      .catch((err) => console.error('불러오기 실패:', err))
  }, [])

  return (
    <div>
      <h1>공연 목록</h1>
      <ul>
        {shows.map((show) => (
          <li key={show.id}>
            <Link to={`/shows/${show.id}`}>
              {show.name} (좌석 {show.seatCount}개)
            </Link>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default ShowListPage
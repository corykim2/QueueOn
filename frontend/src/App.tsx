import { BrowserRouter, Routes, Route } from 'react-router-dom'
import ShowListPage from './pages/ShowListPage'
import ShowDetailPage from './pages/ShowDetailPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ShowListPage />} />
        {/* URL이 "/" 면 → ShowListPage 보여줘 */}
        <Route path="/shows/:showId" element={<ShowDetailPage />} />
        {/* URL이 "/shows/무언가" 면 → ShowDetailPage 보여줘 */}
      </Routes>
    </BrowserRouter>
  )
}

export default App
import { Routes, Route, Link } from 'react-router-dom'
import ProductListPage from './pages/ProductListPage.jsx'

export default function App() {
  return (
    <>
      <nav>
        <Link to="/">RetailLab</Link>
        <Link to="/products">Products</Link>
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/products" element={<ProductListPage />} />
        </Routes>
      </main>
    </>
  )
}

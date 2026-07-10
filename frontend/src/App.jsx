import { Routes, Route, Link } from 'react-router-dom'
import ProductListPage from './pages/ProductListPage.jsx'
import CartPage from './pages/CartPage.jsx'
import { useCart } from './cart/CartContext.jsx'

export default function App() {
  const { itemCount } = useCart()

  return (
    <>
      <nav>
        <Link to="/">RetailLab</Link>
        <Link to="/products">Products</Link>
        <Link to="/cart">Cart {itemCount > 0 && `(${itemCount})`}</Link>
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/products" element={<ProductListPage />} />
          <Route path="/cart" element={<CartPage />} />
        </Routes>
      </main>
    </>
  )
}

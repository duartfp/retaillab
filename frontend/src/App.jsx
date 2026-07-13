import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import ProductListPage from './pages/ProductListPage.jsx'
import CartPage from './pages/CartPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import OrderConfirmationPage from './pages/OrderConfirmationPage.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { useAuth } from './auth/AuthContext.jsx'
import { useCart } from './cart/CartContext.jsx'

export default function App() {
  const { isAuthenticated, email, logout } = useAuth()
  const { itemCount } = useCart()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/products')
  }

  return (
    <>
      <nav>
        <Link to="/">RetailLab</Link>
        <Link to="/products">Products</Link>
        <Link to="/cart">Cart {itemCount > 0 && `(${itemCount})`}</Link>

        <span className="nav-spacer" />

        {isAuthenticated ? (
          <>
            <span className="nav-user">{email}</span>
            <button className="nav-logout" onClick={handleLogout}>
              Log out
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Log in</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/products" element={<ProductListPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/cart"
            element={
              <ProtectedRoute>
                <CartPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/orders/:orderId"
            element={
              <ProtectedRoute>
                <OrderConfirmationPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </main>
    </>
  )
}

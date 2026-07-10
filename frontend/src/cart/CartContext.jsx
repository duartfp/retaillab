import { createContext, useContext, useState, useCallback, useEffect } from 'react'
import { getCart } from '../api/cart'
import { useAuth } from '../auth/AuthContext'

const CartContext = createContext(null)

export function CartProvider({ children }) {
  const { isAuthenticated } = useAuth()
  const [cart, setCart] = useState(null)

  const refreshCart = useCallback(() => {
    if (!isAuthenticated) {
      setCart(null)
      return Promise.resolve()
    }
    return getCart().then(setCart)
  }, [isAuthenticated])

  // Reload (or clear) the cart whenever login state changes.
  useEffect(() => {
    refreshCart()
  }, [refreshCart])

  const itemCount = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) ?? 0

  return (
    <CartContext.Provider value={{ cart, itemCount, refreshCart }}>
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const context = useContext(CartContext)
  if (!context) {
    throw new Error('useCart must be used within a CartProvider')
  }
  return context
}

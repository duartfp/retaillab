import { useCart } from '../cart/CartContext'
import { updateCartItem, removeCartItem } from '../api/cart'

const currencyFormatters = {}

function formatPrice(amount, currency) {
  if (amount == null) return '-'
  if (!currencyFormatters[currency]) {
    currencyFormatters[currency] = new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'USD',
    })
  }
  return currencyFormatters[currency].format(amount)
}

export default function CartPage() {
  const { cart, refreshCart } = useCart()

  if (!cart) return <p>Loading cart...</p>

  function handleQuantityChange(itemId, quantity) {
    if (quantity < 1) return
    updateCartItem(itemId, quantity).then(refreshCart)
  }

  function handleRemove(itemId) {
    removeCartItem(itemId).then(refreshCart)
  }

  if (cart.items.length === 0) {
    return <p>Your cart is empty.</p>
  }

  return (
    <div>
      <h2>Your cart</h2>
      <div className="cart-items">
        {cart.items.map((item) => (
          <div className="cart-item" key={item.id}>
            <div className="cart-item-info">
              <strong>{item.productName}</strong>
              <span className="cart-item-sku">{item.productSku}</span>
            </div>

            <div className="cart-item-quantity">
              <button
                onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                disabled={item.quantity <= 1}
              >
                -
              </button>
              <span>{item.quantity}</span>
              <button onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>
                +
              </button>
            </div>

            <span className="cart-item-subtotal">
              {formatPrice(item.subtotal, item.currency)}
            </span>

            <button className="cart-item-remove" onClick={() => handleRemove(item.id)}>
              Remove
            </button>
          </div>
        ))}
      </div>

      <div className="cart-total">
        <span>Total</span>
        <strong>{formatPrice(cart.total, cart.currency)}</strong>
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { useParams, useLocation, Link } from 'react-router-dom'
import { listOrders } from '../api/orders'

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

export default function OrderConfirmationPage() {
  const { orderId } = useParams()
  const location = useLocation()
  const [order, setOrder] = useState(location.state?.order || null)
  const [status, setStatus] = useState(order ? 'ready' : 'loading')

  // If the page was reached directly (e.g. a refresh), location.state is
  // gone, so fall back to fetching order history and finding it there.
  useEffect(() => {
    if (order) return

    listOrders()
      .then((orders) => {
        const found = orders.find((o) => String(o.id) === orderId)
        if (found) {
          setOrder(found)
          setStatus('ready')
        } else {
          setStatus('not-found')
        }
      })
      .catch(() => setStatus('error'))
  }, [order, orderId])

  if (status === 'loading') return <p>Loading order...</p>
  if (status === 'not-found') return <p>Order not found.</p>
  if (status === 'error') return <p>Could not load this order.</p>

  return (
    <div>
      <h2>Order confirmed</h2>
      <p>
        Order <strong>#{order.id}</strong> was placed successfully.
      </p>

      <div className="cart-items">
        {order.items.map((item) => (
          <div className="cart-item" key={item.productId}>
            <div className="cart-item-info">
              <strong>{item.productName}</strong>
              <span className="cart-item-sku">{item.productSku}</span>
            </div>
            <span>Qty: {item.quantity}</span>
            <span className="cart-item-subtotal">
              {formatPrice(item.subtotal, order.currency)}
            </span>
          </div>
        ))}
      </div>

      <div className="cart-total">
        <span>Total paid</span>
        <strong>{formatPrice(order.total, order.currency)}</strong>
      </div>

      <p>
        <Link to="/products">Continue shopping</Link>
      </p>
    </div>
  )
}

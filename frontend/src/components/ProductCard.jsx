const currencyFormatters = {}

function formatPrice(amount, currency) {
  if (amount == null) return 'Price unavailable'

  if (!currencyFormatters[currency]) {
    currencyFormatters[currency] = new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'USD',
    })
  }
  return currencyFormatters[currency].format(amount)
}

export default function ProductCard({ product }) {
  const outOfStock = product.stock === 0

  return (
    <div className="product-card">
      <div className="product-card-category">{product.categoryName}</div>
      <h3 className="product-card-name">{product.name}</h3>
      <p className="product-card-description">{product.description}</p>
      <div className="product-card-footer">
        <span className="product-card-price">
          {formatPrice(product.price, product.currency)}
        </span>
        <span className={`product-card-stock ${outOfStock ? 'out-of-stock' : ''}`}>
          {outOfStock ? 'Out of stock' : `${product.stock} in stock`}
        </span>
      </div>
    </div>
  )
}

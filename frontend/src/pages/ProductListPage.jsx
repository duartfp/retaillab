import { useEffect, useState } from 'react'
import { listProducts } from '../api/products'

// Minimal placeholder for now, just proves the frontend can reach the
// backend. The real catalog UI (cards, filters, pagination controls)
// is built in SCRUM-18.
export default function ProductListPage() {
  const [status, setStatus] = useState('loading')
  const [count, setCount] = useState(0)

  useEffect(() => {
    listProducts()
      .then((data) => {
        setCount(data.totalElements)
        setStatus('ready')
      })
      .catch(() => setStatus('error'))
  }, [])

  if (status === 'loading') return <p>Loading products...</p>
  if (status === 'error') {
    return <p>Could not reach the backend. Is it running on port 8080?</p>
  }

  return <p>Connected to the backend. {count} products found in the catalog.</p>
}

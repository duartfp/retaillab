import { useEffect, useState } from 'react'
import { listProducts } from '../api/products'
import { listCategories } from '../api/categories'
import ProductCard from '../components/ProductCard'

const PAGE_SIZE = 8

export default function ProductListPage() {
  const [status, setStatus] = useState('loading')
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [categoryId, setCategoryId] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // Categories only need to load once, they rarely change during a session.
  useEffect(() => {
    listCategories()
      .then(setCategories)
      .catch(() => {
        // Non-fatal, the filter dropdown just stays empty if this fails.
      })
  }, [])

  // Reload products whenever the page or the category filter changes.
  useEffect(() => {
    setStatus('loading')
    listProducts({ page, size: PAGE_SIZE, categoryId: categoryId || undefined })
      .then((data) => {
        setProducts(data.content)
        setTotalPages(data.totalPages)
        setStatus('ready')
      })
      .catch(() => setStatus('error'))
  }, [page, categoryId])

  function handleCategoryChange(event) {
    setCategoryId(event.target.value)
    setPage(0)
  }

  return (
    <div>
      <div className="catalog-toolbar">
        <h2>Products</h2>
        <select value={categoryId} onChange={handleCategoryChange}>
          <option value="">All categories</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
      </div>

      {status === 'loading' && <p>Loading products...</p>}
      {status === 'error' && (
        <p>Could not reach the backend. Is it running on port 8080?</p>
      )}

      {status === 'ready' && (
        <>
          {products.length === 0 ? (
            <p>No products found for this category.</p>
          ) : (
            <div className="product-grid">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}

          {totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
                disabled={page === 0}
              >
                Previous
              </button>
              <span>
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
                disabled={page >= totalPages - 1}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

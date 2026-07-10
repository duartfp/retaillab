import apiClient from './client'

export async function getCart() {
  const response = await apiClient.get('/cart')
  return response.data
}

export async function addToCart(productId, quantity = 1) {
  const response = await apiClient.post('/cart/items', { productId, quantity })
  return response.data
}

export async function updateCartItem(itemId, quantity) {
  const response = await apiClient.put(`/cart/items/${itemId}`, { quantity })
  return response.data
}

export async function removeCartItem(itemId) {
  const response = await apiClient.delete(`/cart/items/${itemId}`)
  return response.data
}

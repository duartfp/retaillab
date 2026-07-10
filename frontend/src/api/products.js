import apiClient from './client'

export async function listProducts({ page = 0, size = 20, categoryId } = {}) {
  const response = await apiClient.get('/products', {
    params: { page, size, categoryId },
  })
  return response.data
}

export async function getProductById(id) {
  const response = await apiClient.get(`/products/${id}`)
  return response.data
}

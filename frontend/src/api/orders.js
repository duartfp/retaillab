import apiClient from './client'

export async function checkout() {
  const response = await apiClient.post('/checkout')
  return response.data
}

export async function listOrders() {
  const response = await apiClient.get('/orders')
  return response.data
}

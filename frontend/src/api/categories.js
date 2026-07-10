import apiClient from './client'

export async function listCategories() {
  const response = await apiClient.get('/categories')
  return response.data
}

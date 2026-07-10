import apiClient from './client'

export async function loginRequest(email, password) {
  const response = await apiClient.post('/auth/login', { email, password })
  return response.data
}

export async function registerRequest(email, password) {
  const response = await apiClient.post('/auth/register', { email, password })
  return response.data
}

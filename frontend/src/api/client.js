import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attaches the JWT (if present) to every outgoing request. Requests to
// public endpoints (products, categories, auth) work fine even without
// one, since the backend only requires it on protected routes.
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('retaillab_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default apiClient

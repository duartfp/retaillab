import axios from 'axios'

// Relative base URL, resolved through the Vite dev server proxy
// (see vite.config.js) in development, and served from the same
// origin once the frontend is built and deployed behind the backend
// or a reverse proxy in later phases.
const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export default apiClient

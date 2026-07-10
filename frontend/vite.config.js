import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Proxies /api requests to the Spring Boot backend during local development,
// so the frontend can call relative paths like /api/products without
// worrying about CORS or hardcoding http://localhost:8080 everywhere.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})

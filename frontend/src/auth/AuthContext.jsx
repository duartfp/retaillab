import { createContext, useContext, useState } from 'react'
import { loginRequest, registerRequest } from '../api/auth'

const TOKEN_KEY = 'retaillab_token'
const EMAIL_KEY = 'retaillab_email'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [email, setEmail] = useState(localStorage.getItem(EMAIL_KEY))
  const [token, setToken] = useState(localStorage.getItem(TOKEN_KEY))

  function persistSession(data) {
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(EMAIL_KEY, data.email)
    setToken(data.token)
    setEmail(data.email)
  }

  async function login(loginEmail, password) {
    const data = await loginRequest(loginEmail, password)
    persistSession(data)
  }

  async function register(registerEmail, password) {
    const data = await registerRequest(registerEmail, password)
    persistSession(data)
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(EMAIL_KEY)
    setToken(null)
    setEmail(null)
  }

  const isAuthenticated = Boolean(token)

  return (
    <AuthContext.Provider value={{ email, isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

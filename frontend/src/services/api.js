import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

console.log('API_URL configurada:', API_URL)

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true
})

// Request interceptor - agregar token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    console.log('Request:', config.method.toUpperCase(), config.url, config.data);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Interceptor de respuesta para log detallado de errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // El backend respondió con un código fuera del rango 2xx
      console.error('API Error:', {
        url: error.config.url,
        method: error.config.method,
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      });
    } else if (error.request) {
      // La petición fue hecha pero no hubo respuesta
      console.error('No response from backend:', error.request);
    } else {
      // Error al configurar la petición
      console.error('Axios config error:', error.message);
    }
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('Response:', response.config.method.toUpperCase(), response.config.url, response.status)
    return response
  },
  (error) => {
    // Log persistente y detallado
    window._lastApiError = error;
    console.error('[API ERROR]', error.response?.status, error.config?.url);
    if (error.response) {
      console.error('[API ERROR RESPONSE]', error.response.data);
    }
    console.error('[API ERROR MESSAGE]', error.message);

    if (error.response?.status === 401) {
      const authStore = useAuthStore();
      authStore.logout();
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
)

export default api
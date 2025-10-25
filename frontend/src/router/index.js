import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false, hideForAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false, hideForAuth: true }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] }
  },
  {
    path: '/vendedor/dashboard',
    name: 'VendedorDashboard',
    component: () => import('../views/seller/Dashboard.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_VENDEDOR', 'ROLE_ADMIN'] }
  },
  {
    path: '/carrito',
    name: 'Carrito',
    component: () => import('../views/Carrito.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mis-pedidos',
    name: 'MisPedidos',
    component: () => import('../views/MisPedidos.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/perfil',
    name: 'Perfil',
    component: () => import('../views/Perfil.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/configuracion',
    name: 'Configuracion',
    component: () => import('../views/Configuracion.vue'),
    meta: { requiresAuth: true }
  },
  // Páginas de error
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('../views/errors/Forbidden.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/errors/NotFound.vue'),
    meta: { requiresAuth: false }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth
  const roles = to.meta.roles
  const hideForAuth = to.meta.hideForAuth

  // Si la ruta requiere autenticación
  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // Si está autenticado y trata de ir a login/register
  if (hideForAuth && authStore.isAuthenticated) {
    next('/')
    return
  }

  // Si la ruta requiere roles específicos
  if (roles && roles.length > 0) {
    const user = authStore.user
    const hasRole = user?.roles?.some(role => roles.includes(role))
    
    if (!hasRole) {
      next('/403')
      return
    }
  }

  next()
})

export default router
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";

const routes = [
  // ==================== RUTAS PÚBLICAS ====================
  {
    path: "/",
    name: "Home",
    component: () => import("../views/Home.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/productos",
    name: "Products",
    component: () => import("../views/Products.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/productos/:id",
    name: "ProductDetail",
    component: () => import("../views/ProductDetail.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
    meta: { requiresAuth: false, hideForAuth: true },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/Register.vue"),
    meta: { requiresAuth: false, hideForAuth: true },
  },

  // ==================== RUTAS PROTEGIDAS (USUARIOS) ====================
  {
    path: "/carrito",
    name: "Cart",
    component: () => import("../views/Carrito.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/mis-pedidos",
    name: "MisPedidos",
    component: () => import("../views/MisPedidos.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/perfil",
    name: "Perfil",
    component: () => import("../views/Perfil.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/configuracion",
    name: "Configuracion",
    component: () => import("../views/Configuracion.vue"),
    meta: { requiresAuth: true },
  },

  // ==================== RUTAS DE PRODUCTOS (Usuario Común) ====================
  {
    path: "/crear-producto",
    name: "CrearProducto",
    component: () => import("../views/comun/CrearProducto.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/mis-productos",
    name: "MisProductos",
    component: () => import("../views/comun/MisProductos.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/editar-producto/:id",
    name: "EditarProducto",
    component: () => import("../views/comun/EditarProducto.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/producto/:id",
    name: "DetalleProducto",
    component: () => import("../views/comun/DetalleProducto.vue"),
    meta: { requiresAuth: false }, // Público
  },
  // ==================== RUTAS DE ADMINISTRADOR ====================
  {
    path: "/admin",
    name: "admin",
    redirect: "/admin/usuarios",
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/dashboard",
    name: "AdminDashboard",
    component: () => import("../views/admin/Dashboard.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/usuarios",
    name: "admin-usuarios",
    component: () => import("../views/admin/AdminUsuarios.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/estadisticas",
    name: "AdminEstadisticas",
    component: () => import("../views/admin/AdminEstadisticas.vue"),
    meta: { requiresAuth: true, requiresRole: ["ADMIN"] },
  },

  {
    path: "/admin/reportes",
    name: "AdminReportes",
    component: () => import("@/views/admin/AdminReportes.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },

  {
    path: "/logistica",
    name: "Logistica",
    component: () => import("@/views/logistica/PedidosLogistica.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },

  // ==================== RUTAS DE MODERADOR ====================
  {
    path: "/moderador",
    name: "moderador",
    redirect: "/moderador/productos",
    meta: { requiresAuth: true, requiresModerador: true },
  },
  {
    path: "/moderador/productos",
    name: "moderador-productos",
    component: () => import("../views/moderador/ModeradorProductos.vue"),
    meta: { requiresAuth: true, requiresModerador: true },
  },

  // ==================== RUTAS DE VENDEDOR ====================
  {
    path: "/vendedor/dashboard",
    name: "VendedorDashboard",
    component: () => import("../views/seller/Dashboard.vue"),
    meta: { requiresAuth: true, requiresVendedor: true },
  },

  // ==================== PÁGINAS DE ERROR ====================
  {
    path: "/403",
    name: "Forbidden",
    component: () => import("../views/errors/Forbidden.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("../views/errors/NotFound.vue"),
    meta: { requiresAuth: false },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// ==================== NAVIGATION GUARD ====================
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.meta.requiresAuth;
  const hideForAuth = to.meta.hideForAuth;

  // Si la ruta requiere autenticación y no está autenticado
  if (requiresAuth && !authStore.isAuthenticated) {
    next("/login");
    return;
  }

  // Si está autenticado y trata de ir a login/register
  if (hideForAuth && authStore.isAuthenticated) {
    next("/");
    return;
  }

  // Verificar si requiere rol de ADMIN
  if (to.meta.requiresAdmin && !authStore.hasRole("ADMIN")) {
    next("/403");
    return;
  }

  // Verificar si requiere rol de MODERADOR (o ADMIN)
  if (
    to.meta.requiresModerador &&
    !authStore.hasAnyRole(["MODERADOR", "ADMIN"])
  ) {
    next("/403");
    return;
  }

  // Verificar si requiere rol de VENDEDOR
  if (
    to.meta.requiresVendedor &&
    !authStore.hasAnyRole(["VENDEDOR", "ADMIN"])
  ) {
    next("/403");
    return;
  }

  // Si tiene roles específicos antiguos (compatibilidad)
  if (to.meta.roles && to.meta.roles.length > 0) {
    const hasRole = authStore.hasAnyRole(to.meta.roles);

    if (!hasRole) {
      next("/403");
      return;
    }
  }

  next();
});

export default router;

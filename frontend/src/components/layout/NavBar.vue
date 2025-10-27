<template>
  <nav class="bg-white shadow-lg sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <!-- Logo -->
        <div class="flex items-center">
          <router-link
            to="/"
            class="text-2xl font-bold text-blue-600 hover:text-blue-700"
          >
            EcommerceGT
          </router-link>
        </div>

        <!-- Desktop Menu -->
        <div class="hidden md:flex items-center space-x-4">
          <!-- Si NO está logueado -->
          <template v-if="!authStore.isAuthenticated">
            <router-link to="/" class="nav-link"> Inicio </router-link>
            <router-link to="/productos" class="nav-link"> Productos </router-link>
            <router-link to="/login" class="btn-primary"> Iniciar Sesión </router-link>
            <router-link to="/register" class="btn-secondary"> Registrarse </router-link>
          </template>

          <!-- Si está logueado -->
          <template v-else>
            <!-- Enlaces comunes -->
            <router-link to="/" class="nav-link"> Inicio </router-link>
            <router-link to="/productos" class="nav-link"> Productos </router-link>

            <!-- SOLO muestra el menú del rol principal -->
            <div v-if="mainRole === 'ADMIN'" class="relative" ref="adminDropdownRef">
              <button
                @click="toggleAdminDropdown"
                class="nav-link-special admin flex items-center"
              >
                Admin
                <svg
                  class="w-4 h-4 ml-1 transition-transform"
                  :class="{ 'rotate-180': showAdminDropdown }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M19 9l-7 7-7-7"
                  ></path>
                </svg>
              </button>
              <!-- Admin Dropdown Menu -->
              <Transition name="dropdown">
                <div v-if="showAdminDropdown" class="dropdown-menu">
                  <router-link to="/admin/usuarios" class="dropdown-item" @click="closeAdminDropdown">Usuarios</router-link>
                  <router-link to="/admin/productos" class="dropdown-item" @click="closeAdminDropdown">Productos</router-link>
                  <router-link to="/admin/pedidos" class="dropdown-item" @click="closeAdminDropdown">Pedidos</router-link>
                  <router-link to="/admin/estadisticas" class="dropdown-item" @click="closeAdminDropdown">Estadisticas</router-link>
                  <router-link to="/admin/reportes" class="dropdown-item" @click="closeAdminDropdown">Reportes</router-link>
                  <router-link to="/logistica" class="dropdown-item" @click="closeAdminDropdown">Logística</router-link>
                </div>
              </Transition>
            </div>

            <router-link
              v-if="mainRole === 'MODERADOR'"
              to="/moderador/productos"
              class="nav-link-special moderador"
            >
              Moderación
            </router-link>

            <router-link
              v-if="mainRole === 'VENDEDOR'"
              to="/vendedor/dashboard"
              class="nav-link"
            >
              Mis Productos
            </router-link>

            <router-link
              v-if="mainRole === 'LOGISTICA'"
              to="/logistica"
              class="nav-link"
            >
              Logística
            </router-link>

            <!-- Enlaces para usuario COMUN -->
            <router-link
              v-if="mainRole === 'COMUN'"
              to="/mis-productos"
              class="nav-link"
            >
              Mis Productos
            </router-link>
            <router-link
              v-if="mainRole === 'COMUN'"
              to="/mis-pedidos"
              class="nav-link"
            >
              Mis Pedidos
            </router-link>

            <!-- Carrito con contador (todos los roles) -->
            <router-link to="/carrito" class="nav-link relative">
              Carrito
              <span v-if="cartStore.itemCount > 0" class="cart-badge">
                {{ cartStore.itemCount }}
              </span>
            </router-link>

            <!-- Usuario dropdown -->
            <div class="relative" ref="dropdownRef">
              <button @click="toggleDropdown" class="user-button">
                <span>{{ authStore.username }}</span>
                <svg
                  class="w-4 h-4 transition-transform"
                  :class="{ 'rotate-180': showDropdown }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M19 9l-7 7-7-7"
                  ></path>
                </svg>
              </button>

              <!-- Dropdown Menu -->
              <Transition name="dropdown">
                <div v-if="showDropdown" class="dropdown-menu">
                  <router-link to="/perfil" class="dropdown-item" @click="closeDropdown">Mi Perfil</router-link>
                  <router-link to="/configuracion" class="dropdown-item" @click="closeDropdown">Configuración</router-link>
                  <hr class="my-1 border-gray-200" />
                  <button @click="handleLogout" class="dropdown-item text-red-600 hover:bg-red-50 w-full text-left">
                    Cerrar Sesión
                  </button>
                </div>
              </Transition>
            </div>
          </template>
        </div>

        <!-- Mobile menu button -->
        <div class="md:hidden flex items-center">
          <button @click="toggleMobileMenu" class="text-gray-700 hover:text-blue-600 p-2">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path v-if="!showMobileMenu" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
              <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Mobile Menu -->
      <Transition name="mobile-menu">
        <div v-if="showMobileMenu" class="md:hidden pb-4 border-t border-gray-200 mt-2">
          <template v-if="!authStore.isAuthenticated">
            <router-link to="/" class="mobile-link" @click="closeMobileMenu">Inicio</router-link>
            <router-link to="/productos" class="mobile-link" @click="closeMobileMenu">Productos</router-link>
            <router-link to="/login" class="mobile-link" @click="closeMobileMenu">Iniciar Sesión</router-link>
            <router-link to="/register" class="mobile-link" @click="closeMobileMenu">Registrarse</router-link>
          </template>

          <template v-else>
            <div class="px-3 py-2 text-sm text-gray-500 border-b border-gray-200 mb-2">
              Hola, <strong class="text-blue-600">{{ authStore.username }}</strong>
            </div>

            <router-link to="/" class="mobile-link" @click="closeMobileMenu">Inicio</router-link>
            <router-link to="/productos" class="mobile-link" @click="closeMobileMenu">Productos</router-link>

            <!-- SOLO muestra el menú del rol principal en mobile -->
            <div v-if="mainRole === 'ADMIN'" class="border-t border-gray-200 mt-2 pt-2">
              <div class="px-3 py-2 text-xs font-semibold text-red-600">ADMINISTRACION</div>
              <router-link to="/admin/usuarios" class="mobile-link-special admin" @click="closeMobileMenu">Usuarios</router-link>
              <router-link to="/admin/productos" class="mobile-link-special admin" @click="closeMobileMenu">Productos</router-link>
              <router-link to="/admin/pedidos" class="mobile-link-special admin" @click="closeMobileMenu">Pedidos</router-link>
              <router-link to="/admin/estadisticas" class="mobile-link-special admin" @click="closeMobileMenu">Estadisticas</router-link>
              <router-link to="/admin/reportes" class="mobile-link-special admin" @click="closeMobileMenu">Reportes</router-link>
              <router-link to="/logistica" class="mobile-link-special admin" @click="closeMobileMenu">Logística</router-link>
            </div>

            <router-link
              v-if="mainRole === 'MODERADOR'"
              to="/moderador/productos"
              class="mobile-link-special moderador"
              @click="closeMobileMenu"
            >
              Moderación
            </router-link>

            <router-link
              v-if="mainRole === 'VENDEDOR'"
              to="/vendedor/dashboard"
              class="mobile-link"
              @click="closeMobileMenu"
            >
              Mis Productos
            </router-link>

            <router-link
              v-if="mainRole === 'LOGISTICA'"
              to="/logistica"
              class="mobile-link"
              @click="closeMobileMenu"
            >
              Logística
            </router-link>

            <!-- Enlaces para usuario COMUN -->
            <router-link
              v-if="mainRole === 'COMUN'"
              to="/mis-productos"
              class="mobile-link"
              @click="closeMobileMenu"
            >
              Mis Productos
            </router-link>
            <router-link
              v-if="mainRole === 'COMUN'"
              to="/mis-pedidos"
              class="mobile-link"
              @click="closeMobileMenu"
            >
              Mis Pedidos
            </router-link>

            <router-link to="/carrito" class="mobile-link" @click="closeMobileMenu">
              Carrito
              <span v-if="cartStore.itemCount > 0" class="text-red-600 font-bold ml-1">({{ cartStore.itemCount }})</span>
            </router-link>

            <router-link to="/perfil" class="mobile-link" @click="closeMobileMenu">Mi Perfil</router-link>
            <router-link to="/configuracion" class="mobile-link" @click="closeMobileMenu">Configuración</router-link>

            <button @click="handleLogout" class="mobile-link text-red-600 hover:bg-red-50 w-full text-left">Cerrar Sesión</button>
          </template>
        </div>
      </Transition>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth";
import { useCartStore } from "../../stores/cart";

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();

const showDropdown = ref(false);
const showMobileMenu = ref(false);
const dropdownRef = ref(null);

// Admin dropdown
const showAdminDropdown = ref(false);
const adminDropdownRef = ref(null);

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const closeDropdown = () => {
  showDropdown.value = false;
};

const toggleAdminDropdown = () => {
  showAdminDropdown.value = !showAdminDropdown.value;
};

const closeAdminDropdown = () => {
  showAdminDropdown.value = false;
};

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value;
};

const closeMobileMenu = () => {
  showMobileMenu.value = false;
};

const handleLogout = () => {
  authStore.logout();
  closeDropdown();
  closeMobileMenu();
  router.push("/");
};

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown();
  }
  if (
    adminDropdownRef.value &&
    !adminDropdownRef.value.contains(event.target)
  ) {
    closeAdminDropdown();
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);

  // Cargar carrito si está autenticado
  if (authStore.isAuthenticated) {
    cartStore.fetchCart();
  }
});

// Getter para el rol principal
const mainRole = computed(() => {
  // Prioridad: ADMIN > MODERADOR > VENDEDOR > LOGISTICA > COMUN
  const priority = ["ADMIN", "MODERADOR", "VENDEDOR", "LOGISTICA", "COMUN"];
  const userRoles = authStore.user?.roles || [];
  return priority.find((role) => userRoles.includes(role));
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
/* Links normales */
.nav-link {
  @apply text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium transition-colors;
}

.nav-link.router-link-active {
  @apply text-blue-600;
}

/* Links especiales (Admin/Moderador) */
.nav-link-special {
  @apply px-3 py-2 rounded-md font-semibold transition-all;
}

.nav-link-special.admin {
  @apply text-red-600 hover:text-red-700 hover:bg-red-50;
}

.nav-link-special.admin.router-link-active {
  @apply bg-red-100 text-red-700;
}

.nav-link-special.moderador {
  @apply text-yellow-600 hover:text-yellow-700 hover:bg-yellow-50;
}

.nav-link-special.moderador.router-link-active {
  @apply bg-yellow-100 text-yellow-700;
}

/* Badge del carrito */
.cart-badge {
  @apply absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-bold;
}

/* Botón de usuario */
.user-button {
  @apply flex items-center space-x-2 text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium transition-colors;
}

/* Dropdown menu */
.dropdown-menu {
  @apply absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-1 z-50 border border-gray-200;
}

.dropdown-item {
  @apply block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors;
}

/* Mobile links */
.mobile-link {
  @apply block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md transition-colors my-1;
}

.mobile-link.router-link-active {
  @apply bg-blue-50 text-blue-600;
}

.mobile-link-special {
  @apply block px-3 py-2 rounded-md font-semibold transition-colors my-1;
}

.mobile-link-special.admin {
  @apply text-red-600 hover:bg-red-50;
}

.mobile-link-special.admin.router-link-active {
  @apply bg-red-100 text-red-700;
}

.mobile-link-special.moderador {
  @apply text-yellow-600 hover:bg-yellow-50;
}

.mobile-link-special.moderador.router-link-active {
  @apply bg-yellow-100 text-yellow-700;
}

/* Botones */
.btn-primary {
  @apply bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors;
}

.btn-secondary {
  @apply bg-white text-gray-700 px-4 py-2 rounded-lg font-medium border border-gray-300 hover:bg-gray-50 transition-colors;
}

/* Transiciones */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.3s ease;
}

.mobile-menu-enter-from {
  opacity: 0;
  max-height: 0;
}

.mobile-menu-leave-to {
  opacity: 0;
  max-height: 0;
}
</style>
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
            <router-link
              to="/"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Inicio
            </router-link>
            <router-link
              to="/productos"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Productos
            </router-link>
            <router-link to="/login" class="btn-primary">
              Iniciar Sesión
            </router-link>
            <router-link to="/register" class="btn-secondary">
              Registrarse
            </router-link>
          </template>

          <!-- Si está logueado -->
          <template v-else>
            <!-- Enlaces comunes -->
            <router-link
              to="/"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Inicio
            </router-link>
            
            <router-link
              to="/productos"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Productos
            </router-link>

            <!-- Enlaces según rol -->
            <router-link
              v-if="authStore.isAdmin"
              to="/admin/dashboard"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Admin
            </router-link>

            <router-link
              v-if="authStore.isVendedor || authStore.isAdmin"
              to="/vendedor/dashboard"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Mis Productos
            </router-link>

            <router-link
              v-if="authStore.isCliente || !authStore.isAdmin"
              to="/carrito"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium relative"
            >
              Carrito
              <span
                v-if="cartCount > 0"
                class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
              >
                {{ cartCount }}
              </span>
            </router-link>

            <router-link
              to="/mis-pedidos"
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
            >
              Pedidos
            </router-link>

            <!-- Usuario dropdown -->
            <div class="relative" ref="dropdownRef">
              <button
                @click="toggleDropdown"
                class="flex items-center space-x-2 text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium"
              >
                <span>{{ authStore.username }}</span>
                <svg
                  class="w-4 h-4"
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

              <!-- Dropdown Menu con transición -->
              <Transition name="dropdown">
                <div
                  v-if="showDropdown"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50"
                >
                  <router-link
                    to="/perfil"
                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    @click="closeDropdown"
                  >
                    Mi Perfil
                  </router-link>
                  <router-link
                    to="/configuracion"
                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    @click="closeDropdown"
                  >
                    Configuración
                  </router-link>
                  <hr class="my-1" />
                  <button
                    @click="handleLogout"
                    class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 transition-colors"
                  >
                    Cerrar Sesión
                  </button>
                </div>
              </Transition>
            </div>
          </template>
        </div>

        <!-- Mobile menu button -->
        <div class="md:hidden flex items-center">
          <button
            @click="toggleMobileMenu"
            class="text-gray-700 hover:text-blue-600 p-2"
          >
            <svg
              class="w-6 h-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                v-if="!showMobileMenu"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"
              ></path>
              <path
                v-else
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              ></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Mobile Menu -->
      <div v-if="showMobileMenu" class="md:hidden pb-4">
        <template v-if="!authStore.isAuthenticated">
          <router-link
            to="/"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Inicio
          </router-link>
          <router-link
            to="/productos"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Productos
          </router-link>
          <router-link
            to="/login"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Iniciar Sesión
          </router-link>
          <router-link
            to="/register"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Registrarse
          </router-link>
        </template>

        <template v-else>
          <div class="px-3 py-2 text-sm text-gray-500">
            Hola, <strong>{{ authStore.username }}</strong>
          </div>
          <router-link
            to="/"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Inicio
          </router-link>
          <router-link
            to="/productos"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Productos
          </router-link>
          <router-link
            v-if="authStore.isAdmin"
            to="/admin/dashboard"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Admin
          </router-link>
          <router-link
            v-if="authStore.isVendedor || authStore.isAdmin"
            to="/vendedor/dashboard"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Mis Productos
          </router-link>
          <router-link
            to="/carrito"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Carrito ({{ cartCount }})
          </router-link>
          <router-link
            to="/mis-pedidos"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Pedidos
          </router-link>
          <router-link
            to="/perfil"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
            @click="closeMobileMenu"
          >
            Mi Perfil
          </router-link>
          <button
            @click="handleLogout"
            class="block w-full text-left px-3 py-2 text-red-600 hover:bg-gray-100 rounded-md"
          >
            Cerrar Sesión
          </button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const showDropdown = ref(false);
const showMobileMenu = ref(false);
const dropdownRef = ref(null);
const cartCount = ref(0);

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const closeDropdown = () => {
  showDropdown.value = false;
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
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
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
</style>
import { defineStore } from 'pinia'
import productService from '../services/productService'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    currentProduct: null,
    loading: false,
    pagination: {
      page: 0,
      size: 12,
      totalElements: 0,
      totalPages: 0
    },
    filters: {
      categoria: null,
      precioMin: null,
      precioMax: null,
      ordenar: 'fecha_desc'
    }
  }),

  getters: {
    hasProducts: (state) => state.products.length > 0,
    
    isLastPage: (state) => {
      return state.pagination.page >= state.pagination.totalPages - 1
    },

    productsByPrice: (state) => {
      return [...state.products].sort((a, b) => a.precio - b.precio)
    }
  },

  actions: {
    async fetchProducts(params = {}) {
      this.loading = true
      try {
        // Solo mostrar productos aprobados y con stock
        const queryParams = {
          page: this.pagination.page,
          size: this.pagination.size
        }
        const finalParams = { ...queryParams, ...params }
  const response = await productService.getAvailableProducts(finalParams)
        this.products = response.content || response
        if (response.pageable) {
          this.pagination = {
            page: response.number,
            size: response.size,
            totalElements: response.totalElements,
            totalPages: response.totalPages
          }
        }
        return response
      } catch (error) {
        console.error('Error al cargar productos:', error)
        toast.error('Error al cargar productos')
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchProductById(id) {
      this.loading = true
      try {
        const product = await productService.getProductById(id)
        this.currentProduct = product
        return product
      } catch (error) {
        toast.error('Producto no encontrado')
        throw error
      } finally {
        this.loading = false
      }
    },

    async searchProducts(query) {
      this.loading = true
      try {
        const response = await productService.searchProducts(query, {
          page: this.pagination.page,
          size: this.pagination.size
        })
        this.products = response.content || response
        return response
      } catch (error) {
        toast.error('Error en la búsqueda')
        throw error
      } finally {
        this.loading = false
      }
    },

    async createProduct(productData) {
      this.loading = true
      try {
        const product = await productService.createProduct(productData)
        toast.success('Producto creado exitosamente')
        return product
      } catch (error) {
        toast.error('Error al crear producto')
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateProduct(id, productData) {
      this.loading = true
      try {
        const product = await productService.updateProduct(id, productData)
        toast.success('Producto actualizado')
        return product
      } catch (error) {
        toast.error('Error al actualizar producto')
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteProduct(id) {
      this.loading = true
      try {
        await productService.deleteProduct(id)
        this.products = this.products.filter(p => p.id !== id)
        toast.success('Producto eliminado')
      } catch (error) {
        toast.error('Error al eliminar producto')
        throw error
      } finally {
        this.loading = false
      }
    },

    setFilters(filters) {
      this.filters = { ...this.filters, ...filters }
      this.pagination.page = 0
    },

    nextPage() {
      if (!this.isLastPage) {
        this.pagination.page++
        this.fetchProducts()
      }
    },

    previousPage() {
      if (this.pagination.page > 0) {
        this.pagination.page--
        this.fetchProducts()
      }
    },

    resetFilters() {
      this.filters = {
        categoria: null,
        precioMin: null,
        precioMax: null,
        ordenar: 'fecha_desc'
      }
      this.pagination.page = 0
    }
  }
})
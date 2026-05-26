import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server: {
  allowedHosts: ['kiesopfeiten.com', 'localhost'],

  proxy: {
    '/api': {
      target: 'https://api.kiesopfeiten.com',
      changeOrigin: true,
      secure: false,
    },
    '/ws': {
      target: 'http://localhost:8080', // same backend
      ws: true,           // important: enables WebSocket forwarding
      changeOrigin: true,
      secure: false,
    },
  },
},


  plugins: [
    vue(),
    vueDevTools(),
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  define: {
    global: 'window' // <-- add this line to fix SockJS "global is not defined"
  },
})

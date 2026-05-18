import { ConfigEnv, UserConfigExport } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

const pathResolve = (dir: string): any => {
  return resolve(__dirname, ".", dir)
}

const alias: Record<string, string> = {
  '@': pathResolve("src")
}

export default ({ command }: ConfigEnv): UserConfigExport => {
  return {
    base: './',
    resolve: {
      alias
    },
    define: {
      __VUE_I18N_LEGACY_API__: false,
      __VUE_I18N_FULL_INSTALL__: false,
    },
    server: {
      host: '0.0.0.0',
      port: 9090,
      open: false,
      proxy: {
        '/api': {
          target: 'http://localhost:8888/watcher',
          changeOrigin: true,
          rewrite: (path: string) => path,
        },
        '/watcher/api/knowledge': {
          target: 'http://localhost:8000',
          changeOrigin: true,
          rewrite: (path: string) => path.replace('/watcher', ''),
        },
        '/watcher': {
          target: 'http://127.0.0.1:8888',
          changeOrigin: true,
          rewrite: (path: string) => path,
        },
      }
    },
    build: {
      outDir: 'dist',
      rollupOptions: {
        output: {
          manualChunks: {
            'echarts': ['echarts']
          }
        }
      }
    },
    plugins: [
      vue(),
    ]
  };
}

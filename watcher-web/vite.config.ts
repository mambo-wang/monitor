
import { ConfigEnv, UserConfigExport } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'
import { resolve } from 'path'

const pathResolve = (dir: string): any => {
  return resolve(__dirname, ".", dir)
}

const alias: Record<string, string> = {
  '@': pathResolve("src")
}

/**
 * @description-en vite document address
 * @description-cn vite官网
 * https://vitejs.cn/config/ */
export default ({ command }: ConfigEnv): UserConfigExport => {
  const prodMock = false;
  return {
    base: './',
    resolve: {
      alias
    },
    server: {
      host: '0.0.0.0',
      port: 9090,
      open: false,
      proxy: {
        '/watcher': {
          target: 'http://10.99.224.173:8888/',
          changeOrigin: true,
          // headers: {
          //   referer: 'http://10.99.224.172:8888/',
          //   origin: 'http://10.99.224.172:8888/',
          // }
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
      viteMockServe({
        mockPath: 'mock',
        localEnabled: command === 'serve',
        prodEnabled: command !== 'serve' && prodMock,
        watchFiles: true,
        injectCode: `
          import { setupProdMockServer } from '../mockProdServer';
          setupProdMockServer();
        `,
        logger: true,
      }),
    ]
  };
}

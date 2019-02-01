// vue.config.js
module.exports = {
    // proxy all webpack dev-server requests starting with /api
    // to our Spring Boot backend (localhost:8088) using http-proxy-middleware
    // see https://cli.vuejs.org/config/#devserver-proxy
    devServer: {
        proxy: ({
            '/api': {
                target: process.env.VUE_APP_API_ENDPOINT,
                ws: true,
                changeOrigin: true
            },
            '/socket-api': {
                target: process.env.VUE_APP_API_ENDPOINT,
                ws: true,
                changeOrigin: true
            }
        })
    },
    // Change build paths to make them Maven compatible
    // see https://cli.vuejs.org/config/
    outputDir: 'target/dist',
    assetsDir: 'static',
    transpileDependencies: [
        /\bvue-awesome\b/
    ]
}

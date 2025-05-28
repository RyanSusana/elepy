const path = require('path');

module.exports = {
    lintOnSave: true,
    transpileDependencies: true,
    publicPath: process.env.NODE_ENV === 'production'
        ? '/elepy/'
        : '/',
    configureWebpack: {
        resolve: {
            alias: {
                '/images': path.resolve(__dirname, 'node_modules/uikit/src/images')
            }
        }
    }
};
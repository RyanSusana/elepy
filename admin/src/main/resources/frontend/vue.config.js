const path = require('path');

module.exports = {
    lintOnSave: true,
    filenameHashing: false,
    configureWebpack: {
        optimization: {
            splitChunks: false
        }
    }
};
const path = require('path');

module.exports = {
    lintOnSave: true,
    publicPath: process.env.NODE_ENV === 'production'
        ? '/elepy/'
        : '/'
};
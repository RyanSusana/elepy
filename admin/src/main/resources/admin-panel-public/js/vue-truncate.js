;(function () {

    var vueTruncate = {};

    vueTruncate.install = function (Vue) {

        /**
         *
         * @param {String} text
         * @param {Number} length
         * @param {String} clamp
         *
         */

        Vue.filter('truncate', function (text, length, clamp) {
            text = text || '';
            clamp = clamp || '...';
            length = length || 30;

            if (text.length <= length) return text;

            var tcText = text.slice(0, length - clamp.length);
            var last = tcText.length - 1;


            while (last > 0 && tcText[last] !== ' ' && tcText[last] !== clamp[0]) last -= 1;

            // Fix for case when text dont have any `space`
            last = last || length - clamp.length;

            tcText = tcText.slice(0, last);

            return tcText + clamp;
        });
    }

    if (typeof exports == "object") {
        module.exports = vueTruncate;
    } else if (typeof define == "function" && define.amd) {
        define([], function () {
            return vueTruncate
        });
    } else if (window.Vue) {
        window.VueTruncate = vueTruncate;
        Vue.use(VueTruncate);
    }

})()
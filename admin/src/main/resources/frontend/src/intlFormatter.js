
import MessageFormat from "messageformat";
export default class IntlFormatter {
    constructor (options = {}) {
        this.setLocale(options);
        this._caches = Object.create(null)
    }

    setLocale(locale){
        this._locale = locale || 'en-US'
        this._formatter = new MessageFormat(this._locale)
    }

    interpolate (message, values) {
        let fn = this._caches[message]
        if (!fn) {
            fn = this._formatter.compile(message, this._locale)
            this._caches[message] = fn
        }
        return [fn(values)]
    }
}
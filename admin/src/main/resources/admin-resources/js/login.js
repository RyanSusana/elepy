const app = new Vue({
    delimiters: ['((', '))'],
    el: "#login-app",
    data: {

        username: "",
        password: "",
        keepLoggedIn: false,
        initialUser: false
    },
    created: function () {
        this.initialUser = this.getQueryParams("initialUser") != null
    },
    methods: {

        getQueryParams: function (qs) {
            let url_string = window.location.href;
            let url = new URL(url_string);
            return url.searchParams.get(qs)
        },

        login: function () {

            let selfReference = this;
            axios.post("/elepy-token-login", null, {
                params: {
                    "username": selfReference.username,
                    "password": selfReference.password,
                    "keepLoggedIn": selfReference.keepLoggedIn
                }
            }).then(function (response) {
                window.location = "/admin"

            })
                .catch(function (error) {
                    console.log(error.response.status);
                    UIkit.notification(error.response.data.message, {status: 'danger'})

                });
        }
    }
})
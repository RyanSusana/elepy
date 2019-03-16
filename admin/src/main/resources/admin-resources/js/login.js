const app = new Vue({
    delimiters: ['((', '))'],
    el: "#login-app",
    data: {

        username: "",
        password: ""
    },
    methods: {
        login: function () {

            let selfReference = this;
            axios.post("/elepy-login", null, {
                params: {
                    "username": selfReference.username,
                    "password": selfReference.password
                }
            }).then(function (response) {
                //console.log(response.data)
                //ref.modelData = response.data
                console.log("redirecting");
                window.location = response.data

            })
                .catch(function (error) {
                    console.log(error.response.status);
                    UIkit.notification(error.response.data.message, {status: 'danger'})

                });
        }
    }
})
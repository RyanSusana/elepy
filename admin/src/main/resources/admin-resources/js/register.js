const app = new Vue({
    delimiters: ['((', '))'],
    el: "#register-app",
    data: {

        confirmPassword: "",
        user: {

            username: "",
            password: ""
        }
    },
    methods: {


        register: function () {
            if (this.confirmPassword === this.user.password) {
                let selfReference = this;
                axios.post("/users", selfReference.user).then(function (response) {

                    if (response.status === 200) {
                        window.location = "/elepy-login?initialUser=true"
                    }
                })
                    .catch(function (error) {
                        console.log(error.response.status);
                        UIkit.notification(error.response.data.message, {status: 'danger'})

                    });
            } else {
                UIkit.notification("Passwords do not match!", {status: 'danger'})
            }

        }
    }
})
$(document).on('submit', '#login-form', function (event) {


    event.preventDefault();
    axios({
        method: 'post',
        url: "/login",
        data: $("#login-form").serialize()
    }).then(function (response) {
        //console.log(response.data)
        //ref.modelData = response.data
        console.log("redirecting")
        window.location = response.data

    })
        .catch(function (error) {
            console.log(error.response.status)
            UIkit.notification(error.response.data, {status: 'danger'})

        });
})
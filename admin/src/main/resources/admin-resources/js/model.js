Vue.component('Elepy', ElepyVue);
const app = new Vue({
    el: '#app',
    delimiters: ['((', '))'],
    components: {"Elepy": ElepyVue},
    data() {
        return {selectedModel: null};
    },
    methods: {
        getModel() {
            axios.get("/config").then(response => {
                this.selectedModel = response.data.filter(function (model) {
                    return model.slug === document.getElementById('slug').innerText
                })[0];
            });
        }
    },
    created() {
        this.getModel();
    }
});

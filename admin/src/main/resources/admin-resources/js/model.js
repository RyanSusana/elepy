Vue.component('Elepy', ElepyVue);
const app = new Vue({
    el: '#app',
    delimiters: ['((', '))'],
    components: {"Elepy": ElepyVue},
    data() {
        return {selectedModel: null, url: "http://localhost:8989"};
    },
    methods: {
        getModel() {
            axios.get(this.url + "/config").then(response => {
                this.selectedModel = response.data[0];
                this.selectedModel.url = this.url;
            });
        }
    },
    created() {
        this.getModel();
    }
});

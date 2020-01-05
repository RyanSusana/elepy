
<template>
    <div class="uk-flex uk-height-1-1">
        <div class="uk-width-1-1 uk-height-1-1">
            <Elepy :model="selectedModel" v-if="selectedModel!=null"/>
        </div>
    </div>
</template>

<style lang="scss">
    html,
    body {
        height: 100%;
    }
</style>

<script>
    /* eslint-disable */
    import axios from "axios/index";
    import Elepy from "./components/ElepyDefault";
    import ElepySingle from "./components/ElepySingle";
    import ElepyFile from "./components/ElepyFile";
    import Utils from "./utils.mjs";

    export default {
        components: {Elepy, ElepySingle, ElepyFile},
        data() {
            return {selectedModel: null};
        },
        methods: {
            getModel() {
                axios.get(Utils.url + "/config").then(response => {
                    this.selectedModel = response.data[2];
                    this.selectedModel.url = this.url;
                });
            }
        },
        created() {
            Utils.url = "http://localhost:1337";
            this.getModel();
        }
    };
</script>

<style  lang="scss">

    @import "../scss/main.scss";
</style>

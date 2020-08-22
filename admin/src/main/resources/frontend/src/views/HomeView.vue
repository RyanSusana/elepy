<template>
    <div class="uk-container uk-container-expand uk-margin-large-top">
        <h1>Home</h1>


        <div class="models">
            <ModelCard v-for="model in allModels" :model="model"/>
        </div>

    </div>
</template>

<script>
    import {mapGetters, mapState} from "vuex"
    import ModelCard from "./ModelCard";

    export default {
        name: "HomeView",
        components: {ModelCard},
        computed: {
            ...mapState(['allModels']),
            ...mapGetters(['canExecute'])
        }
    }
</script>

<style lang="scss" scoped>

    @import "scss/main";

    .models {
        display: grid;

        grid-template-columns: 1fr 1fr 1fr;

        grid-gap: 15px;

    }
</style>
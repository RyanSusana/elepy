<template>
    <div class="uk-container uk-container-expand uk-margin-large-top">
        <h1>{{ $t('elepy.ui.home') }}</h1>


        <div class="models">
            <ModelCard v-for="model in allModels" :key="model.name" :model="model"/>
        </div>

    </div>
</template>

<script>
    import { useMainStore } from "@/stores/main";
    import { storeToRefs } from 'pinia'
    import ModelCard from "./ModelCard";

    export default {
        name: "HomeView",
        components: {ModelCard},
        setup() {
            const store = useMainStore()
            const { allModels } = storeToRefs(store)
            
            return {
                allModels,
                canExecute: store.canExecute
            }
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
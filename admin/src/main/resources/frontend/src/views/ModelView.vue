<template>
    <div v-if="model!=null">

        <Elepy v-if="model.view === 'default'" :model="model"></Elepy>
        <ElepyFile v-else-if="model.view === 'file'" :model="model"></ElepyFile>
        <ElepySingle v-else-if="model.view === 'single'" :singleMode="true" :model="model"></ElepySingle>
        <div v-else class="uk-width-1-1 uk-height-1-1">
            <iframe :src="model.view"></iframe>
        </div>
    </div>
</template>

<script>
    import Elepy from "../components/ElepyDefault";
    import ElepySingle from "../components/ElepySingle";
    import ElepyFile from "../components/ElepyFile";
    import {mapGetters} from "vuex";

    export default {
        name: "ModelView",
        components: {Elepy, ElepyFile, ElepySingle},
        computed: {
            ...mapGetters(['getModel']),
            model() {
                return this.getModel(this.$route.params.modelPath);
            }
        },
    }
</script>

<style scoped>

</style>
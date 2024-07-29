<template>
    <Elepy :model="model" v-if="model!=null" adding=true></Elepy>
</template>

<script>
import ElepySingle from "../components/ElepySingle";

import {mapGetters} from "vuex";

export default {
        name: "AddRecordView",

        computed: {
            ...mapGetters(['getModel']),
            model(){
                return this.getModel(this.$route.params.modelPath);
            }
        },
        components: {Elepy: ElepySingle},
    }
</script>

<style scoped>

</style>
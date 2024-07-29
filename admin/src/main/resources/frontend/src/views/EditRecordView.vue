<template>

    <Elepy :model="model" v-if="model!=null" :record-id="this.$route.params.recordId"></Elepy>
</template>

<script>
import Elepy from "../components/ElepySingle";

import {mapGetters} from "vuex";

export default {
        name: "EditRecordView",

        computed: {
            ...mapGetters(['getModel']),
            model(){
                return this.getModel(this.$route.params.modelPath);
            }
        },
        components: {Elepy},
    }
</script>

<style scoped>

</style>
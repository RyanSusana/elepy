<template>
    <button @click="doAction"
            :action="actionName"
            class="uk-button"><span
            v-if="!busy"><slot></slot></span><span uk-spinner v-else></span></button>
</template>

<script>
    export default {
        name: "ActionButton",
        props: ['action', 'actionName'],

        data() {
            return {busy: false}
        },

        methods: {
            async doAction() {

                if (this.busy) {
                    return;
                }
                this.busy = true;

                try{
                    await this.action();
                }finally {
                    this.busy = false;
                }

            }
        }
    }
</script>

<style scoped>

</style>
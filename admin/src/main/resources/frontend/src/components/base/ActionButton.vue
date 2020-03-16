<template>
    <button @click="doAction"
            class="uk-button uk-button-primary"><span
            v-if="!busy"><slot></slot></span><span uk-spinner v-else></span></button>
</template>

<script>
    export default {
        name: "ActionButton",
        props: ['action'],

        data() {
            return {busy: false}
        },

        methods: {
            async doAction() {

                if (this.busy) {
                    return;
                }
                this.busy = true;

                await this.action();
                this.busy = false;
            }
        }
    }
</script>

<style scoped>

</style>
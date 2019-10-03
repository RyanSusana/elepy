<template>
    <input
            :value="value"
            @input="handleInput"
            class="uk-input"
            type="number"
            v-bind:disabled="field.generated == true || (field.editable == false && (content !== null))"

    >
</template>

<script>
    export default {
        props: ["field", "value"],
        data() {
            return {
                content: this.value
            };
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e.target.value);
            }
        }
    };
</script>

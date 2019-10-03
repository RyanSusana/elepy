<template>
    <div>
        <input
                :value="value"
                @input="handleInput"
                class="uk-input"
                type="password"
                v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
        >
    </div>
</template>

<script>
    import "vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css";

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

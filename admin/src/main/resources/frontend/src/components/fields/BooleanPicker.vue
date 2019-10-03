<template>
    <div class="pad">
        <input :checked="value" @input="handleInput" class="uk-checkbox" type="checkbox">
        <label for>{{prettyName}}</label>
    </div>
</template>

<style lang="scss" scoped>
    .uk-checkbox {
        margin-right: 5px;
        margin-left: 10px;
    }

    .pad {
        padding: 5px;
    }
</style>

<script>
    export default {
        props: ["value", "field"],
        data() {
            return {
                content: this.value
            };
        },
        computed: {
            prettyName() {
                if (this.value === true) {
                    return this.field.trueValue;
                } else {
                    return this.field.falseValue;
                }
            }
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e.target.checked);
            }
        }
    };
</script>
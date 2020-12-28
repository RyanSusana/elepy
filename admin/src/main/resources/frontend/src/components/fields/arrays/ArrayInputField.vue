<template>
    <div class="uk-flex">
        <div class="width-10 uk-flex uk-flex-center uk-flex-middle">
            <a class uk-icon="menu"></a>
        </div>

        <div class="uk-inline width-90">
            <a @click="remove" class="uk-form-icon" uk-icon="close"></a>
            <input
                    :id="field.name+'-'+index"
                    :value="val"
                    @input="handleInput"
                    class="uk-input"
                    v-on:keydown.backspace="backspace"
                    v-on:keyup.enter="nextField"
            >
        </div>
    </div>
</template>
<script>
    export default {
        name: 'ArrayInputField',
        props: {
            field: {},
            index: {},
            removeIndex: {},
            updateIndexWithEvent: {},
            val: {}
        },
        methods: {
            handleInput(e) {
                this.$emit('input', e)
            },
            nextField() {
                this.$emit('next');
            },
            remove() {
                this.$emit('remove')
            },
            backspace(e) {
                if (!this.val) {
                    e.preventDefault();

                    this.remove();
                    if (this.index !== 0) {
                        this.$nextTick(() => {

                            const el = document
                                .getElementById(this.field.name + "-" + (this.index - 1));
                            el && el.focus();
                        });
                    }
                }
            },
        }
    }
</script>
<style lang="scss" scoped>

    .width-10 {
        width: calc(7%);
    }

    .width-90 {
        width: calc(93%);

    }

    [uk-icon="menu"] {
        cursor: grab;
    }

</style>
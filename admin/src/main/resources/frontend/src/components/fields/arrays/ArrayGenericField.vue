<template>
    <div class v-else>
        <div class="uk-flex">
            <div
                    class="width-10 uk-margin-small-bottom uk-flex uk-flex-middle uk-flex-around uk-flex-column"
                    v-if="isBigField"
            >
                <div class="uk-flex uk-flex-column"><a @click="moveUp" class uk-icon="arrow-up"></a>
                    <a class uk-icon="menu"></a>
                    <a @click="moveDown" class uk-icon="arrow-down"></a></div>


                <a
                        @click="remove"
                        class="uk-icon-button uk-button-danger uk-icon-button-small"
                        uk-icon="close"
                ></a>
            </div>

            <div class="width-10 uk-flex-middle uk-flex-center uk-flex" v-else>
                <a
                        @click="remove"
                        class="uk-margin-right uk-icon-button uk-button-danger uk-icon-button-small"
                        uk-icon="close"
                ></a>

                <a class uk-icon="menu"></a>
            </div>
            <div class="width-90 uk-flex uk-flex-middle">
                <div class="uk-width-1-1">
                    <GenericField
                            :field="field"
                            :fieldType="field.arrayType"
                            :value="val"
                            :violations="violations"
                            @input="handleInput"
                    />
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        name: 'ArrayGenericField',
        props: {
            field: {},
            index: {},
            val: {},
            violations: {}
        },
        components: {
            GenericField: () => import("../GenericField.vue"),
        },
        computed: {
            isBigField() {
                let smallFields = ["DATE", "ENUM", "BOOLEAN"];
                return (
                    !this.isNumberOrTextfield && !smallFields.includes(this.field.arrayType)
                );
            }
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
            moveUp() {
                this.$emit('moveUp')
            },
            moveDown() {
                this.$emit('moveDown')
            }
        }
    }
</script>
<style lang="scss" scoped>
    .uk-icon-button-small {
        width: 26px;
        height: 26px;
    }

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
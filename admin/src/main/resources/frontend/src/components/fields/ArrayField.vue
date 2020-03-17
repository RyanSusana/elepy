<template>

    <ReferenceField
            v-if="field.arrayType === 'REFERENCE'"
            :field="field"
            :value="value"
            @input="handleInput"
            :multiple="true"
    >

    </ReferenceField>
    <div v-else class="uk-background-muted">
        <div class="uk-margin-small-bottom">
            <a @click="addField()" class="uk-inline uk-padding-small uk-width-1-1">
                <a
                        action="edit"
                        class="uk-icon-button uk-button-primary uk-icon-button-small uk-margin-small-right"
                        uk-icon="plus"
                ></a>
                <a class="uk-link-reset uk-text-small">Add Field</a>
            </a>
        </div>

        <draggable :options="{animation:250}" @change="handleInput" v-model="values">
            <div
                    :key="index"
                    class
                    v-bind:class="{ 'array-item': isBigField  }"
                    v-for="(val, index) in values"
            >
                <div class="uk-flex" v-if="isNumberOrTextfield">
                    <div class="width-10 uk-flex uk-flex-center uk-flex-middle">
                        <a class uk-icon="menu"></a>
                    </div>

                    <div class="uk-inline width-90">
                        <a @click="removeIndex(index)" class="uk-form-icon" uk-icon="close"></a>
                        <input
                                :id="field.name+'-'+index"
                                :value="val"
                                @input="updateIndexWithEvent(index,$event)"
                                class="uk-input"
                                v-on:keydown.backspace="backspace(index, $event)"
                                v-on:keyup.enter="nextField(index)"
                        >
                    </div>
                </div>
                <div class v-else>
                    <div class="uk-flex">
                        <div
                                class="width-10 uk-margin-small-bottom uk-flex uk-flex-middle uk-flex-around uk-flex-column"
                                v-if="isBigField"
                        >
                            <div class="uk-flex uk-flex-column"><a @click="moveUp(index)" class uk-icon="arrow-up"></a>
                                <a class uk-icon="menu"></a>
                                <a @click="moveDown(index)" class uk-icon="arrow-down"></a></div>


                            <a
                                    @click="removeIndex(index)"
                                    class="uk-icon-button uk-button-danger uk-icon-button-small"
                                    uk-icon="close"
                            ></a>
                        </div>

                        <div class="width-10 uk-flex-middle uk-flex-center uk-flex" v-else>
                            <a
                                    @click="removeIndex(index)"
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
                                        @input="updateIndexWithValue(index, $event)"
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </draggable>
    </div>
</template>

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

    .array-item {
        border: 1px solid rgba($color: #777, $alpha: 0.5);

        &:nth-child(2n) + &:not(:last-child) {
            border-top: none;
            border-bottom: none;
        }
    }
</style>

<script>
    import draggable from "vuedraggable";
    import ReferenceField from "./ReferenceField";

    export default {
        props: ["field", "value"],
        name: "ArrayField",
        data() {
            return {
                values: this.value
            };
        },

        watch: {
            value: function (val) {
                this.values = val;
            }
        },
        components: {
            ReferenceField,
            GenericField: () => import("./GenericField.vue"),
            draggable
        },

        computed: {
            isNumberOrTextfield() {
                return (
                    this.field.arrayType === "NUMBER" ||
                    (this.field.arrayType === "INPUT")
                );
            },
            isBigField() {
                var smallFields = ["DATE", "ENUM", "BOOLEAN"];
                return (
                    !this.isNumberOrTextfield && !smallFields.includes(this.field.arrayType)
                );
            }
        },
        methods: {
            handleInput(e) {
                if (e == null) {
                    this.$emit("input", this.values);
                    this.$forceUpdate();
                } else {

                    this.$emit("input", e);
                    this.$forceUpdate();
                }
            },

            removeIndex(index) {
                this.values.splice(index, 1);
                this.handleInput();
            },

            nextField(index) {
                if (index + 1 === this.values.length) {
                    this.addField();

                    if (this.isNumberOrTextfield) {
                        this.$nextTick(() => {
                            document
                                .getElementById(this.field.name + "-" + (index + 1))
                                .focus();
                        });
                    }
                }
            },
            backspace(index, e) {
                if (this.values[index] == "") {
                    e.preventDefault();

                    this.removeIndex(index);
                    if (index !== 0 && this.isNumberOrTextfield) {
                        this.$nextTick(() => {
                            document
                                .getElementById(this.field.name + "-" + (index - 1))
                                .focus();
                        });
                    }
                }
            },

            updateIndexWithEvent(index, e) {
                this.updateIndexWithValue(index, e.target.value);
            },

            updateIndexWithValue(index, val) {
                this.values[index] = val;
                this.handleInput();
            },

            moveUp(index) {
                if (index > 0) {
                    let item = this.values[index];
                    let above = this.values[index - 1];

                    this.values[index - 1] = item;
                    this.values[index] = above;

                    this.handleInput();
                }
            },
            moveDown(index) {
                if (index < this.values.length - 1) {
                    let item = this.values[index];
                    let below = this.values[index + 1];

                    this.values[index + 1] = item;
                    this.values[index] = below;
                    this.handleInput();
                }
            },

            addField() {
                if (this.field.arrayType === "OBJECT") {
                    this.values.push({});
                } else {
                    this.values.push("");
                }
                this.handleInput();

                if (this.isNumberOrTextfield) {
                    this.$nextTick(() => {
                        document
                            .getElementById(this.field.name + "-" + (this.values.length - 1))
                            .focus();
                    });
                }
            }
        }
    };
</script>


<template>

    <ReferenceField
            v-if="field.arrayType === 'REFERENCE'"
            :field="field"
            v-model="values"
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
                <a class="uk-link-reset uk-text-small">{{ $t('elepy.ui.actions.add') }}</a>
            </a>
        </div>

        <draggable :options="{animation:250}" @change="handleInput" v-model="values">
            <div
                    :key="index"
                    class
                    v-bind:class="{ 'array-item': isBigField  }"
                    v-for="(val, index) in values"
            >
                <ArrayInputField v-if="isNumberOrTextfield"
                                 :field="field"
                                 :index="index"
                                 :val="val"
                                 :violations="getViolationsFor(index)"
                                 @next="nextField(index)"
                                 @remove="removeIndex(index)"
                                 @input="updateIndexWithEvent(index,$event)"
                />
                <ArrayGenericField v-else
                                   :field="field"
                                   :index="index"
                                   :val="val"
                                   :violations="getViolationsFor(index)"
                                   @moveDown="moveDown(index)"
                                   @moveUp="moveUp(index)"
                                   @remove="removeIndex(index)"
                                   @input="updateIndexWithValue(index, $event)"
                />
            </div>
        </draggable>
    </div>
</template>

<style lang="scss" scoped>
    .uk-icon-button-small {
        width: 26px;
        height: 26px;
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
    import ArrayInputField from "./arrays/ArrayInputField";
    import ArrayGenericField from "./arrays/ArrayGenericField";

    export default {
        props: ["field", "value", "violations"],
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
            ArrayGenericField,
            ArrayInputField,
            ReferenceField,
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
                let smallFields = ["DATE", "ENUM", "BOOLEAN"];
                return (
                    !this.isNumberOrTextfield && !smallFields.includes(this.field.arrayType)
                );
            }

        },
        methods: {
            getViolationsFor(idx) {
                return (this.violations || [])
                    // Makes sure that the filtered violation does not belong to the array itself
                    .filter(violation => violation.propertyPath)
                    .filter(violation => {
                        const violationIndex = violation.propertyPath.match(/\[(\d+)]/)[1];
                        return idx === Number(violationIndex);
                    }).map(
                        violation => {
                            return {
                                ...violation,
                                propertyPath: violation.propertyPath.replace(/\[\d+]/, '').replace(/^\./, "")
                            }
                        }
                    )

            },
            handleInput() {
                this.$emit("input", this.values || []);
                this.$forceUpdate();
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
        },

        mounted() {
            this.handleInput();
        }
    };
</script>


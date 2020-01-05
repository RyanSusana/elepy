<template>
    <div :property="field.name">
        <div class="uk-text-emphasis uk-text-bold" v-if="trueFieldType !== 'OBJECT'">{{field.prettyName}}</div>
        <DatePicker
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'DATE'"
        />

        <NumberField
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'NUMBER'"
        />

        <TextField
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'TEXT' && field.textType === 'TEXTFIELD'"
        />

        <PasswordField
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'TEXT' && field.textType === 'PASSWORD'"
        />

        <EnumPicker :field="field" :value="value" @input="handleInput" v-if="trueFieldType === 'ENUM'"/>

        <BooleanPicker
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'BOOLEAN'"
        />

        <TextArea
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'TEXT' && field.textType === 'TEXTAREA'"
        />

        <MarkdownEditor
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'TEXT' && field.textType === 'MARKDOWN'"
        />

        <HtmlEditor
                :field="field"
                :value="value"
                @input="handleInput"
                v-if="trueFieldType === 'TEXT' && field.textType === 'HTML'"
        />

        <ArrayField
                :field="field"
                :value="value || []"
                @input="handleInput"
                v-if="trueFieldType === 'ARRAY'"
        />

        <FileField
                :field="field"
                :value="value || ''"
                @input="handleInput"
                v-if="trueFieldType === 'FILE_REFERENCE'"
        />

        <ul class="background-darken" uk-accordion="multiple: true" v-if="trueFieldType === 'OBJECT'">
            <li>
                <a class="uk-accordion-title uk-padding-small background-darken" href="#">{{objectName}}</a>
                <div class="uk-accordion-content uk-padding-small">
                    <ObjectField :model="field" :value="value == null ? {} : value" @input="handleInput"/>

                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>

    .uk-accordion {
        list-style: none;

        .uk-accordion-title::before {
            float: left;
            margin-left: 0;
            margin-right: 10px;
        }

    }

    .uk-accordion {
        margin-bottom: 0 !important;
    }

    .background-darken {
        background-color: rgba($color: #000000, $alpha: 0.04);
    }

    .inner-object {
        padding: 15px;
    }
</style>


<script>
    import DatePicker from "./DatePicker";
    import NumberField from "./NumberField";
    import TextField from "./TextField";
    import PasswordField from "./PasswordField";
    import TextArea from "./TextArea";
    import MarkdownEditor from "./MarkdownEditor.vue";
    import HtmlEditor from "./HtmlEditor.vue";
    import EnumPicker from "./EnumPicker.vue";
    import BooleanPicker from "./BooleanPicker.vue";
    import ArrayField from "./ArrayField.vue";
    import FileField from "./FileField.vue";

    export default {
        props: ["value", "fieldType", "field"],
        name: "GenericField",
        components: {
            NumberField,
            TextField,
            TextArea,
            MarkdownEditor,
            DatePicker,
            HtmlEditor,
            EnumPicker,
            BooleanPicker,
            ArrayField,
            PasswordField,
            FileField,
            ObjectField: () => import("./ObjectField.vue")
        },

        computed: {
            trueFieldType() {
                return this.fieldType || this.field.type;
            },
            objectName: function () {
                if (this.field.type === 'ARRAY') {
                    let featuredProperty = this.field.featuredProperty;
                    return this.value[featuredProperty] || 'One of the ' + this.field.prettyName;

                }

                return this.field.prettyName
            }
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e);
            }
        }
    };
</script>
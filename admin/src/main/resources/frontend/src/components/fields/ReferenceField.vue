<template>
    <div class="uk-flex uk-flex-middle">
        <router-link :to="`${schema.path}/edit/${selected[schema.idProperty]}`"
                     target="_blank"
                     uk-icon="forward"
                     v-if="!multiple && selected!=null"
                     class="uk-margin-small-right"
                     :uk-tooltip="`title: Click to navigate to ${selected[schema.featuredProperty]}; pos: bottom`"
        ></router-link>

        <multiselect
                v-model="selected"
                :id="_uid"
                :trackBy="schema.idProperty"
                :label="schema.idProperty"
                placeholder="Type to search"
                open-direction="bottom"
                :options="foundRecords"
                :multiple="multiple"
                :searchable="true"
                :loading="isLoading"
                :internal-search="false"
                :options-limit="300"
                :limit="10"
                :limit-text="limitText"
                :hideSelected="field.required"
                :max-height="600"
                :show-no-results="false"

                @input="handleInput"
                @search-change="findRecords"
        >

            <template
                    slot="tag"
                    slot-scope="{ option, remove }">

                <span class="multiselect__tag">
                    <router-link
                            :uk-tooltip="`title: Click to navigate to ${option[schema.featuredProperty] }; pos: bottom-center`"
                            :to="`${schema.path}/edit/${option[schema.idProperty]}`"
                            target="_blank"
                            class="tag-link">{{ option[schema.featuredProperty] }}</router-link> <i
                        aria-hidden="true" tabindex="1" @click="remove(option)"
                        class="multiselect__tag-icon"></i></span>

            </template>

            <template slot="clear" slot-scope="props">
                <div class="multiselect__clear" v-if="foundRecords.length"
                     @mousedown.prevent.stop="clearAll(props.search)">
                </div>
            </template>

            <template slot="singleLabel" slot-scope="props">
                    <span class="option__desc" v-if="selected!=null">

                    <span
                    >{{ selected[schema.featuredProperty]  }}</span>
                    <span class="option__small uk-margin-small-left uk-text-muted uk-text-small" v-if="hasFeaturedField">({{ selected[schema.idProperty]  }})</span>

                </span>

            </template>

            <template slot="option" slot-scope="props">
                <div class="option__desc ">
                    <span class="option__title">{{ props.option[schema.featuredProperty]  }}</span>
                    <span class="option__small uk-margin-small-left uk-text-small" v-if="hasFeaturedField">({{ props.option[schema.idProperty]  }})</span>
                </div>
            </template>

            <span slot="noResult">Oops! No elements found. Consider changing the search query.</span>
        </multiselect>
    </div>
</template>

<script>

    import Multiselect from 'vue-multiselect'
    import SchemaClient from "../../elepyClient"

    import {throttle} from "lodash";

    export default {
        name: "ReferenceField",
        props: ["value", "field", "multiple"],
        components: {
            Multiselect
        },

        watch: {
            value: {
                immediate: true,
                handler: function (o, n) {
                    if (typeof o !== 'undefined' && !this.initialLoaded) {
                        this.sync()
                    }
                }
            }
        },
        data() {
            return {
                isLoading: false,
                initialLoaded: false,
                selected: null,
                foundRecords: [],

            }
        },
        computed: {
            schema() {
                return this.field.referenceSchema
            },

            hasFeaturedField(){
                return this.schema.featuredProperty !== this.schema.idProperty
            }
        },
        methods: {
            handleInput(e) {
                if (this.multiple === true) {
                    let x = e.map(i => i[this.schema.idProperty])

                    this.$emit("input", x);
                } else {
                    this.$emit("input", e === null ? null : e[this.schema.idProperty]);

                }
            },

            limitText(count) {
                return `and ${count} other ${this.schema.name}`
            },

            findRecords: throttle(async function (query) {

                this.isLoading = true

                this.foundRecords = (await new SchemaClient(this.schema).search(query).catch(err => console.log(err.response))).values;

                this.isLoading = false;
            }, 500),
            clearAll() {
                this.handleInput([])
            },

            async sync() {
                let schemaClient = new SchemaClient(this.schema);

                if (this.value !== undefined && this.value != null) {
                    if (this.multiple) {
                        if (this.value.length !== 0) {
                            this.isLoading = true;

                            this.selected = (await schemaClient.getByIds(this.value)).values;
                            this.isLoading = false;
                            this.initialLoaded = true;
                        }
                    } else {
                        this.isLoading = true;
                        this.selected = await schemaClient.getById(this.value);
                        this.isLoading = false;
                        this.initialLoaded = true;
                    }
                }
            }
        },
        mounted() {
            this.findRecords('');
        }
    }
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style lang="scss">

    @import "../../../scss/main.scss";

    .tag-link {
        color: white;

        &:hover {
            color: white;
        }
    }

    .multiselect__tags {
        border-radius: 0;
    }

    .multiselect__tag, .multiselect__option.multiselect__option--highlight, .multiselect__option.multiselect__option--highlight::after {
        background-color: var(--primary-color);
    }

    .multiselect__option--selected.multiselect__option--highlight {
        background: $global-danger-background;

        &::after {

            background-color: $global-danger-background;
        }
    }

    .multiselect__tag-icon:hover, .multiselect__tag-icon:focus {
        background-color: var(--primary-hover-color);
    }


</style>
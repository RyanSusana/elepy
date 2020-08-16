<template>

    <div class="query-bar">
        <div id="search" class="uk-flex-1">

            <div class="bar" tabindex="0" :class="{'focus': focus}">
                <div class="badges">
                    <span class="uk-badge" v-for="filter in this.filters">{{clamp(filter, 5) }}</span>
                    <input
                            class="input"
                            id="search-input"
                            placeholder="Search..."
                            type="text"
                            v-on:keypress.enter="addFilter"
                            v-on:keydown.delete="backspace"
                            v-on:focusin="focus=true"
                            v-on:focusout="focus=false"
                            v-model="input"
                            autocomplete="off"
                    >
                </div>


            </div>

        </div>

    </div>

</template>

<script>


    export default {
        name: "QueryBar",
        props: ["value", "model"],
        watch: {
            filters: function (oldValue, newValue) {
                this.handleInput()
            }
        },
        computed: {
            trimmedInput() {
                return this.input.trim()
            },

            selectedPropertyName() {
                let propName = this.trimmedInput.match(this.propertyRegex)
                if (!propName) return null
                let inputWithoutProperty = this.trimmedInput.replace(this.propertyRegex, "")

                if (inputWithoutProperty.match(/^[a-zA-Z0-9]/)) {
                    return null
                }

                return propName[0]
            },
            selectedProperty() {
                return this.getProperty(this.selectedPropertyName)
            },

            selectedFilterName() {

                let inputWithoutProperty = this.trimmedInput.substring(this.selectedPropertyName.length, this.trimmedInput.length).trim()

                let filter = inputWithoutProperty.match(this.filterRegex)
                if (!filter) {
                    return null
                }
                return filter[0]
            },

            isSearchQuery() {
                return this.propertyRegex == null || this.filterRegex == null
            },
            filterValue() {
                let withoutProp = this.trimmedInput.replace(this.propertyRegex, "")
                return withoutProp.trim().replace(this.filterRegex, "").trim()
            },

            propertyNames() {
                return this.model.properties.map(p => p.name).sort((p1, p2) => p2.length - p1.length)
            },

            propertyRegex() {
                return new RegExp("^(" + this.propertyNames.join("|") + ")")
            },

            filterRegex() {
                if (!this.selectedProperty) {
                    return null
                }
                return new RegExp("^(" + this.selectedProperty.availableFilters.map(f => f.filter).join("|") + ")")
            }

        },

        methods: {

            handleInput() {
                this.$emit("input", this.filters.join(" and "));
            },

            clamp(text, length, clamp) {

                if (this.filters.length < 2) {
                    return text
                }
                clamp = clamp || '...';

                let content = text
                return content.length > length ? content.slice(0, length) + clamp : content;
            },

            getProperty(propName) {
                return this.model.properties.filter(p => p.name === propName)[0]
            },


            getPropertyFilter(propName, filterName) {
                let property = this.getProperty(propName);
                if (!property) {
                    return null;
                }
                return property.availableFilters.filter(f => f.name === filterName)[0]
            },

            backspace() {
                if (this.input.length === 0) {
                    this.filters.pop();
                }
            },
            addFilter() {
                if (this.input.trim().length === 0) {
                    return
                }


                if (this.isSearchQuery) {
                    this.filters.push("'" + this.input + "'")
                } else {
                    this.filters.push(this.selectedPropertyName + " " + this.selectedFilterName + " '" + this.filterValue + "'")
                }
                this.input = ""
            }
        },
        data() {
            return {
                input: "",
                filters: [],
                focus: false
            }
        },


    }
</script>

<style lang="scss" scoped>

    @import "scss/main";

    .bar {
        display: flex;
        min-height: 40px;
        border: 1px solid $form-border;
        align-items: center;

        outline: none;
        padding: 5px;

        &.focus {

            border-color: $form-focus-border;
        }

        .input {

            height: 100%;

            flex-shrink: 0;
            outline: none;
            border: 0;
            margin: 0 0 0 5px;
            padding: 0;
        }

        .badges {
            margin-left: 5px;
        }
    }

</style>
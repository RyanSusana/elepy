<template>
    <div class="uk-position-relative" id="search">
        <div class="uk-inline">
            <a
                    :class="{'with-filters':queryFilter.filters.length >0 }"
                    @click="filterFocused = !filterFocused"
                    class="uk-form-icon uk-form-icon-flip uk-link-muted"
                    href="#"
                    id="filter-button"
            >
                <span uk-icon="icon: settings"></span>
                <span class="uk-badge" v-if="queryFilter.filters.length >0">{{queryFilter.filters.length}}</span>
            </a>
            <span class="uk-form-icon" href="#" uk-icon="icon: search"></span>
            <input
                    :class="{focused: focused, 'filter-focused': filterFocused}"
                    @blur="focused = false"
                    @focus="focused = true"
                    class="uk-input"
                    id="search-input"
                    placeholder="Search..."
                    type="text"
                    v-model="queryFilter.query"
                    v-on:keyup="triggerRefresh()"
            >
        </div>

        <div id="filter-dropdown" v-show="filterFocused">
            <div class="uk-padding-small">
                <div class="uk-flex uk-flex-center uk-flex-middle">
                    <a
                            @click="addFilter"
                            class="uk-icon-button uk-button-primary uk-margin-small-right uk-color-light"
                            id="add-filter-button"
                            uk-icon="plus"
                    ></a>
                    <h5 class="uk-margin-remove">Filter</h5>
                </div>

                <div class="uk-margin-small-top">
                    <div
                            :id="'filter-'+index"
                            :key="filter.id"
                            class="uk-flex uk-flex-middle"
                            v-for="(filter, index) in queryFilter.filters"
                    >
                        <select @change="triggerRefresh()" class="uk-select" id v-model="filter.field">
                            <option
                                    :key="field.name"
                                    :value="field"
                                    v-for="field in filterableFields"
                            >{{field.prettyName}}
                            </option>
                        </select>

                        <select @change="triggerRefresh()" class="uk-select" v-model="filter.type">
                            <option
                                    :key="filterType.param"
                                    :value="filterType"
                                    v-for="filterType in filter.field.availableFilters"
                            >{{filterType.prettyName}}
                            </option>
                        </select>
                        <input @input="triggerRefresh()" class="uk-input" type="text" v-model="filter.value">

                        <a
                                class="uk-margin-small-left close-button"
                                uk-icon="close"
                                v-on:click="removeFilter(filter.id)"
                        ></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss">
    #search-input {
        border-radius: 200px;
        width: 15em;
        transition: all 0.2s;

        &.filter-focused,
        &.focused {
            width: 20em;
        }
    }

    #filter-button {
        right: 0;
        text-decoration: none;
        cursor: pointer;

        & > *:hover {
            text-decoration: none;
        }

        & > .uk-badge {
            margin-left: 5px;
        }

        &.with-filters {
            right: 10px;
        }
    }

    #filter-dropdown {
        min-width: 500px;
        max-height: 90vh;
        overflow-y: scroll;
        position: absolute;
        top: 4em;
        left: -50%;

        box-shadow: 0 2px 5px rgba(#000, 0.25);
        background: #fefefe;
        z-index: 5;
    }
</style>

<script>
    import Icons from "uikit/dist/js/uikit-icons";
    import UIkit from "uikit";

    import vClickOutside from "v-click-outside";

    UIkit.use(Icons);

    export default {
        props: ["model", "value"],

        directives: {
            clickOutside: vClickOutside.directive
        },
        computed: {
            filterableFields() {
                return this.model.properties.filter(f => f.type !== "OBJECT");
            },
            searchFocused() {
                return this.filterFocused || this.focused;
            }
        },
        data() {
            return {
                focused: false,
                filterFocused: false,
                queryFilter: {
                    query: "",
                    filters: []
                }
            };
        },
        methods: {
            triggerRefresh() {
                if (this.timer) {
                    clearTimeout(this.timer);
                    this.timer = null;
                }
                this.timer = setTimeout(() => {
                    this.update();
                }, 200);
            },
            removeFilter(id) {
                this.queryFilter.filters = this.queryFilter.filters.filter(
                    f => f.id !== id
                );

                this.update();
            },
            addFilter() {
                let filter = {
                    id: this.generateUniqueId(),
                    field: this.model.idProperty,
                    type: {param: "equals", prettyName: "equals"},
                    value: ""
                };

                this.queryFilter.filters.push(filter);
                this.update();
            },
            generateUniqueId() {
                return (
                    Math.random()
                        .toString(36)
                        .substring(2) + new Date().getTime().toString(36)
                );
            },

            getIdField() {
                return this.model.properties[0];
            },

            getFiltersQueryString() {
                return "q=" + this.queryFilter.query + "&" + this.queryFilter.filters
                    .filter(f => f != null && f.value.length > 0)
                    .map(f => this.toFilterQuery(f))
                    .join("&");
            },
            toFilterQuery(filter) {
                return filter.field.name + "_" + filter.type.filter + "=" + filter.value;
            },
            update() {
                this.$emit("input", this.getFiltersQueryString());
                this.$emit("change");
            }
        },
        created() {
            let self = this;
            window.addEventListener("click", e => {
                // close dropdown when clicked outside

                if (!self.$el.contains(e.target) && e.target.tagName !== "svg") {
                    self.filterFocused = false;
                }
            });
        }
    };
</script>
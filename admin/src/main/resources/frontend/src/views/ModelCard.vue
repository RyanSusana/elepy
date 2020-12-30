<template>
    <div class="uk-background-muted uk-card uk-border-rounded uk-padding ">
        <h3>{{model.name}}</h3>

        <p>{{model.description}}</p>
        <ul class="uk-list">

            <li>
                <router-link :to="model.path">{{ $t('elepy.ui.actions.view') }} {{model.name}}</router-link>
            </li>
            <li v-if="model.view!== 'single'">
                <router-link :to="model.path +'/add'">{{ $t('elepy.ui.actions.addTo', [model.name]) }}</router-link>
            </li>

        </ul>
    </div>
</template>
<script>
    export default {
        name: 'ModelCard',
        props: ["model"]
    }
</script>

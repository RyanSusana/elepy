<template>
    <div uk-lightbox>
        <a :data-caption="caption" :href="src" class="uk-button uk-button-default">
            <img :alt="alt" :data-src="src" :height="height" :width="width" uk-img>
        </a>
    </div>
</template>

<script>
    export default {
        props: ["height", "width", "src", "alt", "caption"]
    };
</script>

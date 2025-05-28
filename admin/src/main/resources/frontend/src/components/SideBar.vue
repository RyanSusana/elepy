<template>
  <div id="sidebar" class="">
    <div class="sidebar-content">
      <router-link to="/" class="sidebar-header uk-flex uk-flex-center">
        <img class="banner-image" :src="logo" alt="logo">
      </router-link>
      <div class="sidebar-main uk-light">
        <h4 class="sidebar-heading">{{ $t('elepy.ui.resources') }}</h4>
        <ul class="sidebar-list">
          <li v-for="model in this.allModels" v-if="canExecute(model.defaultActions.find)" :key="model.name">
            <router-link :to="model.path"><span uk-icon="folder"></span> {{ model.name }}</router-link>
          </li>

        </ul>
        <h4 class="sidebar-heading">General</h4>
        <ul class="sidebar-list">

          <li>
            <router-link to="/"><span uk-icon="home"></span> Admin Home</router-link>
          </li>
          <li><a @click="logOut"><span uk-icon="sign-out"></span> Log out</a></li>

        </ul>
      </div>
      <div class="sidebar-footer">
        <LanguageSelect/>
      </div>

    </div>


  </div>
</template>
<script>
import { useMainStore } from "@/stores/main";
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import LanguageSelect from "@/components/LanguageSelect";

export default {
  name: 'SideBar',
  components: {LanguageSelect},
  setup() {
    const store = useMainStore()
    const router = useRouter()
    const { allModels, locale } = storeToRefs(store)
    
    return {
      allModels,
      locale,
      logo: store.logo,
      canExecute: store.canExecute,
      logOut: () => {
        store.logOut();
        store.init().then(_ => router.replace('/login'))
      }
    }
  }
}
</script>
<style lang="scss">
@import "scss/main.scss";


#sidebar {
  box-sizing: border-box;
  background-color: var(--sidebar-bg);
  font-size: 0.9em;
  top: 0;

  .sidebar-content {
    display: grid;

    height: 100%;
    box-sizing: border-box;
    grid-template-rows: 128px 1fr min-content;

    .sidebar-footer {
      padding: 12px;
    }

    .sidebar-main {
      padding: 20px;
    }

    .sidebar-header .banner-image {
      object-fit: cover;
      width: 100%;
    }
  }


  .sidebar-heading {
    color: var(--text-muted-color-dark);
    font-size: 1em;
    text-transform: uppercase;
    margin-bottom: 5px;
    /*font-weight: bold;*/
  }

  .sidebar-list {

    padding: 0;
    margin: 0;

    li {
      margin-bottom: 3px;
      margin-left: 10px;
      list-style: none;

      & > a {
        font-size: 1em;
        margin-bottom: 0;
        color: var(--text-muted-color);

        &:hover {
          text-decoration: none;
        }

        &.router-link-exact-active {
          color: white;
        }
      }
    }

  }
}

</style>

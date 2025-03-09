import vuetify from '@/plugins/vuetify'
import Vue from 'vue'
import App from './App.vue'
import router from './router/router.js'
import store from './store'


let scripts_loaded = 0

const scripts = [
    "variants.js",
    "maternalVariants.js",
    "deletions.js",
    "defaultSettings.js"
];

window.loadScripts = async function() {

    scripts_loaded = 0

    return new Promise((resolve, reject) => {
        scripts.forEach(src => {
            const script = document.createElement("script");
            script.src = src;
            script.async = true;
            script.onload = () => {
                ++scripts_loaded
                if (scripts_loaded == scripts.length) {
                    console.log("All scripts loaded")
                    resolve(scripts_loaded)
                }
      }
            document.body.appendChild(script);
        });
    })

}

new Vue({
    store: store.store,
    router,
    vuetify,
    render: h => h(App),
}).$mount('#app')

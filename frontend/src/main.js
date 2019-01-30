import Vue from 'vue'
import App from './App.vue'
import router from './router'
import BootstrapVue from 'bootstrap-vue'
import Icon from 'vue-awesome/components/Icon'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'vue-awesome/icons'

Vue.config.productionTip = false

// Bootstrap
Vue.use(BootstrapVue)
// Vue-awesome icons
Vue.component('icon', Icon)

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')



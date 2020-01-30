import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import axios from 'axios'

axios.defaults.baseURL = 'http://140.238.20.170:8000'
//axios.defaults.baseURL = 'http://localhost:8000'
axios.defaults.headers.post["Content-Type"] = "application/json";
axios.defaults.headers.get["Content-Type"] = "application/json";

const local = axios.create({
  baseURL: ''
});

Vue.prototype.$local = local;

Vue.config.productionTip = false;

Vue.prototype.$EventBus = new Vue();
Vue.prototype.$axios = axios;
Vue.prototype.$store = store;

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')
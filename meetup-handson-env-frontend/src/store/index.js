import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    access_token: sessionStorage.getItem("access_token"),
    refresh_token: sessionStorage.getItem("refresh_token"),
    expires_in: sessionStorage.getItem("expires_in")
  },
  mutations: {
    loginToken(state) {
      /* eslint-disable no-console */
      //console.log("loginToken....");
      state.access_token = sessionStorage.getItem("access_token");
      state.refresh_token = sessionStorage.getItem("refresh_token");
      state.expires_in = sessionStorage.getItem("expires_in");
    },
    delToken(state) {
      /* eslint-disable no-console */
      //console.log("delToken....");
      sessionStorage.removeItem('access_token');
      sessionStorage.removeItem('refresh_token');
      sessionStorage.removeItem('expires_in');
      if (state.access_token)
        state.access_token = null;
      if (state.refresh_token)
        state.refresh_token = null;
      if (state.expires_in)
        state.expires_in = null;
    }
  },
  actions: {},
  modules: {}
})
import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    access_token: sessionStorage.getItem("access_token"),
    refresh_token: sessionStorage.getItem("refresh_token"),
    expires_in: "",
    expiration: sessionStorage.getItem("expiration"),
    intervalId: null
  },
  getters: {
    getTokenExpiresIn(state) {
      return state.expires_in;
    },
    getIntervalId(state) {
      return state.intervalId;
    }
  },
  mutations: {
    loginToken(state, data) {
      /* eslint-disable no-console */
      //console.log("loginToken....");

      sessionStorage.setItem("access_token", data.access_token);
      sessionStorage.setItem("refresh_token", data.refresh_token);
      sessionStorage.setItem("expiration", data.expiration);

      state.access_token = data.access_token;
      state.refresh_token = data.refresh_token;
      state.expires_in = data.expires_in;
    },
    delToken(state) {
      /* eslint-disable no-console */
      //console.log("delToken....");
      sessionStorage.removeItem('access_token');
      sessionStorage.removeItem('refresh_token');
      sessionStorage.removeItem('expiration');
      if (state.access_token)
        state.access_token = null;
      if (state.refresh_token)
        state.refresh_token = null;
      if (state.expiration)
        state.expiration = null;
    },
    setExpiration(state, expiration) {
      state.expiration = expiration;
    },
    setJwtExpiresIn(state, expires_in) {
      state.expires_in = expires_in;
    },
    setIntervalId(state, intervalId) {
      state.intervalId = intervalId;
    }
  },
  actions: {
    getAllClaimsFromToken(context, username) {

      /* eslint-disable no-console */
      //console.log(context.state.access_token);
      axios({
          method: "post",
          url: "/api/auth/claims",
          data: {
            "username": username
          },
          headers: {
            "X-Authorization": "Bearer " + context.state.access_token,
            "Content-Type": "application/json"
          }
        })
        .then(result => {
          //console.log(result.data.exp - Math.floor(new Date().getTime() / 1000));

          sessionStorage.setItem("expiration", result.data.exp);
          context.commit("setExpiration", result.data.exp);
        })
        .catch(error => {
          console.log(error);
        });
    },
    setJwtExpiresIn(context) {
      var intervalId = setInterval(() => {
        context.commit('setJwtExpiresIn', context.state.expiration - Math.floor(new Date().getTime() / 1000))
      }, 1000)

      context.commit("setIntervalId", intervalId);
    },
    destroySetJwtExpiresInScheduler(context) {
      clearInterval(context.getters.getIntervalId);
    }
  },
  modules: {}
})
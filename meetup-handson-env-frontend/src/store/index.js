import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)
//JSON.parse(sessionStorage.getItem("claims"))
export default new Vuex.Store({
  state: {
    access_token: sessionStorage.getItem("access_token"),
    refresh_token: sessionStorage.getItem("refresh_token"),
    expires_in: "",
    claims: JSON.parse(sessionStorage.getItem("claims")),
    intervalId: null
  },
  getters: {
    getTokenExpiresIn(state) {
      return state.expires_in;
    },
    getIntervalId(state) {
      return state.intervalId;
    },
    getClaims(state) {
      return state.claims;
    }
  },
  mutations: {
    loginToken(state, data) {
      /* eslint-disable no-console */
      //console.log(data);

      sessionStorage.setItem("access_token", data.access_token);
      sessionStorage.setItem("refresh_token", data.refresh_token);

      state.access_token = data.access_token;
      state.refresh_token = data.refresh_token;
      state.expires_in = data.expires_in;
    },
    delToken(state) {
      /* eslint-disable no-console */
      //console.log("delToken....");
      sessionStorage.removeItem('access_token');
      sessionStorage.removeItem('refresh_token');
      sessionStorage.removeItem('claims');
      if (state.access_token)
        state.access_token = null;
      if (state.refresh_token)
        state.expires_in = null;
      if (state.claims)
        state.claims = null;
    },
    setAllClaims(state, claims) {
      state.claims = claims;
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
          sessionStorage.setItem("claims", JSON.stringify(result.data));
          context.commit("setAllClaims", result.data);
          context.dispatch("setJwtExpiresIn");
        })
        .catch(error => {
          console.log(error);
        });
    },
    setJwtExpiresIn(context) {
      if (context.state.claims != null && context.state.claims.exp != undefined) {
        var intervalId = setInterval(() => {
          let expires_in = (context.state.claims.exp - Math.floor(new Date().getTime() / 1000) < 0 ? "" : context.state.claims.exp - Math.floor(new Date().getTime() / 1000));
          context.commit('setJwtExpiresIn', expires_in)
        }, 1000)

        context.commit("setIntervalId", intervalId);
      }

    },
    destroySetJwtExpiresInScheduler(context) {
      clearInterval(context.getters.getIntervalId);
    }
  },
  modules: {}
})
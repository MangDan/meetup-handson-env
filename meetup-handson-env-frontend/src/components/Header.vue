<template>
  <v-app-bar app elevate-on-scroll dark color="red lighten-1">
    <div class="d-flex align-center">
      <a href="/">
        <v-img
          src="/assets/img/oracle_logo1.png"
          class="shrink mr-2"
          transition="scale-transition"
          width="30px"
          contain
        ></v-img>
      </a>
      <v-toolbar-title>Oracle Korea Developer Meetup</v-toolbar-title>
    </div>
    <v-spacer></v-spacer>
    <v-icon left @click="loginAdminForm">mdi-login</v-icon>
    <v-icon left v-if="this.adminMode" @click="logoutDialog = true">mdi-logout</v-icon>

    <v-img
      src="/assets/img/oracle_logo3.png"
      class="shrink mr-2"
      transition="scale-transition"
      width="120px"
      contain
    ></v-img>
    <!-- Admin Login Dialog -->
    <v-dialog v-model="loginAdminDialog" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">{{this.adminMode ? "You are Already Logged in" : "Admin Login"}}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field label="Email*" v-model="adminUsername" :rules="emailRules" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field label="Password*" type="password" v-model="adminPassword" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
          <small>*indicates required field</small>
        </v-card-text>
        <v-alert type="error" v-model="adminAlert" dismissible>
          <h3 class="title">Oops!</h3>
          <div v-text="adminAlertErrmsg"></div>
        </v-alert>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn
            color="blue darken-1"
            text
            :disabled="this.adminMode ? true : false"
            @click="loginAdmin"
          >Login</v-btn>
          <v-btn color="blue darken-1" text @click="registerAdmin">Register</v-btn>
          <v-btn color="blue darken-1" text @click="loginAdminDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- RefreshToken Dialog -->
    <v-dialog v-model="refreshTokenDialog" persistent max-width="500px">
      <v-card>
        <v-card-title>Refresh Token</v-card-title>
        <v-card-text>
          <v-alert
            border="right"
            colored-border
            type="error"
            elevation="2"
            v-html="refreshTokenDialogMsg"
          ></v-alert>
        </v-card-text>
        <v-alert type="error" v-model="refreshTokenAlert" dismissible>
          <h3 class="title">Oops!</h3>
          <div v-text="refreshTokenAlertErrmsg"></div>
        </v-alert>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="refreshToken">Refresh</v-btn>
          <v-btn color="primary" text @click="refreshTokenDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- RefreshToken Dialog -->
    <!-- Logout Dialog -->
    <v-dialog v-model="logoutDialog" max-width="500px">
      <v-card>
        <v-card-title>Logout</v-card-title>
        <v-card-text>로그아웃 하시겠습니까?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="logoutAdmin">Logout</v-btn>
          <v-btn color="primary" text @click="logoutDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- Logout Dialog -->
  </v-app-bar>
</template>
<script>
import { mapState } from "vuex";

export default {
  name: "Header",
  props: ["role", "passwd"],
  data: () => ({
    loginAdminDialog: false,
    adminAlert: false,
    adminAlertErrmsg: "",
    adminUsername: "",
    adminPassword: "",
    refreshTokenDialog: false,
    refreshTokenDialogMsg: "",
    refreshTokenAlert: false,
    refreshTokenAlertErrmsg: "",
    logoutDialog: false,
    emailRules: [
      v => !!v || "E-mail is required",
      v => /.+@.+\..+/.test(v) || "E-mail must be valid"
    ]
  }),
  created() {
    // this.$store.watch(
    //   (state, getters) => getters.getTokenExpiresIn,
    //   (newValue, oldValue) => {
    //     /* eslint-disable no-console */
    //     //console.log(`Updating from ${oldValue} to ${newValue}`); // eslint-disable-line no-unused-vars
    //     // Do whatever makes sense now
    //     if (newValue === 2500) {
    //       console.log(newValue);
    //     }
    //   }
    // );
    // vuex store watch....
    var _this = this;
    _this.$store.watch(function(state) {
      /* eslint-disable no-console */
      if (
        state.expires_in === 600 || //10m
        state.expires_in === 300 || //5m
        state.expires_in === 60 //1m
      ) {
        _this.refreshTokenDialogMsg =
          "<font color='red'>" +
          state.expires_in +
          "</font>초 후에 토큰이 만료됩니다. 토큰을 재발급 받으시겠습니까?";

        if (_this.refreshTokenDialog == false) _this.refreshTokenDialog = true;
      }
    });
  },
  mounted() {
    this.$store.dispatch("setJwtExpiresIn");
  },
  methods: {
    loginAdminForm() {
      this.adminAlert = false;
      this.adminAlertErrmsg = "";
      this.loginAdminDialog = true;
    },
    registerAdmin() {
      let data = {
        username: this.adminUsername,
        password: this.adminPassword
      };

      this.$axios({
        method: "post",
        url: "/api/auth/register",
        data: data,
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          if (result.data.errorCode != "00") {
            // 토큰 발행 오류
            this.adminAlert = true;
            this.adminAlertErrmsg = result.errorMessage;
          } else {
            // 성공

            this.$store.commit("loginToken", result.data);
            this.$store.dispatch("getAllClaimsFromToken", this.adminUsername);
            //this.$store.dispatch("setJwtExpiresIn");

            this.loginAdminDialog = false; //close this dialog
          }
        })
        .catch(error => {
          this.adminAlert = true;
          this.adminAlertErrmsg = error.response.data.message;
        });
    },
    loginAdmin() {
      let data = {
        username: this.adminUsername,
        password: this.adminPassword
      };

      this.$axios({
        method: "post",
        url: "/api/auth/login",
        data: data,
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          if (result.data.errorCode != "00") {
            // 토큰 발행 오류
            this.adminAlert = true;
            this.adminAlertErrmsg = result.errorMessage;
          } else {
            // 성공

            this.$store.commit("loginToken", result.data);
            this.$store.dispatch("getAllClaimsFromToken", this.adminUsername);
            //this.$store.dispatch("setJwtExpiresIn");

            this.loginAdminDialog = false; //close this dialog
          }
        })
        .catch(error => {
          this.adminAlert = true;
          this.adminAlertErrmsg = error.response.data.message;
        });
    },
    logoutAdmin() {
      this.$store.commit("delToken");
      this.$store.dispatch("destroySetJwtExpiresInScheduler");

      this.logoutDialog = false; //close this dialog
    },
    refreshToken() {
      let data = {
        username: this.$store.state.claims.sub,
        refresh_token: this.$store.state.refresh_token
      };

      this.$axios({
        method: "post",
        url: "/api/auth/token",
        data: data,
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          if (result.data.errorCode != "00") {
            // 토큰 발행 오류
            this.refreshTokenAlert = true;
            this.refreshTokenAlertErrmsg = result.errorMessage;
          } else {
            // 성공

            this.$store.commit("loginToken", result.data);
            this.$store.dispatch(
              "getAllClaimsFromToken",
              this.$store.state.claims.sub
            );

            this.refreshTokenDialog = false; //close this dialog
          }
        })
        .catch(error => {
          this.refreshTokenAlert = true;
          this.refreshTokenAlertErrmsg = error.response.data.message;
        });
    }
  },
  computed: {
    ...mapState(["expires_in"]),
    adminMode() {
      //console.log("adminMode");
      if (this.$store.state.expires_in > 0) return true;
      else return false;
    }
  } //이와 같이 매핑을 하면  {{ expires_in }} 이렇게 사용할 수 있다.
};
</script>



  
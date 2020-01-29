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
    <v-icon left @click="loginAdminForm">mdi-login-variant</v-icon>
    <v-icon left @click="logoutAdmin">mdi-logout-variant</v-icon>
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
          <span class="headline">Admin Login</span>
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

          <v-btn color="blue darken-1" text @click="loginAdmin">Login</v-btn>
          <v-btn color="blue darken-1" text @click="registerAdmin">Register</v-btn>
          <v-btn color="blue darken-1" text @click="loginAdminDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- Admin Login Dialog -->
  </v-app-bar>
</template>
<script>
export default {
  name: "Header",
  props: ["role", "passwd"],
  data: () => ({
    loginAdminDialog: false,
    adminAlert: false,
    adminAlertErrmsg: "",
    adminUsername: "meetuporclkr@oracle.com",
    adminPassword: "welcome1",
    emailRules: [
      v => !!v || "E-mail is required",
      v => /.+@.+\..+/.test(v) || "E-mail must be valid"
    ]
  }),
  created() {
    /* eslint-disable no-console */
    this.setAdminMode();
    //console.log("access_code : " + $store.state.access_code);
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
          /* eslint-disable no-console */
          console.log(result);
          this.loginAdminDialog = false; //close this dialog
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
            sessionStorage.setItem("access_token", result.data.access_token);
            sessionStorage.setItem("refresh_token", result.data.refresh_token);
            sessionStorage.setItem("expires_in", result.data.expires_in);

            this.$store.commit("loginToken");
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

      this.adminMode = false;
      this.loginAdminDialog = false; //close this dialog
    },
    setAdminMode() {
      if (
        sessionStorage.getItem("access_token") == "" ||
        sessionStorage.getItem("access_token") == null
      )
        this.adminMode = false;
      else this.adminMode = true;
    }
  }
};
</script>



  
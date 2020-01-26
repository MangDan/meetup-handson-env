<template v-slot:top>
  <v-card flat height="540px" tile>
    <v-toolbar flat color="white">
      <v-toolbar-title>실습 환경 정보</v-toolbar-title>
      <!--
      <v-divider class="mx-4" inset vertical></v-divider>
      -->
      <v-spacer></v-spacer>
      <v-btn icon>
        <v-icon
          :color="(listType == 'card' ? 'red' : '')"
          @click="changeListType('card')"
        >mdi-view-module</v-icon>
      </v-btn>
      <v-btn icon>
        <v-icon
          :color="(listType == 'list' ? 'red' : '')"
          @click="changeListType('list')"
        >mdi-format-list-bulleted</v-icon>
      </v-btn>
    </v-toolbar>
    <EnvDetail ref="envDetail" v-if="showEnv" />
    <v-container v-else fluid>
      <v-row>
        <v-col cols="12">
          <v-card class="d-flex pa-2 justify-center" style="box-shadow: none;">
            <v-form style="width:70%" ref="form" lazy-validation>
              <v-text-field
                v-model="username"
                :counter="10"
                :rules="nameRules"
                label="Name"
                required
              ></v-text-field>
              <v-spacer></v-spacer>
              <v-text-field v-model="email" :rules="emailRules" label="E-mail" required></v-text-field>
              <v-alert type="error" v-model="alert" dismissible>
                <h3 class="title">Oops!</h3>
                <div v-text="errmsg"></div>
              </v-alert>
              <div class="text-right">
                <v-btn rounded color="primary" @click="searchEnvFromForm">Search</v-btn>
                <v-btn
                  v-if="role == 'admin' && passwd == 'welcome1' && num != 0"
                  class="ml-4"
                  rounded
                  dark
                  color="red"
                  @click="alertDialog = true"
                >Reset</v-btn>
              </div>
            </v-form>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-dialog v-model="alertDialog" persistent max-width="290">
      <v-card>
        <v-card-title class="headline">Reset?</v-card-title>
        <v-card-text>
          <b>{{username}}</b>님의 신청을 초기화할까요??
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="alertDialog = false">Disagree</v-btn>
          <v-btn color="green darken-1" text @click="reset">Reset</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>
  
<script>
import EnvDetail from "../components/EnvDetail";

export default {
  name: "MeetupHandsonEnvDetail",
  props: ["role", "passwd"],
  components: {
    EnvDetail
  },
  data: () => ({
    alertDialog: false,
    showEnv: false,
    listType: "card",
    valid: true,
    alert: false,
    errmsg: "",
    num: 0,
    username: "",
    email: "",
    nameRules: [
      v => !!v || "Name is required",
      v => (v && v.length <= 10) || "Name must be less than 10 characters"
    ],
    emailRules: [
      v => !!v || "E-mail is required",
      v => /.+@.+\..+/.test(v) || "E-mail must be valid"
    ]
  }),
  mounted() {
    this.$EventBus.$on(
      "searchEnv",
      function(env) {
        /* eslint-disable no-console */
        this.searchEnv(env);
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );

    this.$EventBus.$on(
      "goEnvForm",
      function(env) {
        /* eslint-disable no-console */
        this.goEnvForm(env);
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );
  },
  computed: {},
  methods: {
    searchEnv(num) {
      this.alert = false;

      this.$axios({
        method: "get",
        url: "/api/v1/meetup/env/" + num,
        headers: { "Content-Type": "application/json" }
      })
        .then(result => {
          this.showEnv = true;

          setTimeout(
            function() {
              if (this.$refs.envDetail) this.$refs.envDetail.env = result.data;
              if (this.$refs.envDetail)
                this.$refs.envDetail.listType = this.listType;
            }.bind(this),
            300
          );
        })
        .catch(error => {
          console.log(error);
        });
    },
    searchEnvFromForm() {
      if (this.$refs.form.validate()) {
        this.alert = false;

        this.$axios({
          method: "get",
          url:
            "/api/v1/meetup/env?username=" +
            this.username +
            "&email=" +
            this.email,
          headers: { "Content-Type": "application/json" }
        })
          .then(result => {
            if (result.data == "") {
              this.alert = true;
              this.errmsg =
                this.username + "님이 신청한 정보가 없습니다. 다시 신청하세요.";
            } else {
              this.showEnv = true;

              setTimeout(
                function() {
                  if (this.$refs.envDetail)
                    this.$refs.envDetail.env = result.data;
                  if (this.$refs.envDetail)
                    this.$refs.envDetail.listType = this.listType;
                }.bind(this),
                300
              );

              //this.$EventBus.$emit("displayEnv", this.listType, result.data);
            }
          })
          .catch(error => {
            console.log(error);
          });
      }
    },
    goEnvForm(env) {
      this.showEnv = false;
      this.alert = false;
      this.num = env.num;
      this.username = env.username;
      this.email = "";
    },
    changeListType(listType) {
      if (this.$refs.envDetail) this.$refs.envDetail.listType = listType;

      this.listType = listType;
    },
    reset() {
      let data = {
        num: this.num,
        username: "",
        email: ""
      };

      this.$axios({
        method: "post",
        url: "/api/v1/meetup/env/reset",
        data: data,
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          console.log(result.data);

          this.$EventBus.$emit("initialize", result.data);

          //this.$parent.initialize();
        })
        .catch(error => {
          console.log(error);
        });

      this.alertDialog = false;
    }
  }
};
</script>
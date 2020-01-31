<template>
  <v-data-table
    :headers="headers"
    :items="envs"
    :items-per-page="itemsPerPage"
    sort-by="calories"
    class="elevation-1"
    height="418px"
  >
    <template v-slot:top>
      <v-toolbar flat color="white">
        <v-toolbar-title>실습 환경 목록</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-icon @click="initialize">refresh</v-icon>
        <v-dialog v-model="dialog" max-width="500px">
          <v-card ref="form">
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12" sm="12" md="12">
                    <v-text-field
                      ref="username"
                      v-model="editedItem.username"
                      label="Name"
                      counter
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="12" md="12">
                    <v-text-field
                      ref="email"
                      v-model="editedItem.email"
                      :rules="[rules.required, rules.email]"
                      label="Email"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="12" sm="12" md="12">
                    <v-alert dense outlined type="error" v-model="alert" dismissible>
                      <h3 class="title">Oops!</h3>
                      <div v-text="errmsg1"></div>
                      <v-divider class="my-4 info" style="opacity: 0.22"></v-divider>

                      <v-row align="center" no-gutters>
                        <v-col class="grow" v-text="errmsg2"></v-col>
                      </v-row>
                    </v-alert>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="submit">Submit</v-btn>
              <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
    </template>
    <template v-slot:item.username="{ item }">
      <v-chip
        :color="getColor(item.username)"
        dark
        no-data-text="No data..."
        @click="editItem(item)"
      >{{ item.username == null ? '등록' : item.username }}</v-chip>
    </template>
  </v-data-table>
</template>

<script>
export default {
  name: "MeetupHandsonEnvList",
  props: ["role", "passwd"],
  data: () => ({
    dialog: false,
    alert: false,
    errmsg1: "",
    errmsg2: "",
    formTitle: "실습 환경 신청",
    formHasErrors: false,
    itemsPerPage: -1, //all
    headers: [
      { text: "실습 서버", value: "env_name", align: "center" },
      { text: "OS 계정", value: "osuser", align: "center" },
      { text: "클라우드 테넌트", value: "tenant", align: "center" },
      { text: "리전", value: "region", align: "center" },
      { text: "신청자", value: "username", align: "center" }
    ],
    envs: [],
    editedIndex: -1,
    editedItem: {
      num: 0,
      username: "",
      email: ""
    },
    defaultItem: {
      username: "",
      email: "",
      passwd: ""
    },
    rules: {
      required: value => !!value || "Required.",
      counter: value => value.length <= 5 || "Max 5 characters",
      email: value => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return pattern.test(value) || "Invalid e-mail.";
      }
    }
  }),
  mounted() {
    this.$EventBus.$on(
      "initialize",
      function() {
        /* eslint-disable no-console */
        this.initialize();
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );
  },
  created() {
    this.initialize();
  },
  computed: {
    form() {
      return {
        username: this.editedItem.username,
        email: this.editedItem.email
      };
    }
  },
  methods: {
    initialize() {
      /* eslint-disable no-console */

      this.$axios({
        method: "get",
        url: "/api/v1/meetup/envs?sort=num,asc",
        headers: { "Content-Type": "application/json" }
      })
        .then(result => {
          this.envs = result.data;
        })
        .catch(error => {
          console.log(error.response.data);
        });
    },
    getColor(assign) {
      if (assign != null) return "grey";
      else return "green";
    },
    editItem(item) {
      this.alert = false;
      this.editedIndex = this.envs.indexOf(item);
      this.editedItem = Object.assign({}, item);

      if (item.username != null) {
        /* eslint-disable no-console */
        this.$EventBus.$emit("goEnvForm", item);
      } else {
        this.dialog = true;
      }
    },
    deleteItem(item) {
      const index = this.envs.indexOf(item);
      confirm("Are you sure you want to delete this item?") &&
        this.envs.splice(index, 1);
    },
    close() {
      this.dialog = false;
      setTimeout(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      }, 300);
      this.formHasErrors = false;
    },
    submit() {
      this.formHasErrors = false;

      Object.keys(this.form).forEach(f => {
        if (!this.form[f]) this.formHasErrors = true;

        //console.log(this.$refs[f]);
        this.$refs[f].validate(true);
      });

      if (this.formHasErrors == false) {
        if (this.editedIndex > -1) {
          // TODO: 여기서 AJAX로 데이터 업데이트 해야 한다
          // 성공하면 우측에 관련 실습 환경에 대한 정보를 보여준다.

          this.assignUser()
            .then(result => {
              this.searchEnv(result);
              Object.assign(this.envs[this.editedIndex], this.editedItem);
              this.close();
            })
            .catch(error => {
              //console.log(error.response.data.message);
              this.errmsg1 = error.message;

              if (
                error.response.data.message.includes(
                  "ConstraintViolationException"
                )
              )
                this.errmsg2 =
                  "이메일이 중복됩니다. 다른 이메일을 사용해주세요.";
              else this.errmsg2 = error.response.data.message;

              this.alert = true;
            });
          // update
        } else {
          this.envs.push(this.editedItem); // new item
        }
      }
    },
    delete() {
      this.close();
    },
    assignUser() {
      var _this = this;

      let data = {
        num: this.editedItem.num,
        osuser: this.editedItem.osuser,
        username: this.editedItem.username,
        email: this.editedItem.email,
        tenant: this.editedItem.tenant,
        region: this.editedItem.region,
        cloud_account: this.editedItem.cloud_account,
        cloud_account_pw: this.editedItem.cloud_account_pw,
        handson_doc_url: this.editedItem.handson_doc_url,
        key_download_url: this.editedItem.key_download_url,
        env_ip: this.editedItem.env_ip,
        env_name: this.editedItem.env_name
      };

      return new Promise((resolve, reject) => {
        _this
          .$axios({
            method: "post",
            url: "/api/v1/meetup/env",
            data: data,
            headers: {
              "Content-Type": "application/json"
            }
          })
          .then(result => {
            resolve(result.data);
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    searchEnv(data) {
      this.$EventBus.$emit("searchEnv", data.num);
    }
  }
};
</script>
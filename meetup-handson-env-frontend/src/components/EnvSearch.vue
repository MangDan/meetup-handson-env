<template v-slot:top>
  <div>
    <v-card flat height="540px" tile>
      <v-toolbar flat color="white">
        <v-toolbar-title>실습 환경 정보</v-toolbar-title>
        <!--
      <v-divider class="mx-4" inset vertical></v-divider>
        -->
        <v-chip class="ma-2" color="green" outlined @click="openConnectGuideDialog">
          <v-icon left>mdi-note-text</v-icon>실습환경 접속방법
        </v-chip>
        <v-chip
          v-if="$store.state.access_token"
          class="ma-2"
          color="deep-purple accent-4"
          outlined
          @click="openUpdateEnvDialog"
        >
          <v-icon left>mdi-wrench</v-icon>
          실습 환경 업데이트
          {{ $store.state.expires_in }}
        </v-chip>

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
                  <!-- <v-btn
                    v-if="role == 'admin' && passwd == 'welcome1' && num != 0"
                    class="ml-4"
                    rounded
                    dark
                    color="red"
                    @click="alertDialog = true"
                  >Reset</v-btn>-->
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

    <v-dialog v-model="guideDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
      <v-card>
        <v-app-bar dark elevate-on-scroll scroll-target="#conn-guides" color="primary">
          <v-btn icon dark @click="guideDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>실습환경 접속방법</v-toolbar-title>
        </v-app-bar>
        <v-list id="conn-guides" class="overflow-y-auto" max-height="600">
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>
                <a href="https://the.earth.li/~sgtatham/putty/latest/w64/putty.exe">Putty Download</a>
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item>
            <v-list-item-content>
              <v-img src="/assets/img/putty_connect_guide.png" aspect-ratio="2" contain></v-img>
            </v-list-item-content>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item>
            <v-img src="/assets/img/mac_terminal_connect_guide.png" aspect-ratio="2" contain></v-img>
          </v-list-item>
        </v-list>
      </v-card>
    </v-dialog>
    <v-dialog v-model="updateEnvDialog" scrollable>
      <v-data-table
        height="400px"
        fixed-header
        :headers="envHeaders"
        :items="envs"
        :items-per-page="10"
        sort-by="num"
        class="elevation-1"
      >
        <template v-slot:top>
          <v-toolbar flat color="white">
            <v-toolbar-title>실습 환경 업데이트</v-toolbar-title>
            <v-spacer></v-spacer>

            <v-dialog v-model="updateEnvFormDialog">
              <template v-slot:activator="{ on }">
                <v-btn icon v-on="on">
                  <v-icon>mdi-plus-circle-outline</v-icon>
                </v-btn>
              </template>
              <v-card>
                <v-card-title>
                  <span class="headline">실습 환경 업데이트</span>
                </v-card-title>
                <v-card-text>
                  <v-container>
                    <v-row>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.osuser" label="OS 계정"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.username" label="신청자"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.email" label="신청자 이메일"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.env_name" label="실습서버"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.env_ip" label="실습환경 IP"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.tenant" label="OCI 테넌트"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.region" label="리전"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.cloud_account" label="OCI 계정"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.cloud_account_pw" label="OCI 계정 패스워드"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.key_download_url" label="SSH KEY"></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6" md="4">
                        <v-text-field v-model="editedItem.handson_doc_url" label="실습 문서 및 소스"></v-text-field>
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="closeUpdateEnvDialog">Cancel</v-btn>
                  <v-btn color="blue darken-1" text @click="updateEnv">Save</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <v-btn icon @click="updateEnvDialog = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </v-toolbar>
        </template>
        <template v-slot:item.osuser="props">
          <v-edit-dialog
            :return-value.sync="props.item.num"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @close="closeSaveColunm"
          >
            {{ props.item.num }}
            <template v-slot:input>
              <v-text-field v-model="props.item.num" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.osuser="props">
          <v-edit-dialog
            :return-value.sync="props.item.osuser"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div v-on="on">{{ props.item.osuser }}</div>
              </template>
              <span>{{ props.item.osuser }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.osuser" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.username="props">
          <v-edit-dialog
            :return-value.sync="props.item.username"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm;orgColumnValue=props.item.username"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div v-on="on">{{ props.item.username }}</div>
              </template>
              <span>{{ props.item.username }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.username" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>

        <template v-slot:item.email="props">
          <v-edit-dialog
            :return-value.sync="props.item.email"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.email}}</div>
              </template>
              <span>{{ props.item.email }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.email" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.env_ip="props">
          <v-edit-dialog
            :return-value.sync="props.item.env_ip"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.env_ip}}</div>
              </template>
              <span>{{ props.item.env_ip }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.env_ip" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.env_name="props">
          <v-edit-dialog
            :return-value.sync="props.item.env_name"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.env_name}}</div>
              </template>
              <span>{{ props.item.env_name }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.env_name" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.tenant="props">
          <v-edit-dialog
            :return-value.sync="props.item.tenant"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.tenant}}</div>
              </template>
              <span>{{ props.item.tenant }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.tenant" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.region="props">
          <v-edit-dialog
            :return-value.sync="props.item.region"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.region}}</div>
              </template>
              <span>{{ props.item.region }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.region" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.cloud_account="props">
          <v-edit-dialog
            :return-value.sync="props.item.cloud_account"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.cloud_account}}</div>
              </template>
              <span>{{ props.item.cloud_account }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.cloud_account" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.cloud_account_pw="props">
          <v-edit-dialog
            :return-value.sync="props.item.cloud_account_pw"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.cloud_account_pw}}</div>
              </template>
              <span>{{ props.item.cloud_account_pw }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field v-model="props.item.cloud_account_pw" label="Edit" single-line counter></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.handson_doc_url="props">
          <v-edit-dialog
            :return-value.sync="props.item.handson_doc_url"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.handson_doc_url}}</div>
              </template>
              <span>{{ props.item.handson_doc_url }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field
                v-model="props.item.handson_doc_url"
                style="width:300px"
                label="Edit"
                single-line
                counter
              ></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.key_download_url="props">
          <v-edit-dialog
            :return-value.sync="props.item.key_download_url"
            @save="updateColunm(props.item)"
            @cancel="cancelUpdateColunm"
            @open="openUpdateColunm"
            @close="closeSaveColunm"
          >
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <div
                  class="d-inline-block text-truncate"
                  style="max-width: 80px;"
                  v-on="on"
                >{{props.item.key_download_url}}</div>
              </template>
              <span>{{ props.item.key_download_url }}</span>
            </v-tooltip>
            <template v-slot:input>
              <v-text-field
                v-model="props.item.key_download_url"
                style="width:300px"
                label="Edit"
                single-line
                counter
              ></v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.action="{ item }">
          <v-icon small class="mr-2" @click="editItem(item)">edit</v-icon>
          <v-icon small @click="deleteItem(item)">delete</v-icon>
        </template>
        <template v-slot:no-data>
          <v-btn color="primary" @click="initialize">Reset</v-btn>
        </template>
      </v-data-table>
      <v-snackbar v-model="snack" :timeout="3000" :color="snackColor">
        {{ snackText }}
        <v-btn text @click="snack = false">Close</v-btn>
      </v-snackbar>
    </v-dialog>
  </div>
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
    guideDialog: false,
    updateEnvDialog: false,
    updateEnvFormDialog: false,
    adminMode: true,
    snack: false,
    snackColor: "",
    snackText: "",
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
    ],
    envHeaders: [
      { text: "번호", align: "end", value: "num", sortable: false },
      { text: "OS 계정", align: "end", value: "osuser", sortable: false },
      { text: "신청자", align: "end", value: "username", sortable: false },
      {
        text: "신청자 이메일",
        align: "center",
        value: "email",
        sortable: false
      },
      {
        text: "실습 환경 IP",
        align: "center",
        value: "env_ip",
        sortable: false
      },
      {
        text: "실습 서버명",
        align: "center",
        value: "env_name",
        sortable: false
      },
      { text: "OCI 테넌트", align: "center", value: "tenant", sortable: false },
      { text: "리전", align: "center", value: "region", sortable: false },
      {
        text: "OCI 계정",
        align: "center",
        value: "cloud_account",
        sortable: false
      },
      {
        text: "OCI 패스워드",
        align: "center",
        value: "cloud_account_pw",
        sortable: false
      },
      {
        text: "실습 문서 주소",
        align: "center",
        value: "handson_doc_url",
        sortable: false
      },
      {
        text: "SSH KEY",
        align: "center",
        value: "key_download_url",
        sortable: false
      },
      { text: "Actions", value: "action", sortable: false }
    ],
    envs: [],
    editedIndex: -1,
    editedItem: {
      num: 0,
      osuser: "",
      username: "",
      email: "",
      tenant: "",
      region: "",
      cloud_account: "",
      cloud_account_pw: "",
      handson_doc_url: "",
      key_download_url: "",
      env_ip: "",
      env_name: ""
    },
    defaultItem: {
      osuser: "",
      username: "",
      email: "",
      tenant: "",
      region: "",
      cloud_account: "",
      cloud_account_pw: "",
      handson_doc_url: "",
      key_download_url: "",
      env_ip: "",
      env_name: ""
    },
    orgColumnValue: ""
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
        url: "/api/v1/admin/meetup/env/reset",
        data: data,
        headers: {
          "X-Authorization": "Bearer " + sessionStorage.getItem("access_token"),
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
    },
    openConnectGuideDialog() {
      this.guideDialog = true;
    },
    openUpdateEnvDialog() {
      this.fetchEnvs().then(result => {
        this.envs = result.data;
        this.updateEnvDialog = true;
      });
    },
    fetchEnvs() {
      var _this = this;

      return new Promise((resolve, reject) => {
        _this
          .$axios({
            method: "get",
            url: "/api/v1/meetup/envs?sort=num,asc",
            headers: { "Content-Type": "application/json" }
          })
          .then(result => {
            resolve(result);
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    editItem(item) {
      this.editedIndex = this.envs.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.updateEnvFormDialog = true;
    },

    deleteItem(item) {
      const index = this.envs.indexOf(item);
      confirm("Are you sure you want to delete this item?") &&
        this.envs.splice(index, 1);
    },

    closeUpdateEnvDialog() {
      this.updateEnvFormDialog = false;
      setTimeout(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      }, 300);
    },

    updateEnv() {
      //console.log(this.editedItem);

      this.$axios({
        method: "post",
        url: "/api/v1/admin/meetup/env/reset",
        data: this.editedItem,
        headers: {
          "X-Authorization": "Bearer " + sessionStorage.getItem("access_token"),
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          console.log(result);
          if (this.editedIndex > -1) {
            Object.assign(this.envs[this.editedIndex], this.editedItem);
          } else {
            this.updateEnvFormDialog.push(this.editedItem);
          }
          this.closeUpdateEnvDialog();
        })
        .catch(error => {
          this.snack = true;
          this.snackColor = "error";
          this.snackText = error.response.data.message;
        });
    },
    updateColunm(item) {
      console.log(item);
      this.$axios({
        method: "post",
        url: "/api/v1/admin/meetup/env/reset",
        data: item,
        headers: {
          "X-Authorization": "Bearer " + sessionStorage.getItem("access_token"),
          "Content-Type": "application/json"
        }
      })
        .then(result => {
          console.log(result.data);
          this.snack = true;
          this.snackColor = "success";
          this.snackText = "Data saved";
        })
        .catch(error => {
          console.log(error);
          this.snack = true;
          this.snackColor = "error";
          this.snackText = error.response.data.message;
        });
    },
    cancelUpdateColunm() {
      this.snack = true;
      this.snackColor = "error";
      this.snackText = "Canceled";
    },
    openUpdateColunm() {
      this.snack = true;
      this.snackColor = "info";
      this.snackText = "Dialog opened";
    },
    closeSaveColunm() {
      console.log("Dialog closed");
    }
  }
};
</script>
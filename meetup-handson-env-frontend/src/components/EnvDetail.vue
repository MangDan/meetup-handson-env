<template>
  <div>
    <v-tooltip
      v-model="tooltipShow"
      left
      :position-x="tooltipPositionX"
      :position-y="tooltipPositionY"
      color="red"
    >
      <span>Copied</span>
    </v-tooltip>
    <v-container v-if="this.listType=='card'" fluid class="pt-0">
      <v-row>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>신청자</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.username}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon @click="copy(env.username, $event)">
                  <v-icon>mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>신청자 이메일</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.email}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.email, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>OS(linux) 계정</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.osuser}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.osuser, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>OS(linux) 접속 SSH KEY</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>
                    <a :href="env.key_download_url">
                      <font color="white">다운로드</font>
                    </a>
                  </kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.key_download_url, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>실습 환경 IP</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.env_ip}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon @click="copy(env.env_ip, $event)">
                  <v-icon>mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>클라우드 테넌트(Tenant)</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.tenant}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.tenant, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>오라클 클라우드 계정</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.cloud_account}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.cloud_account, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
        <v-col cols="12" sm="6" md="6" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>오라클 클라우드 계정 패스워드</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>{{env.cloud_account_pw}}</kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.cloud_account_pw, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="12" md="12" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>오라클 클라우드 접속 주소 ({{env.region}})</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>
                    <a :href="'https://console.'+env.region+'.oraclecloud.com?tenant='+env.tenant">
                      <font
                        color="white"
                      >https://console.{{env.region}}.oraclecloud.com?tenant={{env.tenant}}</font>
                    </a>
                  </kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon
                    @click="copy('https://console.'+env.region+'.oraclecloud.com?tenant='+env.tenant, $event)"
                  >mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="12" md="12" class="pa-1">
          <v-card class="mx-auto" outlined tile>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title disabled>실습 문서 및 소스</v-list-item-title>
                <v-list-item-subtitle>
                  <kbd>
                    <a :href="env.handson_doc_url">
                      <font color="white">{{env.handson_doc_url}}</font>
                    </a>
                  </kbd>
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-icon>
                <v-btn icon>
                  <v-icon @click="copy(env.handson_doc_url, $event)">mdi-content-copy</v-icon>
                </v-btn>
              </v-list-item-icon>
            </v-list-item>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-container v-else-if="this.listType=='list'" fluid class="pt-0">
      <v-list dense two-line height="470px" style="overflow-y:auto">
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>신청자</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.username}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.username, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>신청자 이메일</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.email}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.email, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>OS(linux) 계정</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.osuser}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.osuser, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>OS(linux) 접속 SSH KEY</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>
                <a :href="env.key_download_url">
                  <font color="white">다운로드</font>
                </a>
              </kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.key_download_url, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>실습 환경 IP</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.env_ip}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon @click="copy(env.env_ip, $event)">
              <v-icon>mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>클라우드 테넌트(Tenant)</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.tenant}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.tenant, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>오라클 클라우드 계정</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.cloud_account}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.cloud_account, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>오라클 클라우드 계정 패스워드</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>{{env.cloud_account_pw}}</kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.cloud_account_pw, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>오라클 클라우드 접속 주소 ({{env.region}})</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>
                <a :href="'https://console.'+env.region+'.oraclecloud.com?tenant='+env.tenant">
                  <font
                    color="white"
                  >https://console.{{env.region}}.oraclecloud.com?tenant={{env.tenant}}</font>
                </a>
              </kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon
                @click="copy('https://console.'+env.region+'.oraclecloud.com?tenant='+env.tenant, $event)"
              >mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title disabled>실습 문서 및 소스</v-list-item-title>
            <v-list-item-subtitle>
              <kbd>
                <a :href="env.handson_doc_url">
                  <font color="white">{{env.handson_doc_url}}</font>
                </a>
              </kbd>
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-icon>
            <v-btn icon>
              <v-icon @click="copy(env.handson_doc_url, $event)">mdi-content-copy</v-icon>
            </v-btn>
          </v-list-item-icon>
        </v-list-item>
      </v-list>
    </v-container>
  </div>
</template>
<script>
export default {
  name: "MeetupHandsonEnvDetail",
  data: () => ({
    tooltipShow: false,
    tooltipPositionX: 0,
    tooltipPositionY: 0,
    tooltipCloseDelay: 1000,
    notifications: false,
    sound: true,
    widgets: false,
    alert: false,
    errmsg: "",
    listType: "card",
    env: {
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
      env_ip: ""
    }
  }),
  mounted() {
    this.$EventBus.$on(
      "displayEnv",
      function(listType, data) {
        /* eslint-disable no-console */
        this.displayEnv(listType, data);
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );
  },
  computed: {},
  methods: {
    displayEnv(listType, data) {
      /* eslint-disable no-console */
      // console.log(listType); // card, list
      console.log(data);
      this.listType = listType;
      this.env = Object.assign({}, data);
    },
    copy(copyTxt, event) {
      let tempElem = document.createElement("textarea");
      tempElem.value = copyTxt;
      document.body.appendChild(tempElem);

      tempElem.select();
      document.execCommand("copy");
      document.body.removeChild(tempElem);

      // console.log(event);
      // console.log(copyTxt);

      if (this.tooltipShow == false) {
        this.tooltipShow = !this.tooltipShow;

        this.tooltipPositionX = event.clientX - 10;
        this.tooltipPositionY = event.clientY;

        setTimeout(
          function() {
            this.tooltipShow = !this.tooltipShow;
          }.bind(this),
          this.tooltipCloseDelay
        );
      }
    },
    openConnectGuideDialog() {
      this.dialog = true;
    }
  }
};
</script>



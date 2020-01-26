<template>
  <v-container fluid>
    <v-row></v-row>

    <v-row>
      <!-- Environments -->
      <v-col xs="12" sm="12" md="12" lg="5" xl="5">
        <v-card class="mr-1 pa-1" outlined tile>
          <EnvList ref="envlist" :role="role" :passwd="passwd" />
        </v-card>
      </v-col>
      <!-- Environment Detail -->
      <v-col xs="12" sm="12" md="12" lg="7" xl="7">
        <v-card class="mr-1 pa-1" outlined tile>
          <EnvSearch ref="envsearch" :role="role" :passwd="passwd" />
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import EnvList from "../components/EnvList";
import EnvSearch from "../components/EnvSearch";

export default {
  name: "home",
  components: {
    EnvList,
    EnvSearch
  },
  data() {
    return { role: "", passwd: "" };
  },
  mounted() {
    this.role = this.$route.params.role;
    this.passwd = this.$route.params.passwd;
    /* eslint-disable no-console */
    console.log(this.role);
  },
  created() {},
  methods: {}
};
</script>

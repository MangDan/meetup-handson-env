<template>
  <v-app>
    <Header :role="role" :passwd="passwd" />
    <v-content height="auto">
      <router-view :role="role" :passwd="passwd"></router-view>
    </v-content>
    <Footer />
  </v-app>
</template>

<script>
import Header from "./components/Header";
import Footer from "./components/Footer";

export default {
  name: "App",

  components: {
    Header,
    Footer
  },

  data() {
    return { role: "", passwd: "" };
  },
  mounted() {
    this.role = this.$route.params.role;
    this.passwd = this.$route.params.passwd;
  }
};
</script>

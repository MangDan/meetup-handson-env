const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
  "transpileDependencies": [
    "vuetify"
  ],
  configureWebpack: {
    devServer: {
      host: '140.238.22.91',
      port: '80'
      //host: 'localhost',
      //port: '8080'
    }
  }
}
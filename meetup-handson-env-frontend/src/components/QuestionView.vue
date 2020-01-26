<template>
  <div>
    <div style="display:none;">$$\require{cancel}$$</div>
    <div class="container" style="height:135px;overflow-y:auto">
      <div id="html" class="content" v-html="html"></div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      content: "test"
    };
  },
  props: {
    html: {
      type: String,
      default: null
    }
  },
  created() {
    var MathJax = window.MathJax; // eslint-disable-line no-unused-vars

    window.MathJax.Hub.Config({
      showMathMenu: false,
      tex2jax: { displayMath: [["$$", "$$"]] }
    });

    window.cdml_mathjax_init();
    window.cdml_mathjax_rerender();
  }
};
</script>
<style>
p,
div,
ul,
li,
dl,
dt,
dd,
ol,
a,
strong,
fieldset,
textarea,
input,
select,
span,
table,
th,
td,
address,
cite,
h1,
h2,
h3,
h4,
h5,
h6 {
  color: #000;
  font-size: 13px;
  line-height: 140%;
}

div#web {
  max-width: 980px;
  min-height: 800px;
  margin: 0 auto;
  padding: 0 0 30px 0;
  background: url(/app/q/questions/line.gif) 50% 0 repeat-y;
}

div#web div#header {
  width: 100%;
  text-align: center;
  position: relative;
  background: url(/app/q/questions/line.gif) 0 100% repeat-x #fff;
  padding: 0 0 10px 0;
  height: 80px;
}

div#web div#header h1 {
  width: 100%;
  font-size: 60px !important;
  font-weight: bold;
}

div#web div#header p.paging {
  font-size: 30px;
  font-weight: bold;
  position: absolute;
  bottom: 0px;
  left: 0;
}

div#web div.container {
  width: 100%;
  display: inline-block;
  padding: 15px 0 0 0;
  background: url(/app/q/questions/line.gif) 0 100% repeat-x;
}

div#web div.content {
  width: 49.9%;
  float: left;
  padding: 10px;
}

div#mobile {
  width: 100%;
  min-width: 320px;
  margin: 0 auto;
  padding: 0 0 30px 0;
}

div#mobile div#header {
  width: 100%;
  text-align: center;
  position: relative;
  background: url(/app/q/questions/line.gif) 0 100% repeat-x #fff;
  padding: 0 0 10px 0;
  height: 40px;
}

div#mobile div#header h1 {
  width: 100%;
  font-size: 30px;
  font-weight: bold;
}

div#mobile div#header p.paging {
  font-size: 20px;
  font-weight: bold;
  position: absolute;
  bottom: 0px;
  left: 0;
}

div#mobile div.container {
  width: 100%;
  display: inline-block;
  padding: 15px 0 0 0;
  background: url(/app/q/questions/line.gif) 0 100% repeat-x;
}

div#mobile div.content {
  width: 100%;
}

.btn_div {
  margin-bottom: 15px;
}
</style>
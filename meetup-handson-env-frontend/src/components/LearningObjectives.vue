<template>
  <v-card>
    <v-card-title style="padding: 16px 16px 1px 16px;" class="font-weight-black">
      <v-icon class="mr-2">mdi-teach</v-icon>학습 목표
      <v-spacer></v-spacer>
      <span class="title">STEP 1</span>
      <v-select
        class="pl-3"
        v-model="selgradeunit"
        :items="gradeunits"
        append-outer-icon="search"
        label="선택"
        @click:append-outer="searchGradeUnits"
        style="width:250px"
      ></v-select>
    </v-card-title>
    <v-list shaped dense height="400px" style="overflow-y:auto">
      <v-list-item-group multiple v-model="selectedObjs" active-class="brown lighten-5">
        <template v-if="learning_objectives.length">
          <template v-for="(objective, index) in learning_objectives">
            <v-list-item :key="objective.lbno" :value="objective">
              <template v-slot:default="{ active, toggle }">
                <v-list-item-content style="max-width:50px">
                  <v-list-item-title v-text="objective.lbno"></v-list-item-title>
                </v-list-item-content>
                <v-list-item-content>
                  <v-list-item-title v-text="objective.objective"></v-list-item-title>
                </v-list-item-content>
                <v-list-item-action style="max-width:50px">
                  <!-- list에서 로우 클릭 시 체크박스가 체크되려면 v-model을 반드시 active로 해줘야 한다.-->
                  <v-checkbox v-model="active" color="brown darken-4"></v-checkbox>
                </v-list-item-action>
              </template>
            </v-list-item>
            <v-divider v-if="index + 1 < learning_objectives.length" :key="'divider'+index"></v-divider>
          </template>
        </template>
        <template v-else>
          <v-list-item>
            <template>
              <v-list-item-content>
                <v-list-item-title class="subtitle-1">학습목표를 선택해주세요.</v-list-item-title>
              </v-list-item-content>
            </template>
          </v-list-item>
        </template>
      </v-list-item-group>
    </v-list>
  </v-card>
</template>

<script>
export default {
  name: "LearningObjectives",
  data() {
    return {
      search: "",
      selectedObjs: [],
      selgradeunit: [],
      learning_objectives: [],
      gradeunits: ["선택"]
    };
  },
  mounted() {
    this.loadGradeUnit();
  },
  methods: {
    loadGradeUnit() {
      this.$axios
        .get("/api/v1/kerisaig/query/gradeunit")
        .then(response => {
          for (var i = 0; i < response.data.length; i++) {
            if (response.data[i].unitValue != null) {
              this.gradeunits.push({
                text:
                  response.data[i].eduGubn +
                  " " +
                  response.data[i].schoolYear +
                  "학년 " +
                  response.data[i].semester +
                  "학기 " +
                  (response.data[i].unitValue == null
                    ? ""
                    : response.data[i].unitValue),
                value: response.data[i].guno
              });
            }
          }
        })
        .catch(error => {
          /* eslint-disable no-console */
          console.log(error);
          this.loadGradeUnit();
        });
    },
    searchGradeUnits() {
      /* eslint-disable no-console */
      //console.log(this.selgradeunit);
      if (this.selgradeunit == "" || this.selgradeunit == "선택") {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "학년/학기/차시 선택을 한 후 돋보기 버튼을 클릭하여 검색하세요!"
        );
      } else {
        // Issue: 한참 찾았다 ㅜㅜ 검색 버튼 클릭할 때 같은 데이터가 있으면 선택된 채로 남기 때문에 미리보기에서 또 추가되는 것 같다... 아예 검색 시점에 초기화해서 해결
        this.learning_objectives = [];
        this.$axios
          .get("/api/v1/kerisaig/query/learningobjective/" + this.selgradeunit)
          .then(response => {
            /* eslint-disable no-console */
            //console.log(response.data.content);

            this.learning_objectives = response.data.content;
          });
      }
    }
  },
  watch: {
    selectedObjs: function(objectives) {
      //do something when the data changes.
      if (objectives) {
        this.$EventBus.$emit("queryQuestionPreviewList", objectives);
      }
    }
  }
};
</script>
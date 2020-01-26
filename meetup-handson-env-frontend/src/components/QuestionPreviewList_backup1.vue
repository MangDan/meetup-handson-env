<template>
  <v-card>
    <v-card-title style="padding: 16px 16px 1px 16px;" class="font-weight-black">
      <v-icon class="mr-2">mdi-view-agenda-outline</v-icon>문항유형 미리보기
      <v-spacer></v-spacer>
      <span class="title">STEP 2</span>
      <v-select
        class="pl-3"
        v-model="selcount"
        :items="qcount"
        label="문항당 생성개수"
        style="max-width:150px"
      ></v-select>
      <v-btn
        class="ma-2"
        large
        rounded
        :loading="loading"
        :disabled="loading"
        outlined
        color="indigo"
        @click="detectAndGenQuestion"
      >
        생성
        <template v-slot:loader>
          <span style="font-size:xx-small">Loading..</span>
        </template>
      </v-btn>
    </v-card-title>
    <v-list dense height="400px" style="overflow-y:auto">
      <template v-if="prevQuestions.length">
        <v-list-item-group multiple v-model="selectQs" active-class="brown lighten-5">
          <template v-for="(q, index) in prevQuestions">
            <v-list-item :key="q.content.qsno" :value="q">
              <template v-slot:default="{ active, toggle }">
                <v-list-item-content style="max-width:80px">
                  <v-list-item-title v-text="q.content.lbno +'-' +q.content.qsno"></v-list-item-title>
                </v-list-item-content>
                <v-list-item-content style="padding-top: 0px;padding-bottom: 0px">
                  <v-row>
                    <v-col cols="7" xs="7" sm="7" md="7" lg="7" xl="7">
                      <v-card raised class="mx-auto ml-1">
                        <v-list-item>
                          <v-icon class="mr-2">mdi-file-question-outline</v-icon>
                          <v-list-item-content>
                            <v-list-item-title class="subtitle-1 font-weight-bold">문항 내용</v-list-item-title>
                          </v-list-item-content>
                        </v-list-item>
                        <iframe
                          v-bind:id="'qp_bodyhtml' + q.content.qsno"
                          src="../../question_view.html"
                          height="130px"
                          width="100%"
                          frameborder="0"
                          @load="setPrevQuestion('qp_bodyhtml', q.content)"
                        ></iframe>
                      </v-card>
                    </v-col>
                    <v-col cols="5" xs="5" sm="5" md="5" lg="5" xl="5">
                      <v-card raised class="mx-auto mr-1">
                        <v-list-item>
                          <v-icon class="mr-2">mdi-adchoices</v-icon>
                          <v-list-item-content>
                            <v-list-item-title class="subtitle-1 font-weight-bold">객관식</v-list-item-title>
                          </v-list-item-content>
                        </v-list-item>
                        <iframe
                          v-bind:id="'qp_listhtml' + q.content.qsno"
                          src="../../question_view.html"
                          height="130px"
                          width="100%"
                          frameborder="0"
                          @load="setPrevQuestion('qp_listhtml', q.content)"
                        ></iframe>
                      </v-card>
                    </v-col>
                  </v-row>
                </v-list-item-content>
                <v-list-item-action style="max-width:50px">
                  <!-- list에서 로우 클릭 시 체크박스가 체크되려면 v-model을 반드시 active로 해줘야 한다.-->
                  <v-checkbox v-model="active" color="brown darken-4" @click="toggle"></v-checkbox>
                </v-list-item-action>
              </template>
            </v-list-item>
            <v-divider v-if="index + 1 < prevQuestions.length" :key="'divider'+index"></v-divider>
          </template>
        </v-list-item-group>
      </template>
      <template v-else>
        <v-list-item>
          <template>
            <v-list-item-content>
              <v-list-item-title class="subtitle-1">생성 할 문항을 선택해주세요.</v-list-item-title>
            </v-list-item-content>
          </template>
        </v-list-item>
      </template>
    </v-list>
  </v-card>
</template>
<script>
export default {
  name: "QuestionList",
  data() {
    return {
      selcount: 0,
      qcount: [1, 2, 3, 4, 5],
      selectQs: [],
      objective: null,
      loading: false,
      generationQs: [],
      prevQuestions: [],
      hmlList: [],
      generatedh: []
    };
  },
  created() {
    this.$EventBus.$on(
      "queryQuestionPreviewList",
      function(objectives) {
        /* eslint-disable no-console */
        //console.log("objectives.length:" + objectives.length);

        //this.objectives = objectives;
        if (this.prevQuestions.length > 0) this.prevQuestions = []; // 모든 미리보기 항목을 초기화 한다.
        if (this.selectQs.length > 0) this.selectQs = []; // 모든 선택 항목을 초기화 한다.

        if (objectives.length > 0) {
          // for loop를 밖으로 뺀다. axios를 for로 감싸니 objective를 제대로 할당 못한다 (비동기 때문인듯)
          for (var i = 0; i < objectives.length; i++) {
            this.queryQuestions(objectives[i]);
          }
        }
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );
  },
  methods: {
    queryQuestions(objective) {
      this.$axios
        .get("/api/v1/kerisaig/query/question/" + objective.lbno)
        .then(response => {
          /* eslint-disable no-console */
          //console.log(response.data.content.length);

          if (response.data.content.length > 0) {
            for (var j = 0; j < response.data.content.length; j++) {
              //console.log(response.data.content[j]);

              this.prevQuestions.push({
                objective: objective,
                content: response.data.content[j]
              });
            }
          }
          /* eslint-disable no-console */
          // console.log(
          //   "question_preview_list: " + this.prevQuestions.length
          // );
        });
    },
    setPrevQuestion(ref, q) {
      /* eslint-disable no-console */
      //console.log(this.$refs[ref + "-" + q.qsno]);
      //his.$refs[ref + q.qsno].contentWindow.setHtml(q.bodyhtml);

      //iframe.append.style = "width:100%;height:100px;frameborder:0";
      // src="../../question_view.html"
      //                     height="100px"
      //                     width="100%"
      //                     frameborder="0"

      //console.log(this.$refs[ref + q.qsno]);
      setTimeout(function() {
        if (ref == "qp_bodyhtml")
          document
            .getElementById(ref + q.qsno)
            .contentWindow.setHtml(q.bodyhtml);
        else if (ref == "qp_listhtml") {
          try {
            document
              .getElementById(ref + q.qsno)
              .contentWindow.setHtml(q.listhtml);
          } catch (e) {
            setTimeout(function() {
              document
                .getElementById(ref + q.qsno)
                .contentWindow.setHtml(q.listhtml);
            }, 500);
          }
        }
        //this.$refs[ref + q.qsno].contentWindow.setHtml(q.bodyhtml);
      }, 100);

      // if (iframe) {
      //   console.log(this.$refs[ref + q.qsno].contentWindow);
      // }

      //this.$refs[ref + q.qsno].contentWindow.postMessage(q.bodyhtml, "*");
    },
    async detectAndGenQuestion() {
      /* eslint-disable no-console */
      //console.log(this.prevq.qsno);
      var _this = this;

      if (this.selcount == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "문항당 생성개수를 선택해주세요!"
        );
        return;
      }

      this.generationQs.length = 0; //generationQ init
      this.loading = true; //버튼 로딩 시작
      this.$EventBus.$emit("popProgressBarToHome", "문항 생성중...", true); // 프로그래스바 시작

      //console.log(this.selectQs);
      if (this.selectQs.length == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "문항을 선택해주세요! (다수 선택 가능)"
        );
        this.loading = false; //버튼 로딩 종료
        this.$EventBus.$emit("popProgressBarToHome", "", false); // 프로그래스바 종료
        return;
      } else {
        for (var i = 0; i < this.selectQs.length; i++) {
          //if (this.selectQs.length - 1 == i) isLast = true;
          await this.detectQuestion(this.selectQs[i]) // 순차로 처리하기 위해서 async와 await을 사용함.
            .then(detectionQ => {
              return detectionQ;
            })
            .then(this.genQuestion);
        }
      }

      if (this.generationQs.length == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "생성된 문항이 없습니다. 관리자에게 문의하세요!"
        );
        this.loading = false; //버튼 로딩 종료
        this.$EventBus.$emit("popProgressBarToHome", "", false); // 프로그래스바 종료
        return;
      } else {
        for (var j = 0; j < this.generationQs.length; j++) {
          if (this.generationQs[j].generationHml.hml_list.length == 0) {
            this.$EventBus.$emit(
              "popAlertMessageToHome",
              "생성된 hml이 없습니다. 관리자에게 문의하세요!"
            );
            this.loading = false; //버튼 로딩 종료
            this.$EventBus.$emit("popProgressBarToHome", "", false); // 프로그래스바 종료
            return;
          } else {
            for (
              var h = 0;
              h < this.generationQs[j].generationHml.hml_list.length;
              h++
            ) {
              //if (this.selectQs.length - 1 == i) isLast = true;
              await this.generationQwithHtml(
                this.generationQs[j].generationHml.hml_list[h].hml
              ).then(function(html) {
                _this.generationQs[j].generationHtml.html_list.push(html);
              });
            }
          }
        }
      }

      this.displayGenerationQs(); //생성 문항 목록으로 전달
      this.loading = false; //버튼 로딩 종료
      this.$EventBus.$emit("popProgressBarToHome", "", false); // 프로그래스바 종료
      //console.log(JSON.stringify(this.generationQs));

      // _this.detections.length = 0; //init array
      // //check this.detection.length
      // if (_this.selectQs.length == 0) {
      //   alert("selectQ count = 0");
      //   return;
      // }
      // if (_this.selectQs.length > 0) {
      //   for (var i = 0; i < _this.selectQs.length; i++) {

      // generation까지 호출 완료, 이후 진행........
      // this.loading = true; //로딩 시작

      // this.detectQuestions().then(function() {
      //   console.log("start generatin question...");
      //   //_this.genQuestions();
      // });

      // this.loading = false; // 로딩 종료
    },
    detectQuestion(selectQ) {
      //console.log("detectQuestion-" + idx);
      var _this = this;

      return new Promise((resolve, reject) => {
        // for test
        //var formBody = _this.createReqDetectQSampleFormBody();
        // real..
        var formBody = this.createReqDetectQFormBody(selectQ);
        // 문항 디텍트

        _this
          .$detection({
            method: "post",
            url: "/cms.iscream.cceapi.com/cms_api/model_information/",
            data: formBody,
            headers: { "Content-Type": "multipart/form-data" }
          })
          .then(result => {
            resolve({
              selectQ: selectQ,
              detectionModel: result.data
            });
          })
          .catch(error => {
            //handle error
            reject(error);
            console.log(error);
          });
      });
    },
    genQuestion(detectionQ) {
      var _this = this;
      /* eslint-disable no-console */

      // for test
      //var formBody = this.createReqGenerateQSampleFormBody(detectionQ);
      // real..
      var formBody = this.createReqGenerateQFormBody(detectionQ);
      return new Promise((resolve, reject) => {
        _this
          .$generation({
            method: "post",
            url: "/cms.iscream.cceapi.com/cms_api/create_question/",
            data: formBody,
            headers: { "Content-Type": "multipart/form-data" }
          })
          .then(result => {
            this.generationQs.push({
              objective: detectionQ.selectQ.objective,
              generationHml: result.data,
              generationHtml: { html_list: [] }
            }); // html 결과만 제외하고 모두 담는다.

            resolve({
              objective: detectionQ.selectQ.objective,
              generationHml: result.data,
              generationHtml: { html_list: [] }
            });
            //console.log(_this.generations);

            //this.$EventBus.$emit("generationQuestions", _this.generations);
          })
          .catch(error => {
            //handle error
            reject(error);
            console.log(error);
          });
      });
    },
    generationQwithHtml(hml) {
      //console.log("generationQwithHtml-" + generationQ.idx);
      var _this = this;

      //console.log(this.generationQs);
      return new Promise((resolve, reject) => {
        // var blob = this.dataURItoBlob(
        //   "data:binary;charset=UTF-8;base64," + hml
        // );

        let form = new FormData();
        //form.append("file", blob, "hml.hml");
        form.append("hml", hml);

        _this
          .$iscream({
            method: "post",
            //url: "/cms_api/questions_preview",
            url: "/hml2html",
            data: form,
            headers: { "Content-Type": "multipart/form-data" }
          })
          .then(result => {
            //console.log(this.generationQs);

            // this.generationQs[generationQ.idx].generationHtml.html_list.push(
            //   result.data
            // );

            resolve(result.data);
          })
          .catch(function(error) {
            //handle error
            console.log(error);

            // 일단 추가, 나중에 삭제
            // resolve("<div></div>");
            // 일단 제거, 나중에 다시 주석 해제
            reject(error);
          });
      });
    },
    displayGenerationQs() {
      //console.log(this.generationQs);
      this.$EventBus.$emit("generatedQuestions", this.generationQs);
    },
    createReqDetectQFormBody(selectQ) {
      let form = new FormData();

      form.append("service_id", "");
      form.append("etc_service_id", "");
      form.append("category1", selectQ.content.category1);
      form.append("category2", selectQ.content.category2);
      form.append("category3", selectQ.content.category3);
      form.append("category4", selectQ.content.category4);
      form.append("category5", selectQ.content.category5);
      form.append("chapter_code", selectQ.content.chapterCode);
      form.append("etc_category1", selectQ.content.etcCategory1);
      form.append("etc_category2", selectQ.content.etcCategory2);
      form.append("etc_category3", selectQ.content.etcCategory3);
      form.append("etc_category4", selectQ.content.etcCategory4);
      form.append("etc_category5", selectQ.content.etcCategory5);
      form.append("etc_chapter_code", "");
      form.append("question_id", selectQ.content.qsno);
      form.append("subject_cd", "");
      form.append("subject_cd_value", "");
      form.append("body_title_html", "");
      form.append("body_html", selectQ.content.bodyhtml);
      form.append("body_ex_html", selectQ.content.bodyext);
      form.append("list_html", selectQ.content.listhtml);
      form.append("answer_html", selectQ.content.answerhtml);
      form.append("explanation_html", selectQ.content.bodyext);
      form.append("hint_html", "");
      form.append("help1_html", "");
      form.append("help2_html", "");
      form.append("help3_html", "");
      form.append("help4_html", "");
      form.append("help5_html", "");
      form.append("question_group_id", "");
      form.append("sentence_html", "");
      form.append("sub_sentence_id", "");
      form.append("sub_sentence_html", "");
      form.append("small_group_id", "");
      form.append("small_group_html", "");
      form.append("analysis_id", "");
      form.append("analysis_name", "");
      form.append("studytree_id", "");
      form.append("studytree_name", "");
      form.append("question_type_cd", "");
      form.append("question_type_cd_value", selectQ.content.questionType);
      form.append("f_choice_shape_cd", "");
      form.append("f_choice_shape_cd_value", "");
      form.append("f_test_yn", "");
      form.append("f_usage_cd", "");
      form.append("f_usage_cd_value", "");
      form.append("difficulty_cd", "");
      form.append("difficulty_cd_value", "");
      form.append("f_weight_yn", "");
      form.append("f_book_cd", "");
      form.append("f_book_cd_value", "");
      form.append("f_etc10_cd", "");
      form.append("f_etc10_cd_value", "");
      form.append("point", "");
      form.append("cc_answer", "");
      form.append("cc_legend", "");
      form.append("concept_ids", "");
      form.append("inspection_mode_cd", "");
      form.append("inspection_mode_cd_value", "");
      form.append("f_book_desc", "");
      form.append("f_listen", "");
      form.append("f_keyword", "");
      form.append("f_etc_txt01", "");
      form.append("f_etc_txt02", "");
      form.append("f_etc_txt03", "");
      form.append("f_etc_txt04", "");
      form.append("f_etc_txt05", "");

      return form;
    },
    createReqDetectQSampleFormBody() {
      let form = new FormData();

      form.append("service_id", "");
      form.append("etc_service_id", "");
      form.append("category1", "01 소인수분해");
      form.append("category2", "01 소수와 합성수");
      form.append("category3", "01 소수와 합성수");
      form.append("category4", "02 소수와 합성수");
      form.append("category5", "");
      form.append("chapter_code", "MA1507A01010102");
      form.append("etc_category1", "중학교 1학년 / 1학기");
      form.append("etc_category2", "소인수분해");
      form.append("etc_category3", "소인수분해");
      form.append("etc_category4", "소수와 합성수");
      form.append("etc_category5", "");
      form.append("etc_chapter_code", "");
      form.append("question_id", "76032");
      form.append("subject_cd", "");
      form.append("subject_cd_value", "");
      form.append("body_title_html", "");
      form.append("body_title_html", "");
      form.append(
        "body_html",
        '<div class="cdml_question"><div class="question_box"><p style="margin: 0px 0px 0px 0px;text-align: left;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;">[#NO#] <span style="vertical-align: baseline;">다음&nbsp; 중&nbsp; 소수는&nbsp; 모두&nbsp; 몇&nbsp; 개인지&nbsp; 구하시오.</span></p><p style="margin: 0px 0px 0px 0px;text-align: left;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;">&nbsp;<span style="vertical-align: baseline;"></span></p><div style="margin: 0px 0px 0px 0px;text-align: center;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;"><div style="display: inline-block;width: 95%;"><table style="min-width: 100%;margin: 0px 0px 0px 0px;" ><colgroup><col width="100%"/></colgroup><tr><td colspan="1" rowspan="1" style="padding: 2px 2px 2px 2px;border-top: 1px solid #000000;border-right: 1px solid #000000;border-bottom: 1px solid #000000;border-left: 1px solid #000000;background-color: transparent;vertical-align: middle;height: 34px;" class=""><p style="margin: 0px 0px 0px 0px;text-align: center;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;"><span class="ccctex " style="">$$ {1} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {7} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {9} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {13} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {19} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {27} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {33} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {45} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {57} $$</span><span style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$ {71} $$</span></p></td></tr></table></div></div><p style="margin: 0px 0px 0px 0px;text-align: left;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;">&nbsp;<span style="vertical-align: baseline;"></span></p><p style="margin: 0px 0px 0px 0px;text-align: right;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;"><span style="padding: 0px 0px 0px 0px;border-top: 1px solid #000000;border-right: 1px solid #000000;border-bottom: 1px solid #000000;border-left: 1px solid #000000;background-color: transparent;vertical-align: middle;width: 75px;height: 37px;" class=""><input type="text" class="cc-input" style="width: 75px;" value="" data-index="①"/></span><span style="vertical-align: baseline;">개</span></p></div></div>'
      );
      form.append("body_ex_html", "");
      form.append("list_html", "");
      form.append(
        "answer_html",
        '<div class="cdml_question">\n<div class="question_box">\n<p  style="margin: 0px 0px 0px 0px;text-align: left;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;"><span class="ccctex " style="">$$  {4}  $$</span></p>\r\n\n</div>\n</div>\n'
      );
      form.append(
        "explanation_html",
        '<div class="cdml_question">\n<div class="question_box">\n<p  style="margin: 0px 0px 0px 0px;text-align: left;vertical-align: baseline;text-indent: 0px;line-height: 160.0%;border-top: 0;border-right: 0;border-bottom: 0;border-left: 0;"><span  style="vertical-align: baseline;">소수는&nbsp; </span><span class="ccctex " style="">$$  {7}  $$</span><span  style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$  {13}  $$</span><span  style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$  {19}  $$</span><span  style="vertical-align: baseline;">,&nbsp; </span><span class="ccctex " style="">$$  {71}  $$</span><span  style="vertical-align: baseline;">의&nbsp; </span><span class="ccctex " style="">$$  {4}  $$</span><span  style="vertical-align: baseline;">개이다.</span></p>\r\n\n</div>\n</div>\n'
      );
      form.append("hint_html", "");
      form.append("help1_html", "");
      form.append("help2_html", "");
      form.append("help3_html", "");
      form.append("help4_html", "");
      form.append("help5_html", "");
      form.append("question_group_id", "");
      form.append("sentence_html", "");
      form.append("sub_sentence_id", "");
      form.append("sub_sentence_html", "");
      form.append("small_group_id", "");
      form.append("small_group_html", "");
      form.append("analysis_id", "");
      form.append("analysis_name", "");
      form.append("studytree_id", "");
      form.append("studytree_name", "");
      form.append("question_type_cd", "");
      form.append("question_type_cd_value", "");
      form.append("f_choice_shape_cd", "");
      form.append("f_choice_shape_cd_value", "");
      form.append("f_test_yn", "");
      form.append("f_usage_cd", "");
      form.append("f_usage_cd_value", "");
      form.append("difficulty_cd", "");
      form.append("difficulty_cd_value", "");
      form.append("f_weight_yn", "");
      form.append("f_book_cd", "");
      form.append("f_book_cd_value", "");
      form.append("f_etc10_cd", "");
      form.append("f_etc10_cd_value", "");
      form.append("point", "");
      form.append("cc_answer", "");
      form.append("cc_legend", "");
      form.append("concept_ids", "");
      form.append("inspection_mode_cd", "");
      form.append("inspection_mode_cd_value", "");
      form.append("f_book_desc", "");
      form.append("f_listen", "");
      form.append("f_keyword", "");
      form.append("f_etc_txt01", "");
      form.append("f_etc_txt02", "");
      form.append("f_etc_txt03", "");
      form.append("f_etc_txt04", "");
      form.append("f_etc_txt05", "");

      return form;
    },
    createReqGenerateQSampleFormBody(detectionQ) {
      let form = new FormData();

      form.append("request_count", this.selcount);
      form.append("service_id", "");
      form.append("etc_service_id", "");
      form.append("category1", "");
      form.append("category2", "");
      form.append("category3", "");
      form.append("category4", "");
      form.append("category5", "");
      form.append("chapter_code", detectionQ.selectQ.content.chapterCode);
      form.append("etc_category1", detectionQ.selectQ.content.etcCategory1);
      form.append("etc_category2", detectionQ.selectQ.content.etcCategory2);
      form.append("etc_category3", detectionQ.selectQ.content.etcCategory3);
      form.append("etc_category4", detectionQ.selectQ.content.etcCategory4);
      form.append("etc_category5", detectionQ.selectQ.content.etcCategory5);
      form.append("etc_chapter_code", "");
      form.append("question_id", detectionQ.selectQ.content.qsno);
      form.append("subject_cd", "");
      form.append("subject_cd_value", "");
      form.append("body_title_html", "");
      form.append("body_html", "");
      form.append("body_ex_html", "");
      form.append("list_html", "");
      form.append("answer_html", "");
      form.append("explanation_html", "");
      form.append("hint_html", "");
      form.append("help1_html", "");
      form.append("help2_html", "");
      form.append("help3_html", "");
      form.append("help4_html", "");
      form.append("help5_html", "");
      form.append("question_group_id", "");
      form.append("sentence_html", "");
      form.append("sub_sentence_id", "");
      form.append("sub_sentence_html", "");
      form.append("small_group_id", "");
      form.append("small_group_html", "");
      form.append("analysis_id", "");
      form.append("analysis_name", "이해력");
      form.append("studytree_id", "");
      form.append("studytree_name", "수와 연산");
      form.append("question_type_cd", "");
      form.append("question_type_cd_value", "");
      form.append("f_choice_shape_cd", "");
      form.append("f_choice_shape_cd_value", "");
      form.append("f_test_yn", "");
      form.append("f_usage_cd", "");
      form.append("f_usage_cd_value", "");
      form.append("difficulty_cd", "");
      form.append("difficulty_cd_value", detectionQ.selectQ.content.defficulty);
      form.append("f_weight_yn", "");
      form.append("f_book_cd", "");
      form.append("f_book_cd_value", "");
      form.append("f_etc10_cd", "");
      form.append("f_etc10_cd_value", "");
      form.append("point", "");
      form.append("cc_answer", "");
      form.append("cc_legend", "");
      form.append("concept_ids", "");
      form.append("inspection_mode_cd", "");
      form.append("inspection_mode_cd_value", "");
      form.append("f_book_desc", "");
      form.append("f_listen", "");
      form.append("f_keyword", "");
      form.append("f_etc_txt01", "");
      form.append("f_etc_txt02", "");
      form.append("f_etc_txt03", "");
      form.append("f_etc_txt04", "");
      form.append("f_etc_txt05", "");
      form.append("model", JSON.stringify(detectionQ.detectionModel));

      return form;
    },
    createReqGenerateQFormBody(detectionQ) {
      let form = new FormData();

      form.append("request_count", this.selcount);
      form.append("service_id", "");
      form.append("etc_service_id", "");
      form.append("category1", "");
      form.append("category2", "");
      form.append("category3", "");
      form.append("category4", "");
      form.append("category5", "");
      form.append("chapter_code", detectionQ.selectQ.content.chapterCode);
      form.append("etc_category1", detectionQ.selectQ.content.etcCategory1);
      form.append("etc_category2", detectionQ.selectQ.content.etcCategory2);
      form.append("etc_category3", detectionQ.selectQ.content.etcCategory3);
      form.append("etc_category4", detectionQ.selectQ.content.etcCategory4);
      form.append("etc_category5", detectionQ.selectQ.content.etcCategory5);
      form.append("etc_chapter_code", "");
      form.append("question_id", detectionQ.selectQ.content.qsno);
      form.append("subject_cd", "");
      form.append("subject_cd_value", "");
      form.append("body_title_html", "");
      form.append("body_html", "");
      form.append("body_ex_html", "");
      form.append("list_html", "");
      form.append("answer_html", "");
      form.append("explanation_html", "");
      form.append("hint_html", "");
      form.append("help1_html", "");
      form.append("help2_html", "");
      form.append("help3_html", "");
      form.append("help4_html", "");
      form.append("help5_html", "");
      form.append("question_group_id", "");
      form.append("sentence_html", "");
      form.append("sub_sentence_id", "");
      form.append("sub_sentence_html", "");
      form.append("small_group_id", "");
      form.append("small_group_html", "");
      form.append("analysis_id", "");
      form.append("analysis_name", "이해력");
      form.append("studytree_id", "");
      form.append("studytree_name", "수와 연산");
      form.append("question_type_cd", "");
      form.append("question_type_cd_value", "");
      form.append("f_choice_shape_cd", "");
      form.append("f_choice_shape_cd_value", "");
      form.append("f_test_yn", "");
      form.append("f_usage_cd", "");
      form.append("f_usage_cd_value", "");
      form.append("difficulty_cd", "");
      form.append("difficulty_cd_value", detectionQ.selectQ.content.defficulty);
      form.append("f_weight_yn", "");
      form.append("f_book_cd", "");
      form.append("f_book_cd_value", "");
      form.append("f_etc10_cd", "");
      form.append("f_etc10_cd_value", "");
      form.append("point", "");
      form.append("cc_answer", "");
      form.append("cc_legend", "");
      form.append("concept_ids", "");
      form.append("inspection_mode_cd", "");
      form.append("inspection_mode_cd_value", "");
      form.append("f_book_desc", "");
      form.append("f_listen", "");
      form.append("f_keyword", "");
      form.append("f_etc_txt01", "");
      form.append("f_etc_txt02", "");
      form.append("f_etc_txt03", "");
      form.append("f_etc_txt04", "");
      form.append("f_etc_txt05", "");
      form.append("model", JSON.stringify(detectionQ.detectionModel));

      return form;
    },
    dataURItoBlob(dataURI) {
      // convert base64/URLEncoded data component to raw binary data held in a string
      var byteString;

      if (dataURI.split(",")[0].indexOf("base64") >= 0)
        byteString = atob(dataURI.split(",")[1]);
      else byteString = unescape(dataURI.split(",")[1]);

      // separate out the mime component
      var mimeString = dataURI
        .split(",")[0]
        .split(":")[1]
        .split(";")[0];

      // write the bytes of the string to a typed array
      var ia = new Uint8Array(byteString.length);

      for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
      }

      return new Blob([ia], {
        type: mimeString
      });
    },
    sortFunc(obj) {
      console.log(obj);
      return obj.objective.slice().sort(function(a, b) {
        return a.lbno > b.lbno ? -1 : 1;
      });
    }
  }
};
</script>
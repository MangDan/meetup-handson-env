<template>
  <v-card class="mx-auto" style="padding-bottom:1px">
    <v-responsive :aspect-ratio="20/3">
      <v-card-title style="padding: 16px 16px 1px 16px;" class="font-weight-black">
        <v-icon class="mr-2">mdi-playlist-plus</v-icon>생성 문항 리스트
        <v-spacer></v-spacer>
        <span class="title">STEP 3</span>
        <v-btn
          class="ma-2"
          large
          rounded
          :loading="loading"
          :disabled="loading"
          outlined
          color="indigo"
          @click="downloadHmlFiles"
        >
          문항 다운로드
          <template v-slot:loader>
            <span style="font-size:xx-small">Loading..</span>
          </template>
        </v-btn>
        <!--
        <v-btn
          class="ma-2"
          large
          rounded
          :loading="loading"
          :disabled="loading"
          outlined
          color="indigo"
          @click="downloadIndividualHmlFiles"
        >
          개별 HML 파일로 다운로드
          <template v-slot:loader>
            <span style="font-size:xx-small">Loading..</span>
          </template>
        </v-btn>
        -->
      </v-card-title>
      <v-list dense style="overflow-y:auto" class="mt-1">
        <template v-if="generatedQhtmls.length">
          <v-list-item-group>
            <template v-for="(generatedQhtml, index) in generatedQhtmls">
              <v-list-item :key="'l_' + index">
                <template>
                  <v-list-item-content style="max-width:70px">
                    <v-list-item-title v-text="index+1"></v-list-item-title>
                  </v-list-item-content>
                  <v-list-item-content style="max-width:200px">
                    <v-list-item-title v-text="generatedQhtml.objective.lesson"></v-list-item-title>
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
                          <QuestionView v-bind:html="generatedQhtml.html.body_html" />
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
                          <QuestionView v-bind:html="generatedQhtml.html.list_html" />
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-list-item-content>
                </template>
              </v-list-item>
              <v-divider v-if="index + 1 < generatedQhtmls.length" :key="'divider'+index"></v-divider>
            </template>
          </v-list-item-group>
        </template>
        <template v-else>
          <v-list-item>
            <template>
              <v-list-item-content>
                <v-list-item-title class="subtitle-1" v-text="generatingMsg"></v-list-item-title>
              </v-list-item-content>
            </template>
          </v-list-item>
        </template>
      </v-list>
    </v-responsive>
  </v-card>
</template>
<script>
import JsZip from "jszip";
import saveAs from "file-saver";
import QuestionView from "./QuestionView";

export default {
  name: "GeneratedQuestions",
  components: {
    QuestionView
  },
  data() {
    return {
      loading: false,
      objective: null,
      isActive: false,
      generatedQhtmls: [],
      generatedQhmls: [],
      generatedQhmlStems: [],
      generatedQhmlAnswers: [],
      generatedQhmlModelIDs: [],
      generatingMsg: "생성 문항을 다운로드 해주세요.",
      h: window.innerHeight - 595
    };
  },
  created() {
    this.$EventBus.$on(
      "generatedQuestions",
      function(generationQs) {
        /* eslint-disable no-console */

        if (this.generatedQhtmls.length > 0) this.generatedQhtmls = []; // 모든 미리보기 항목을 초기화 한다.
        if (this.generatedQhmls.length > 0) this.generatedQhmls = []; // 모든 선택 항목을 초기화 한다.
        this.generatedQhmlModelIDs = [];
        this.generatingMsg = "Generating...";

        setTimeout(
          function() {
            this.displayGeneratedQuestions(generationQs);
          }.bind(this),
          100
        );
      }.bind(this) // EventBus에서는 Event 안에 this는 자신이 된다. 따라서 바인딩할때 이와 같이 처리해줘야 한다.
    );
  },
  methods: {
    displayGeneratedQuestions(generationQs) {
      //console.log(generationQs);

      if (generationQs.length == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "보여줄 생성 문항이 없습니다.! 관리자에게 문의하세요."
        );
        return;
      } else {
        // 멀티 선택으로 변경..
        //this.objective = generationQs[0].objective;
        //console.log(generationQs);

        let generatedCount = 0;
        for (var i = 0; i < generationQs.length; i++) {
          if (generationQs[i].generationHtml.html_list.length == 0) {
            this.$EventBus.$emit(
              "popAlertMessageToHome",
              "HTML로 변환된 생성 문항이 없습니다! 관리자에게 문의하세요."
            );
            return;
          } else {
            //console.log(JSON.stringify(generationQs[i]));
            for (
              var h = 0;
              h < generationQs[i].generationHtml.html_list.length;
              h++
            ) {
              //console.log(generationQs[i].generationHtml.html_list[h].d[0]);

              // 임시로 주석, 나중에 주석 해제
              this.generatedQhtmls.push({
                objective: generationQs[i].objective,
                //html: generationQs[i].generationHtml.html_list[h].d[0] as is
                html: generationQs[i].generationHtml.html_list[h] // 변경된 API는 d 노드가 없다.
              });

              this.generatedQhmls.push(
                generationQs[i].generationHml.hml_list[h].hml //base64
              );

              this.generatedQhmlStems.push(
                generationQs[i].generationHml.hml_stem_list[h].hml_stem //base64
              );

              this.generatedQhmlAnswers.push(
                generationQs[i].generationHml.hml_answer_list[h].hml_answer //base64
              );

              this.generatedQhmlModelIDs.push(
                generationQs[i].generationHml.model_id_list[h].model_id
              );

              generatedCount++;
            }
          }
        }

        this.generatingMsg = "생성 문항을 다운로드 해주세요.";

        this.$EventBus.$emit(
          "generaionSnackBarToHome",
          generatedCount + "개의 문항이 성공적으로 생성되었습니다."
        );
      }
    },
    downloadHmlFiles() {
      if (this.generatedQhmls.length == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "문항 생성을 먼저 진행해 주세요."
        );
        return;
      } else {
        var agent = navigator.userAgent.toLowerCase();

        if (
          navigator.appName == "Microsoft Internet Explorer" ||
          agent.search("trident") > -1 ||
          agent.search("edge/") > -1
        ) {
          console.log("MSIE or Edge Browser!!!");
          this.downloadHmlFilesForMSIE();
        } else {
          console.log("Other Browser!!!");
          this.downloadHmlFilesForOther();
        }

        this.sendDownloadHistory();
        //this.downloadHmlFilesForOther();
      }
    },
    sendDownloadHistory() {
      // TODO: 다운로드 받은 ID를 저장하자
      //console.log("다운로드 모델 IDs: " + this.generatedQhmlModelIDs);

      var _this = this;
      /* eslint-disable no-console */

      var formBody = new FormData();
      this.generatedQhmlModelIDs.forEach(modelid => {
        formBody.append("modelid", modelid);
        //console.log("modelid: " + modelid);
      });
      return new Promise((resolve, reject) => {
        _this
          .$axios({
            method: "post",
            url: "/api/v1/kerisaig/history/model/download",
            data: formBody,
            headers: { "Content-Type": "form-data" }
          })
          .then(result => {
            //console.log(result.data);
            resolve(result.data);
          })
          .catch(error => {
            //handle error
            reject(error);
            //console.log(error);
          });
      });
    },
    downloadHmlFilesForMSIE() {
      var zip = new JsZip(); // **ReferenceError: JSZip is not defined**

      console.log(this.generatedQhmlStems);
      console.log(this.generatedQhmlAnswers);

      //console.log("downloadHmlFiles");
      //console.log(this.generatedQhmls[0]);

      if (this.generatedQhmlStems.length == 0) {
        this.$EventBus.$emit(
          "popAlertMessageToHome",
          "문항 생성을 먼저 진행해 주세요."
        );
        return;
      } else {
        for (var i = 0; i < this.generatedQhmlStems.length; i++) {
          console.log(
            "this.generatedQhmlStems[i] : " + this.generatedQhmlStems[i]
          );
          zip.file(
            "문제(" + i + ").hwp",
            ("data:binary;base64," + this.generatedQhmlStems[i]).substring(
              ("data:binary;base64," + this.generatedQhmlStems[i]).indexOf(
                "base64,"
              ) + "base64,".length
            ),
            { base64: true }
          );
          console.log(
            "this.generatedQhmlAnswers[i] : " + this.generatedQhmlAnswers[i]
          );
          zip.file(
            "해설(" + i + ").hwp",
            ("data:binary;base64," + this.generatedQhmlAnswers[i]).substring(
              ("data:binary;base64," + this.generatedQhmlAnswers[i]).indexOf(
                "base64,"
              ) + "base64,".length
            ),
            { base64: true }
          );
        }
      }
      zip.generateAsync({ type: "blob" }).then(function(blob) {
        saveAs(blob, "문제집.zip");
      });
      this.$EventBus.$emit(
        "generaionSnackBarToHome",
        "파일이 다운로드 되었습니다."
      );
    },
    downloadHmlFilesForOther() {
      var zip = new JsZip(); // **ReferenceError: JSZip is not defined**
      var templateQuestionsHmlDoc,
        templateQuestionsHml,
        templateAnswersHmlDoc,
        templateAnswersHml,
        decodedTexthml,
        decodedHmlDoc,
        questionSectionNode,
        answerSectionNode,
        sectionPnodeSnapshots,
        sectionPnode;
      var parser = new DOMParser();
      var q_index = 0; //모든 hml for loop 밖에 둔다.
      var sectionMode; //q: 문제/선택지, a: 답/해설 i: 문항정보 이후
      let downloadedCount = 0;

      // read sample hml
      // this.$local.get("../hml/hml_sample.hml").then(response => {
      //   decodedHmlDoc = parser.parseFromString(response.data, "text/xml");
      // });

      // read template hml
      this.$local.get("../hml/template.hml").then(response => {
        // console.log(template_hml);

        templateQuestionsHmlDoc = parser.parseFromString(
          response.data,
          "text/xml"
        );
        templateAnswersHmlDoc = parser.parseFromString(
          response.data,
          "text/xml"
        );
        for (var i = 0; i < this.generatedQhmls.length; i++) {
          decodedTexthml = this.$base64.decode(this.generatedQhmls[i]); // DECODING
          //console.log(decodedTexthml);

          if (window.DOMParser) {
            //console.log("window.DOMParser");
            decodedHmlDoc = parser.parseFromString(decodedTexthml, "text/xml");
          }

          if (decodedHmlDoc.evaluate) {
            sectionPnodeSnapshots = decodedHmlDoc.evaluate(
              "/HWPML/BODY/SECTION/P",
              decodedHmlDoc.cloneNode(true),
              null,
              XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
              null
            );

            for (var j = 0; j < sectionPnodeSnapshots.snapshotLength; j++) {
              //모든 P노드에 대한 Iterator를 가져온다.
              sectionPnode = sectionPnodeSnapshots.snapshotItem(j);

              if (
                sectionPnode.getElementsByTagName("CHAR").length &&
                sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                  .nodeValue == "(문제)"
              ) {
                sectionMode = "q";
                questionSectionNode = templateQuestionsHmlDoc.createElement(
                  "SECTION"
                );
                questionSectionNode.setAttribute("ID", q_index);
                sectionPnode.getElementsByTagName(
                  "CHAR"
                )[0].childNodes[0].nodeValue = "(" + (q_index + 1) + "번 문제)";
                questionSectionNode.appendChild(sectionPnode);
              } else if (
                sectionPnode.getElementsByTagName("CHAR").length &&
                sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                  .nodeValue == "(답)"
              ) {
                sectionMode = "a";
                answerSectionNode = templateAnswersHmlDoc.createElement(
                  "SECTION"
                );
                answerSectionNode.setAttribute("ID", q_index);
                sectionPnode.getElementsByTagName(
                  "CHAR"
                )[0].childNodes[0].nodeValue = "(" + (q_index + 1) + "번 답)";
                answerSectionNode.appendChild(sectionPnode);
              } else if (
                sectionPnode.getElementsByTagName("CHAR").length &&
                (sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                  .nodeValue == "(문항정보)" ||
                  sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                    .nodeValue == "(난이도)" ||
                  sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                    .nodeValue == "(행동영역)" ||
                  sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                    .nodeValue == "(내용영역)" ||
                  sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                    .nodeValue == "(단원코드)")
              ) {
                sectionMode = "i";
              } else {
                if (sectionMode == "q") {
                  questionSectionNode.appendChild(sectionPnode);
                } else if (sectionMode == "a") {
                  answerSectionNode.appendChild(sectionPnode);
                } else {
                  if (sectionPnode.getElementsByTagName("CHAR").length) {
                    console.log(
                      sectionPnode.getElementsByTagName("CHAR")[0].firstChild
                        .nodeValue + " is skiped..."
                    );
                  } else {
                    console.log("NOT EXIST CHAR NODE....");
                  }
                }
              }
            }

            q_index++;

            //console.log(questionSectionNode);
            //console.log(answerSectionNode); // (답) 부분은 API 부분 수정이 필요함.

            templateQuestionsHmlDoc
              .getElementsByTagName("BODY")[0]
              .appendChild(questionSectionNode);

            templateAnswersHmlDoc
              .getElementsByTagName("BODY")[0]
              .appendChild(answerSectionNode);

            // console.log(templateQuestionsHmlDoc);
            // console.log(templateAnswersHmlDoc);
          }
        }

        //code for IE
        // if (window.ActiveXObject) {
        //   templateQuestionsHml = this.$base64.encode(
        //     templateQuestionsHmlDoc.xml
        //   );

        //   templateAnswersHml = this.$base64.encode(templateAnswersHmlDoc.xml);
        // }
        // code for Chrome, Safari, Firefox, Opera, etc.
        templateQuestionsHml = this.$base64.encode(
          new XMLSerializer().serializeToString(templateQuestionsHmlDoc)
        );

        templateAnswersHml = this.$base64.encode(
          new XMLSerializer().serializeToString(templateAnswersHmlDoc)
        );

        //console.log(templateQuestionsHml);
        //console.log(templateAnswersHml);

        downloadedCount++;

        zip.file(
          "시험지.hwp",
          ("data:binary;base64," + templateQuestionsHml).substring(
            ("data:binary;base64," + templateQuestionsHml).indexOf("base64,") +
              "base64,".length
          ),
          { base64: true }
        );

        zip.file(
          "해설지.hwp",
          ("data:binary;base64," + templateAnswersHml).substring(
            ("data:binary;base64," + templateAnswersHml).indexOf("base64,") +
              "base64,".length
          ),
          { base64: true }
        );

        zip.generateAsync({ type: "blob" }).then(function(blob) {
          saveAs(blob, "문제집.zip");
        });

        this.$EventBus.$emit(
          "generaionSnackBarToHome",
          downloadedCount + "파일이 다운로드 되었습니다."
        );
      });
    },
    setGeneratedQuestion(ref, index, html) {
      //console.log(ref + "/" + index + ":" + html);

      //console.log(document.getElementById(ref + index));
      setTimeout(function() {
        if (document.getElementById(ref + index))
          document.getElementById(ref + index).contentWindow.setHtml(html);
        //this.$refs[ref + q.qsno].contentWindow.setHtml(q.bodyhtml);
      }, 100);
    }
  }
};
</script>
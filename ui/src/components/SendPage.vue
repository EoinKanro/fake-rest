<template>
  <NavigationMenu/>
  <PageDiv>
    <div class="main-div-content">
      <h1 class="header">Send</h1>
      <h2 class="header">Request</h2>

      <form class="send-form" @submit="sendRequest">
        <div class="flex-form">
          <h3 class="header">Url</h3>
          <input type="text" v-model="requestUrl" placeholder="http://localhost:8080/example" class="url-input"/>
          <input type="submit" value="Send" class="configuration-button-full-text send-button">
        </div>

        <h3 class="header">Method</h3>
        <select v-model="requestMethod">
          <option>GET</option>
          <option>POST</option>
          <option>PUT</option>
          <option>DELETE</option>
          <option>HEAD</option>
          <option>PATCH</option>
          <option>OPTIONS</option>
          <option>TRACE</option>
        </select>

        <h3 class="header">Headers</h3>
        <table class="configuration-table">
          <thead>
            <tr>
              <th>Header</th>
              <th>Value</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(header, index) in requestHeaders" :key="index">
              <td>
                <input v-model="header.name"/>
              </td>
              <td>
                <input v-model="header.value"/>
              </td>
              <td>
                <div class="configuration-button-square" v-on:click="removeHeader(index)">
                  <p>x</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="configuration-button-square" v-on:click="addHeader">
          <p>+</p>
        </div>
        <br><br>

        <h3 class="header">Body</h3>
        <textarea v-model="requestBody"/>
      </form>

      <h2 class="header">Answer</h2>
      <h3 class="header">Status</h3>
      <input v-model="responseStatus" readonly/>
      <h3 class="header">Body</h3>
      <textarea v-model="responseBody" readonly/>

      <div v-if="responseHeaders.length > 0">
        <p class="header">Headers</p>
        <table class="configuration-table">
          <thead>
          <tr>
            <th>Header</th>
            <th>Value</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(header, index) in responseHeaders" :key="index">
            <td>
              <input v-model="header.name"/>
            </td>
            <td>
              <input v-model="header.value"/>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="configuration-button-full-text" v-on:click="clearData">
        <p>Clear</p>
      </div>
    </div>
  </PageDiv>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
import PageDiv from "@/components/PageDiv";
export default {
  name: "SendPage",
  components: {PageDiv, NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body',

      requestUrl: '',
      requestMethod: 'GET',
      requestHeaders: [],
      requestBody: '',

      responseStatus: 0,
      responseHeaders: [],
      responseBody: ''
    }
  },
  methods: {
    async sendRequest() {
      try {
        let prepareHeaders = {}
        for (let i = 0; i < this.requestHeaders; i++) {
          let header = this.requestHeaders[i]
          prepareHeaders[header.name] = header.value
        }

        let requestMeta = {
          method: this.requestMethod,
          headers: prepareHeaders
        }
        if (this.requestBody && this.requestBody.length > 0) {
          requestMeta.body = this.requestBody
        }
        let response = await fetch(this.requestUrl, requestMeta)

        this.responseStatus = response.status
        this.responseBody = await response.text()

        this.responseHeaders = []
        for (let [key, value] of response.headers) {
          this.responseHeaders.push({
            name: key,
            value: value
          })
        }
      } catch (e) {
        alert(e)
      }
    },

    removeHeader(index) {
      this.requestHeaders.splice(index, 1)
    },

    addHeader() {
      this.requestHeaders.push({
        name: '',
        value: ''
      })
    },

    clearData() {
      this.requestUrl = ''
      this.requestMethod = 'GET'
      this.requestHeaders = []
      this.requestBody = ''
      this.responseStatus = 0
      this.responseHeaders = []
      this.responseBody = ''
    }
  }
}
</script>

<style scoped>
.configuration-button-square {
  float: left;
}

textarea {
  width: 100%;
}

select {
  font-size: min(1.8vw, 18px);
}

.header {
  margin-bottom: 0.2em;
}

.send-button {
  font-size: min(2.5vw, 25px);
}

.url-input {
  width: 46vw;
  font-size: min(1.8vw, 18px);

}
</style>
<template>
  <NavigationMenu/>
  <PageDiv>
    <div class="main-div-content">
      <h1 class="header">Configure</h1>

      <!-- Controllers -->
      <div class="flex-form">
        <h3 class="header">Controllers</h3>
        <div class="configuration-button-add configuration-button-square" v-on:click="clickAddController">
          <p>+</p>
        </div>
        <form class="add-config-form" @submit="submitApiController" v-if="confControllerVisible">
          <div class="configuration-button-square" v-on:click="confControllerVisible = !confControllerVisible">
            <p>x</p>
          </div>

          <p>Method</p>
          <select v-model="cMethod">
            <option>GET</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
            <option>HEAD</option>
            <option>PATCH</option>
            <option>OPTIONS</option>
            <option>TRACE</option>
          </select>

          <p>Function mode</p>
          <select v-model="cFunctionMode">
            <option>CREATE</option>
            <option>READ</option>
            <option>UPDATE</option>
            <option>DELETE</option>
            <option>GROOVY</option>
          </select>

          <p>Information Mode</p>
          <select v-model="cSaveInfoMode">
            <option>Static</option>
            <option>Collection</option>
          </select>

          <p>Uri</p>
          <input v-if="cSaveInfoMode === 'Static'" type="text" v-model="cUri" placeholder="/example"/>
          <input v-if="cSaveInfoMode === 'Collection'" type="text" v-model="cUri" placeholder="/example/{id-name}/{id-name2}"
                 v-on:input="calculateControllerIdsFromUri"/>

          <div v-if="cFunctionMode !== 'GROOVY'">
            <p>Answer</p>
            <input type="text" v-model="cAnswer" placeholder="Enter static answer"/>

            <div v-if="cFunctionMode === 'CREATE' && cSaveInfoMode === 'Collection'">
              <p>Generate id</p>
              <select v-model="cGenerateId">
                <option :value="true">Yes</option>
                <option :value="false">No</option>
              </select>
              <div v-if="cGenerateId === true">
                <p>Id patterns</p>
                <table class="configuration-table">
                  <thead>
                  <th>Id</th>
                  <th>Pattern</th>
                  </thead>
                  <tbody v-for="(id, index) in cIdParams" v-bind:key="index">
                  <td>{{ id }}</td>
                  <td>
                    <select v-model="cIdPatterns[index]">
                      <option>UUID</option>
                      <option>SEQUENCE</option>
                    </select>
                  </td>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div v-if="cFunctionMode === 'GROOVY'">
            <p>Groovy script</p>
            <textarea v-model="cGroovyScript"/>
          </div>

          <p>Delay ms</p>
          <input type="number" v-model="cDelayMs" placeholder="0"/>
          <br>
          <input type="submit" value="Submit" class="configuration-button-full-text">
        </form>
      </div>

      <table class="configuration-table">
        <thead>
          <tr>
            <th>Method</th>
            <th>Uri</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(controller, index) in controllers" :key="index">
            <td>{{ controller.method }}</td>
            <td>{{ controller.uri }}</td>
            <td>
              <div class="flex-form">
                <div class="configuration-button-square" v-on:click="updateApiController(index)">
                  <p>u</p>
                </div>
                <div class="configuration-button-square" v-on:click="removeApiController(index)">
                  <p>x</p>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Routers -->
      <div class="flex-form">
        <h3 class="header">Routers</h3>
        <div class="configuration-button-add configuration-button-square" v-on:click="clickAddRouter">
          <p>+</p>
        </div>
        <form class="add-config-form" @submit="submitApiRouter" v-if="confRouterVisible">
          <div class="configuration-button-square" v-on:click="confRouterVisible = !confRouterVisible">
            <p>x</p>
          </div>

          <p>Method</p>
          <select v-model="rMethod">
            <option>GET</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
            <option>HEAD</option>
            <option>PATCH</option>
            <option>OPTIONS</option>
            <option>TRACE</option>
          </select>

          <p>Uri</p>
          <input type="text" v-model="rUri" placeholder="/example/"/>
          <p>To Url</p>
          <input type="text" v-model="rToUrl" placeholder="/example"/>
          <br>
          <input type="submit" value="Submit" class="configuration-button-full-text">
        </form>
      </div>

      <table class="configuration-table">
        <thead>
        <tr>
          <th>Method</th>
          <th>Uri</th>
          <th>To Url</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(router, index) in routers" :key="index">
          <td>{{ router.method }}</td>
          <td>{{ router.uri }}</td>
          <td>{{ router.toUrl }}</td>
          <td>
            <div class="flex-form">
              <div class="configuration-button-square" v-on:click="updateApiRouter(index)">
                <p>u</p>
              </div>
              <div class="configuration-button-square" v-on:click="removeApiRouter(index)">
                <p>x</p>
              </div>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

    </div>
  </PageDiv>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
import PageDiv from "@/components/PageDiv";

export default {
  name: "ConfigurePage",
  components: {PageDiv, NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body',
      controllers: [],
      routers: [],
      confControllerVisible: false,
      confRouterVisible: false,

      cId: -1,
      cMethod: 'GET',
      cFunctionMode: 'CREATE',
      cSaveInfoMode: 'Static',
      cUri: '',
      cAnswer: '',
      cDelayMs: 0,
      cGenerateId: true,
      cIdParams: [],
      cIdPatterns: [],
      cGroovyScript: '',

      rId: -1,
      rUri: '',
      rMethod: 'GET',
      rToUrl: ''
    }
  },
  methods: {
    clickAddController() {
      this.confControllerVisible = !this.confControllerVisible;
      this.cId = -1
    },

    clickAddRouter() {
      this.confRouterVisible = !this.confRouterVisible;
      this.rId = -1
    },

    getCurrentHost() {
      return window.location.host
    },

    compareControllers(a, b) {
      if (a.uri === b.uri) {
        return a.id < b.id ? -1 : 1
      } else {
        return a.uri < b.uri ? -1 : 1
      }
    },

    compareRouters(a, b) {
      return a.id < b.id ? -1 : 1
    },

    findIndexById(id, collection) {
      if (collection) {
        for (let i = 0; i < collection.length; i++) {
          let config = collection[i]
          if (config.id === id) {
            return i
          }
        }
      }
    },

    setDefaultControllerValues() {
      this.cId = -1
      this.cMethod = 'GET'
      this.cFunctionMode = 'CREATE'
      this.cSaveInfoMode = 'Static'
      this.cUri = ''
      this.cAnswer = ''
      this.cDelayMs = 0
      this.cGenerateId = true
      this.cIdParams = []
      this.cIdPatterns = []
      this.cGroovyScript = ''
    },

    setDefaultRouterValues() {
      this.rId = -1
      this.rMethod = 'GET'
      this.rUri =''
      this.rToUrl = ''
    },

    logErrorApiResponse(status, responseData) {
      if (responseData) {
        alert(responseData.description)
      } else {
        alert('Something went wrong ' + status)
      }
    },

    async fetchApiResponseJson(response) {
      let responseData = null
      try {
        responseData = await response.json()
      } catch (e) {
        console.log(e)
      }
      return responseData
    },

    calculateControllerIdsFromUri() {
      if (this.cUri && this.cUri.length > 0) {
        this.cIdParams = this.cUri.match(new RegExp('(?<=\\{)[\\w]*(?=\\})', 'g'))
        if (!this.cIdParams) {
          this.cIdParams = []
        }

        let checkDuplicatesArray = []
        for (let i = 0; i < this.cIdParams.length; i++) {
          let id = this.cIdParams[i]
          if (checkDuplicatesArray.includes(id)) {
            alert(`Uri has duplicate ids: "${id}", fix it please`)
            this.cIdParams = []
            return
          } else {
            checkDuplicatesArray.push(id)
          }
        }

        for (let i = 0; i < this.cIdParams.length; i++) {
          this.cIdPatterns[i] = 'UUID'
        }
      }
    },

    async getApiControllers() {
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller'
      let response = await fetch(url, {method: 'GET'})
      let responseData = await this.fetchApiResponseJson(response)

      if (response.status >= 200 && response.status < 300) {
        this.controllers = responseData
        this.controllers.sort(this.compareControllers)
      } else {
        this.logErrorApiResponse(response.status, responseData)
      }
    },

    async submitApiController() {
      let idParamsPatterns = new Map()
      if (this.cId && this.cId >= 0) {
        let controllerIndex = this.findIndexById(this.cId, this.controllers)
        await this.removeApiController(controllerIndex)
      }

      for (let i = 0; i < this.cIdParams.length; i++) {
        idParamsPatterns.set(this.cIdParams[i], this.cIdPatterns[i])
      }

      let controller = {
        uri: this.cUri,
        method: this.cMethod,
        functionMode: this.cFunctionMode,
        delayMs: this.cDelayMs,
        idParams: this.cIdParams,
        generateId: this.cGenerateId,
        generateIdPatterns: idParamsPatterns
      }
      if (this.cAnswer && this.cAnswer.length > 0) {
        controller.answer = this.cAnswer
      }
      if (this.cGroovyScript && this.cGroovyScript.length > 0) {
        controller.groovyScript = this.cGroovyScript
      }

      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller'
      let response = await fetch(url,
          {method: 'POST',
            body: JSON.stringify(controller),
            headers: {
              'Content-Type': 'application/json'
            }})
      if (response.status < 200 || response.status >= 300) {
        let responseData = await this.fetchApiResponseJson(response)
        this.cId = -1
        this.logErrorApiResponse(response.status, responseData)
      } else {
        this.confControllerVisible = false
        this.setDefaultControllerValues()
        await this.getApiControllers()
      }
    },

    async updateApiController(index) {
      let controller = this.controllers.at(index)
      if (!controller) {
        alert('Something went wrong')
        return
      }

      this.cId = controller.id
      this.cMethod = controller.method
      this.cUri = controller.uri
      this.cFunctionMode = controller.functionMode
      this.calculateControllerIdsFromUri()
      if (this.cIdParams && this.cIdParams.length > 0) {
        this.cSaveInfoMode = 'Collection'
      } else {
        this.cSaveInfoMode = 'Static'
      }
      this.cAnswer = controller.answer
      this.cDelayMs = controller.delayMs
      this.cGenerateId = controller.generateId

      let idParamsPatterns = controller.generateIdPatterns
      if (idParamsPatterns && idParamsPatterns.size > 0) {
        this.cIdParams = idParamsPatterns.keys()
        this.cIdPatterns = idParamsPatterns.values()
      }
      this.cGroovyScript = controller.groovyScript
      this.confControllerVisible = true
    },

    async removeApiController(index) {
      let controller = this.controllers.at(index)
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller/' + controller.id

      let response = await fetch(url, {method: 'DELETE'})
      if (response.status < 200 || response.status > 300) {
        let responseData = await this.fetchApiResponseJson(response)
        this.logErrorApiResponse(response.status, responseData)
      }

      await this.getApiControllers()
    },

    async getApiRouters() {
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router'
      let response = await fetch(url, {method: 'GET'})
      let responseData = await this.fetchApiResponseJson(response)

      if (response.status >= 200 && response.status < 300) {
        this.routers = responseData
        this.routers = this.routers.sort(this.compareRouters)
      } else {
        this.logErrorApiResponse(response.status, responseData)
      }
    },

    async submitApiRouter() {
      if (this.rId && this.rId >= 0) {
        let routerIndex = this.findIndexById(this.rId, this.routers)
        await this.removeApiRouter(routerIndex)
      }

      let router = {
        uri: this.rUri,
        method: this.rMethod,
        toUrl: this.rToUrl
      }

      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router'
      let response = await fetch(url,
          {method: 'POST',
            body: JSON.stringify(router),
            headers: {
              'Content-Type': 'application/json'
            }})
      if (response.status < 200 || response.status >= 300) {
        let responseData = await this.fetchApiResponseJson(response)
        this.rId = -1
        this.logErrorApiResponse(response.status, responseData)
      } else {
        this.confRouterVisible = false
        this.setDefaultRouterValues()
        await this.getApiRouters()
      }
    },

    async updateApiRouter(index) {
      let router = this.routers.at(index)
      if (!router) {
        alert('Something went wrong')
        return
      }

      this.rId = router.id
      this.rMethod = router.method
      this.rUri = router.uri
      this.rToUrl = router.toUrl

      this.confRouterVisible = true
    },

    async removeApiRouter(index) {
      let router = this.routers.at(index)
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router/' + router.id

      let response = await fetch(url, {method: 'DELETE'})
      if (response.status < 200 || response.status > 300) {
        let responseData = await this.fetchApiResponseJson(response)
        this.logErrorApiResponse(response.status, responseData)
      }

      await this.getApiRouters()
    },

  },

  mounted: function() {
    this.getApiControllers()
    this.getApiRouters()
    this.setDefaultControllerValues()
    this.setDefaultRouterValues()
  }
}
</script>

<style scoped>
.configuration-button-add {
  margin-top: 1.5em !important;
}

.add-config-form {
  position: fixed;
  width: 63.5vw;
  left: 17vw;
  background: #9c8958;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  color: white;
  font-family: 'Lato', sans-serif;
  font-size: min(2vw, 20px);
  padding: 0.2vh 1vw 0.5vh 1vw;
}

.add-config-form input {
  font-size: min(1.8vw, 18px);
}

.add-config-form select {
  font-size: min(1.8vw, 18px);
}

.add-config-form p {
  margin-bottom: 0;
}
</style>
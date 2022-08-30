<template>
  <div class="navigation-menu">
    <div class="navigation-menu-open-button" v-on:click="navigationMenuOpen = !navigationMenuOpen">
      <div class="navigation-menu-open-block-container">
        <div class="navigation-menu-open-button-block"></div>
        <div class="navigation-menu-open-button-block"></div>
        <div class="navigation-menu-open-button-block"></div>
      </div>
    </div>
    <transition name="navigation-menu-list-slide">
      <ul v-if="navigationMenuOpen" class="navigation-menu-list borders">
        <li class="navigation-menu-list-element">
          <router-link to="/configure">
            <button class="navigation-menu-list-element-button borders">Configure</button>
          </router-link>
        </li>
        <li class="navigation-menu-list-element">
          <router-link to="/send">
            <button class="navigation-menu-list-element-button borders">Send</button>
          </router-link>
        </li>
        <li class="navigation-menu-list-element">
          <a href="https://github.com/ivanrosw/fake-rest" class="navigation-menu-list-element-button borders">Github</a>
        </li>
      </ul>
    </transition>
  </div>
</template>

<script>
export default {
  name: "NavigationMenu",
  data() {
    return {
      navigationMenuOpen: false
    }
  }
}
</script>

<style scoped>
.navigation-menu {
  display: block;
  position: fixed;
  left: 86vw;
  height: 90vh;
  width: 30%;
  z-index: 99999;
}

.navigation-menu-open-button {
  background: #4F452BFF;
  height: min(5vw, 50px);
  width: min(5vw, 50px);
  margin-top: 0.4vh;
  margin-left: 9vw;
  margin-bottom: 2vw;
  z-index: 199999;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  transition: box-shadow 600ms ease-out 100ms;
}

.navigation-menu-open-button:hover {
  box-shadow: 1em 0 2em #746641FF;
}

.navigation-menu-open-block-container {
  height: 100%;
  width: 100%;
}

.navigation-menu-open-button-block {
  height: min(0.7vw, 7px);
  width: min(4vw, 40px);
  margin-top: min(0.7vw, 7px);
  margin-left: min(0.5vw, 5px);
  background: rgb(255, 255, 255);
}

.navigation-menu-list {
  padding: 0;
  margin: 0;
  height: 40%;
  width: 100%;
  background-image: linear-gradient(to right bottom, #2c2617, #4f452b, #746641, #9c8958, #c6ae70);
}

.navigation-menu-list-slide-enter-active {
  animation: navigation-menu-list-slide-in 0.8s;
}

@keyframes navigation-menu-list-slide-in {
  from {
    margin-left: 50%;
  }
  to {
    margin-left: 0;
  }
}

.navigation-menu-list-slide-leave-active {
  animation: navigation-menu-list-slide-in-out 1s;
}

@keyframes navigation-menu-list-slide-in-out {
  from {
    margin-left: 0;
  }
  to {
    margin-left: 100%;
  }
}

.navigation-menu-list-element {
  list-style-type: none;
}

.navigation-menu-list-element-button {
  background: none;
  cursor: pointer;
  width: 100%;
  transition: background-color 600ms ease-out 100ms;
  font-family: 'Lato', sans-serif;
  font-size: min(2.2vw, 30px);
  text-align: left;
  color: white;
  padding: 0.25em 0 0.25em 0.4em;
}

a {
  display: list-item;
  text-decoration: none;
}

.navigation-menu-list-element-button:hover {
  background: rgba(0,0,0,0.33);
}

.borders {
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 2em;
}
</style>
<template>
  <div class="main-div">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "PageDiv"
}
</script>

<style>
.main-div {
  min-height: 100vh;
  height: 100%;
  margin-left: 15%;
  margin-right: 15%;
  padding-left: 3%;
  padding-right: 3%;
  background: white;
  border: 0.4em #907C46;
  border-left-style: solid;
  border-right-style: solid;
}

.main-div-content {
  padding-top: 5%;
}

.header {
  font-family: 'Merriweather', serif;
  font-weight: bold;
}

h1 {
  font-size: min(4vw, 40px);
}

h2 {
  font-size: min(3vw, 30px);
}

h3 {
  font-size: min(2vw, 20px);
}

.flex-form {
  display: flex;
  justify-content: space-between;
}

.configuration-table {
  border-collapse: collapse;
  width: 100%;
}

.configuration-table td, .configuration-table th {
  border: 1px solid #ddd;
  padding: 8px;
}

.configuration-table tr:nth-child(odd){background: rgba(255, 255, 255, 1);}

.configuration-table tr:nth-child(even){background: rgba(240, 240, 240, 1);}

.configuration-table tr:hover {background: rgba(79, 69, 43, 0.1);}

.configuration-table th {
  text-align: left;
  background-color: #4f452b;
  color: white;
  font-family: 'Merriweather', serif;
  font-weight: normal;
  font-size: min(1.8vw, 18px);
}

.configuration-table td {
  text-align: left;
  font-family: 'Lato', sans-serif;
  font-weight: lighter;
  font-size: min(1.7vw, 17px);
}

.configuration-table td:last-child {
  width: 1%;
}

.configuration-table input {
  font-size: min(1.8vw, 18px);
  width: 25vw;
}

.configuration-button-square{
  margin-top: 0.4em;
  background-color: #4f452b;
  transition: background-color 600ms ease-out 100ms;
  float: right;
  height: min(3vw, 30px);
  width: min(3vw, 30px);
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  cursor: pointer;
}

.configuration-button-square:hover {
  background-color: rgba(79, 69, 43, 0.6);
}

.configuration-button-square p {
  margin: 0;
  padding: 0;
  line-height: min(2.8vw, 28px);
  text-align: center;
  color: white;
  font-size: min(3vw, 30px);
  font-family: 'Lato', sans-serif;
}

.configuration-button-full-text {
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  background-color: #4f452b;
  color: white;
  cursor: pointer;
  margin-top: 0.5em;
  width: fit-content;
  height: fit-content;
}

.configuration-button-full-text:hover {
  background-color: rgba(79, 69, 43, 0.6);
}

.configuration-button-full-text p {
  margin: 0;
  padding: 0.1em 0.3em 0.1em 0.3em;
  font-size: min(2vw, 20px);
}
</style>
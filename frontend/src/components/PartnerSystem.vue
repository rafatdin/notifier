<template>
    <div class="service">
        <!--<h1>{{ msg }}</h1>
        <h2>REST service call results</h2>
        <button @click="callRestService()">CALL Spring Boot REST backend service</button>

        <h4>Backend response: {{ response }}</h4>-->

        <b-container>
            <b-row>
                <b-col cols="6">
                    <b-form @submit="onSubmit" @reset="onReset" v-if="show">
                        <b-form-group id="partnerNameGroup"
                                      horizontal :label-cols="3"
                                      label="Name:" label-for="partnerName"
                                      description="System name, underscore, environment">
                            <b-form-input id="partnerName"
                                          type="text"
                                          v-model="partner.name"
                                          required
                                          placeholder="Panel_PRODUCTION"/>
                        </b-form-group>
                        <b-form-group id="partnerHookGroup"
                                      horizontal :label-cols="3"
                                      label="Slack hook path"
                                      label-for="partnerHook">
                            <b-form-input id="partnerHook"
                                          type="text"
                                          v-model="partner.hook"
                                          required
                                          placeholder="https://hooks.slack.com/services/...">
                            </b-form-input>
                        </b-form-group>
                        <b-form-group id="partnerLogPathGroup"
                                      horizontal :label-cols="3"
                                      label="Path to log"
                                      label-for="partnerLogPath">
                            <b-form-input id="partnerLogPath"
                                          type="text"
                                          v-model="partner.log_path"
                                          required
                                          placeholder="/opt/partner/logs/errors.log">
                            </b-form-input>
                        </b-form-group>

                        <b-row>
                            <b-col>
                                <b-form-group id="partnerUserGroup"
                                          label="Server user"
                                          label-for="partnerUser">
                                <b-form-input id="partnerUser"
                                              type="text"
                                              v-model="partner.user"
                                              required
                                              placeholder="root">
                                </b-form-input>
                            </b-form-group>
                            </b-col>
                            <b-col>
                                <b-form-group id="partnerHostGroup"
                                          label="Server host"
                                          label-for="partnerHost">
                                <b-form-input id="partnerHost"
                                              type="text"
                                              v-model="partner.host"
                                              required
                                              placeholder="192.168.0.1">
                                </b-form-input>
                            </b-form-group>
                            </b-col>
                            <b-col>
                                <b-form-group id="partnerPasswordGroup"
                                      label="Server password"
                                      label-for="partnerPassword">
                            <b-form-input id="partnerPassword"
                                          type="text"
                                          v-model="partner.password"
                                          required
                                          placeholder="mySuperSecretPassword">
                            </b-form-input>
                        </b-form-group>
                            </b-col>
                        </b-row>

                        <b-button type="submit" variant="outline-primary">Submit</b-button>
                        <b-button type="reset" variant="outline-danger">Reset</b-button>
                    </b-form>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    // import axios from 'axios'
    import {AXIOS} from './http-common'

    export default {
        name: 'service',

        data() {
            return {
                msg: 'HowTo call REST-Services:',
                response: [],
                errors: [],
                show: true,
                partner: {
                    name: "",
                    hook: "",
                    log_path: "",
                    user: "",
                    host: "",
                    password: ""
                }
            }
        },
        methods: {
            onSubmit(evt) {
                evt.preventDefault();
                AXIOS.post(`/partner`, this.partner, {
                    headers:{
                        "Content-type":"application/json"
                    },
                })
                    .then(response => {
                        // JSON responses are automatically parsed.
                        this.response = response.data
                        if(response.status == 200){
                            alert(`Successfully created: \n ${response.data}`)
                        }
                        console.log(response.data)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            onReset(evt) {
                evt.preventDefault();
                /* Reset our form values */
                this.partner.name = ""
                this.partner.hook = ""
                this.partner.log_path = ""
                this.partner.user = ""
                this.partner.host = ""
                this.partner.password = ""
                /* Trick to reset/clear native browser form validation state */
                this.show = false;
                this.$nextTick(() => {
                    this.show = true
                });
            }
        }
    }

</script>


<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    h1, h2 {
        font-weight: normal;
    }

    ul {
        list-style-type: none;
        padding: 0;
    }

    li {
        display: inline-block;
        margin: 0 10px;
    }

    a {
        color: #42b983;
    }
</style>
